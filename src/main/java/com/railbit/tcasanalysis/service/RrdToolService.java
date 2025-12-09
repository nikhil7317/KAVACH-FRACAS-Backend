package com.railbit.tcasanalysis.service;

import com.railbit.tcasanalysis.DTO.*;
import com.railbit.tcasanalysis.cactiRepo.CactiHostRepository;
import com.railbit.tcasanalysis.entity.cactiEntity.CactiHost;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Collectors;

@Service
public class RrdToolService {

    private final CactiHostRepository cactiRepo;

    public RrdToolService(CactiHostRepository cactiRepo) {
        this.cactiRepo = cactiRepo;
    }

    // --- Traffic fetch / rrd parsing logic (unchanged, only minor refactors) ---
    public TrafficData fetchTraffic(String rrdFilePath, Long startDate, Long endDate) throws Exception {

        final class TrafficContext {
            List<Long> timestamps = new ArrayList<>();
            List<List<Double>> values = new ArrayList<>();
            List<String> dsNames = new ArrayList<>();
        }
        TrafficContext ctx = new TrafficContext();

        class Exec {
            List<String> run(String cmd) throws Exception {
                Process p = Runtime.getRuntime().exec(cmd);
                try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                    List<String> out = r.lines().collect(Collectors.toList());
                    p.waitFor();
                    return out;
                }
            }
        }
        Exec exec = new Exec();

        final class Parser {
            boolean parse(List<String> out, boolean allowEmpty) {
                boolean headerFound = false;
                for (String line : out.stream().map(String::trim).filter(l -> !l.isEmpty()).collect(Collectors.toList())) {
                    if (line.contains(":")) {
                        String[] parts = line.split(":");
                        try {
                            long tsSec = Long.parseLong(parts[0].trim());
                            long tsMs = tsSec * 1000L;
                            String[] raw = parts[1].trim().split("\\s+");
                            List<Double> row = Arrays.stream(raw)
                                    .map(v -> v.equals("nan") ? null : Double.parseDouble(v))
                                    .collect(Collectors.toList());

                            if (!ctx.dsNames.isEmpty() && row.size() < ctx.dsNames.size()) {
                                while (row.size() < ctx.dsNames.size()) row.add(null);
                            }

                            if (allowEmpty || row.stream().anyMatch(Objects::nonNull)) {
                                ctx.timestamps.add(tsMs);
                                ctx.values.add(row);
                            }
                        } catch (NumberFormatException ignored) {
                        }
                    } else if (!headerFound) {
                        ctx.dsNames = Arrays.asList(line.split("\\s+"));
                        headerFound = true;
                    }
                }
                return headerFound;
            }
        }
        Parser parser = new Parser();

        // Default: same-day realtime data
        if (startDate == null && endDate == null) {
            long nowSec = System.currentTimeMillis() / 1000L;
            long startOfDaySec = ZonedDateTime.now().toLocalDate().atStartOfDay(ZonedDateTime.now().getZone()).toEpochSecond();

            String cmd = String.format("\"C:\\rrdtool\\rrdtool.exe\" fetch \"%s\" AVERAGE --resolution 300 --start %d --end %d",
                    rrdFilePath, startOfDaySec, nowSec);

            parser.parse(exec.run(cmd), false);

            TrafficData d = new TrafficData();
            d.setTimestamps(ctx.timestamps);
            d.setValues(ctx.values);
            d.setDsNames(ctx.dsNames);
            return d;
        }

        // Range request: XPORT like Cacti
        long s = (startDate == null) ? (System.currentTimeMillis() / 1000L - 7 * 86400L) : (startDate > 10000000000L ? startDate / 1000 : startDate);
        long e = (endDate == null) ? (System.currentTimeMillis() / 1000L) : (endDate > 10000000000L ? endDate / 1000 : endDate);

        try {
            String probeCmd = String.format("\"C:\\rrdtool\\rrdtool.exe\" fetch \"%s\" AVERAGE --resolution 300 --start %d --end %d",
                    rrdFilePath, Math.max(s - 10, s), Math.min(e + 10, e));

            List<String> probeOut = exec.run(probeCmd);
            for (String line : probeOut.stream().map(String::trim).filter(l -> !l.isEmpty() && !l.contains(":")).collect(Collectors.toList())) {
                ctx.dsNames = Arrays.asList(line.split("\\s+"));
                break;
            }
        } catch (Exception ignored) { /* continue — we'll still try xport */ }

        if (ctx.dsNames.isEmpty()) {
            ctx.dsNames = Arrays.asList("traffic_in", "traffic_out");
        }

        String defArgs = ctx.dsNames.stream()
                .map(ds -> String.format("DEF:ds%d=\"%s\":%s:AVERAGE", ctx.dsNames.indexOf(ds), rrdFilePath, ds))
                .collect(Collectors.joining(" "));
        String xportArgs = ctx.dsNames.stream()
                .map(ds -> String.format("XPORT:ds%d:\"%s\"", ctx.dsNames.indexOf(ds), ds))
                .collect(Collectors.joining(" "));

        String xportCmd = String.format("\"C:\\rrdtool\\rrdtool.exe\" xport --start %d --end %d %s %s",
                s, e, defArgs, xportArgs);

        parser.parse(exec.run(xportCmd), false);

        if (ctx.timestamps.isEmpty()) {
            String fallbackCmd = String.format("\"C:\\rrdtool\\rrdtool.exe\" fetch \"%s\" AVERAGE --resolution 1800 --start %d --end %d",
                    rrdFilePath, s, e);
            parser.parse(exec.run(fallbackCmd), true);
        }

        TrafficData d = new TrafficData();
        d.setTimestamps(ctx.timestamps);
        d.setValues(ctx.values);
        d.setDsNames(ctx.dsNames);
        return d;
    }

    private Integer getPollingInterval(String path) {
        try {
            String cmd = "\"C:\\rrdtool\\rrdtool.exe\" info \"" + path + "\"";
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String l;
            while ((l = br.readLine()) != null) {
                if (l.trim().startsWith("step")) {
                    return Integer.parseInt(l.split("=")[1].trim());
                }
            }
        } catch (Exception ignored) {
        }
        return 300;
    }

    // --- Main report builder (uses DS_META for production-grade conversions) ---
    public TrafficReportResponse fetchFullReport(String path, Integer deviceId, Long startDate, Long endDate) throws Exception {

        TrafficData t = fetchTraffic(path, startDate, endDate);

        // timestamp filtering
        if (startDate != null || endDate != null) {
            List<Long> filteredTs = new ArrayList<>();
            List<List<Double>> filteredValues = new ArrayList<>();

            for (int i = 0; i < t.getTimestamps().size(); i++) {
                long ts = t.getTimestamps().get(i);
                boolean afterStart = (startDate == null || ts >= startDate);
                boolean beforeEnd = (endDate == null || ts <= endDate);

                if (afterStart && beforeEnd) {
                    filteredTs.add(ts);
                    filteredValues.add(t.getValues().get(i));
                }
            }

            t.setTimestamps(filteredTs);
            t.setValues(filteredValues);
        }

        TrafficReportResponse res = new TrafficReportResponse();

        // Host details
        CactiHost host = cactiRepo.findById(deviceId).orElse(null);
        if (host != null) {
            CactiHostDetails wrapper = new CactiHostDetails();
            wrapper.setHost(host);
            res.setCactiHost(wrapper);
        }

        // Set timestamps
        if (!t.getTimestamps().isEmpty()) {
            res.setFromTimestamp(t.getTimestamps().get(0));
            res.setToTimestamp(t.getTimestamps().get(t.getTimestamps().size() - 1));
        }

        res.setPollingIntervalSeconds(getPollingInterval(path));

        // Compute report stats using DS_META converters
        List<ReportData> list = new ArrayList<>();

        for (int i = 0; i < t.getDsNames().size(); i++) {
            String ds = t.getDsNames().get(i);
            List<Double> col = new ArrayList<>();
            for (List<Double> row : t.getValues()) {
                Double v = row.get(i);
                if (v != null) col.add(v);
            }

            if (col.isEmpty()) continue;

            // find metadata match by substring (first match)
            DsMeta meta = DS_META.entrySet().stream()
                    .filter(e -> ds.toLowerCase().contains(e.getKey()))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElse(null);

            if (meta == null) {
                // fallback to detectUnit and identity converter
                String fallbackUnit = detectUnit(ds);
                meta = new DsMeta(fallbackUnit, v -> v);
            }

            ReportData rd = new ReportData();
            rd.setDataSourceName(ds);

            // apply converter (current/min/max/avg)
            double rawCurrent = col.get(col.size() - 1);
            double rawMin = Collections.min(col);
            double rawMax = Collections.max(col);
            double rawAvg = col.stream().mapToDouble(d -> d).average().orElse(0);

            double current = safeApply(meta.converter, rawCurrent);
            double min = safeApply(meta.converter, rawMin);
            double max = safeApply(meta.converter, rawMax);
            double avg = safeApply(meta.converter, rawAvg);

            rd.setCurrentValue(round4(current));
            rd.setMinValue(round4(min));
            rd.setMaxValue(round4(max));
            rd.setAvgValue(round4(avg));

            rd.setUnit(meta.unit != null ? meta.unit : "Unknown");

            list.add(rd);
        }

        res.setReports(list);
        return res;
    }

    // safe apply helper (handles NaN / infinite)
    private static double safeApply(DoubleUnaryOperator op, double v) {
        try {
            double r = op.applyAsDouble(v);
            if (Double.isNaN(r) || Double.isInfinite(r)) return 0.0;
            return r;
        } catch (Exception ex) {
            return 0.0;
        }
    }

    private String detectUnit(String dsName) {
        String ds = dsName.toLowerCase();

        // Traffic graphs
        if (ds.contains("traffic") || ds.endsWith("_in") || ds.endsWith("_out")) {
            return "bps";
        }

        // Uptime
        if (ds.contains("uptime")) {
            return "minutes";
        }

        // Polling time (from Cacti poller stats)
        if (ds.contains("polling_time") || ds.contains("polling") || ds.contains("ping_time")) {
            return "seconds";
        }

        // Voltage
        if (ds.contains("volt") || ds.contains("voltage")) {
            return "Volt";
        }

        // Current / Amps
        if (ds.contains("amp") || ds.contains("current")) {
            return "Ampere";
        }

        // Temperature
        if (ds.contains("temp")) {
            return "°C";
        }

        return "Unknown";
    }

    private double round4(double v) {
        return Math.round(v * 10000.0) / 10000.0;
    }

    // ---------- DS metadata map & helper class ----------
    private static final Map<String, DsMeta> DS_META = new LinkedHashMap<>();

    static {
        // key = substring to match inside DS name (lowercase)
        // uptime: centiseconds -> seconds -> minutes
        DS_META.put("uptime", new DsMeta("minutes", v -> (v * 0.01) / 60.0));

        // polling_time and variants are already in seconds
        DS_META.put("polling_time", new DsMeta("seconds", v -> v));
        DS_META.put("polling", new DsMeta("seconds", v -> v));

        // traffic: bps (we keep raw value here, UI can autoscale to Kbps/Mbps)
        DS_META.put("traffic_in", new DsMeta("bps", v -> v));
        DS_META.put("traffic_out", new DsMeta("bps", v -> v));
        DS_META.put("traffic", new DsMeta("bps", v -> v));

        // typical sensors
        DS_META.put("volt", new DsMeta("Volt", v -> v));
        DS_META.put("voltage", new DsMeta("Volt", v -> v));
        DS_META.put("amp", new DsMeta("Ampere", v -> v));
        DS_META.put("current", new DsMeta("Ampere", v -> v));
        DS_META.put("temp", new DsMeta("°C", v -> v));
        DS_META.put("temperature", new DsMeta("°C", v -> v));
    }

    private static class DsMeta {
        String unit;
        DoubleUnaryOperator converter;

        public DsMeta(String unit, DoubleUnaryOperator converter) {
            this.unit = unit;
            this.converter = converter;
        }
    }

}
