package com.railbit.tcasanalysis.service;

import com.railbit.tcasanalysis.DTO.*;

import com.railbit.tcasanalysis.cactiRepo.CactiHostRepository;
import com.railbit.tcasanalysis.entity.cactiEntity.CactiHost;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RrdToolService {

    private final CactiHostRepository cactiRepo;

    public RrdToolService(CactiHostRepository cactiRepo) {
        this.cactiRepo = cactiRepo;
    }


    public TrafficData fetchTraffic(String rrdFilePath, Long startDate, Long endDate) throws Exception {

        // 💡 FIX: Make dsNames a member of a new enclosing class or scope object
        // to allow the inner Parser class to modify it without the 'final' constraint.
        final class TrafficContext {
            List<Long> timestamps = new ArrayList<>();
            List<List<Double>> values = new ArrayList<>();
            List<String> dsNames = new ArrayList<>(); // Now a mutable field
        }
        TrafficContext ctx = new TrafficContext();

        // Helper to run external command and return stdout lines
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

        // --- Helper for parsing rrdtool output ---
        final class Parser {
            // Returns true if DS names were set from header
            boolean parse(List<String> out, boolean allowEmpty) {
                boolean headerFound = false;
                for (String line : out.stream().map(String::trim).filter(l -> !l.isEmpty()).collect(Collectors.toList())) {
                    if (line.contains(":")) {
                        // ... parsing logic remains the same, but now uses ctx ...
                        String[] parts = line.split(":");
                        try {
                            long tsSec = Long.parseLong(parts[0].trim());
                            long tsMs = tsSec * 1000L;
                            String[] raw = parts[1].trim().split("\\s+");
                            List<Double> row = Arrays.stream(raw)
                                    .map(v -> v.equals("nan") ? null : Double.parseDouble(v))
                                    .collect(Collectors.toList());

                            // ⚠️ Accessing and modifying ctx.dsNames
                            if (!ctx.dsNames.isEmpty() && row.size() < ctx.dsNames.size()) {
                                while (row.size() < ctx.dsNames.size()) row.add(null);
                            }

                            if (allowEmpty || row.stream().anyMatch(Objects::nonNull)) {
                                ctx.timestamps.add(tsMs);
                                ctx.values.add(row);
                            }
                        } catch (NumberFormatException ignored) {}
                    } else if (!headerFound) {
                        // ⚠️ This is the line that caused the original error, now fixed:
                        // Reassignment is valid on a mutable field of an outer class/scope object.
                        ctx.dsNames = Arrays.asList(line.split("\\s+"));
                        headerFound = true;
                    }
                }
                return headerFound;
            }
        }
        Parser parser = new Parser();

        // --- Rest of the logic ---

        // ---------- DEFAULT: show only SAME-DAY realtime data ----------
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

        // ---------- RANGE REQUEST: use XPORT (Cacti-like behavior) ----------
        long s = (startDate == null) ? (System.currentTimeMillis() / 1000L - 7 * 86400L) : (startDate > 10000000000L ? startDate / 1000 : startDate);
        long e = (endDate == null) ? (System.currentTimeMillis() / 1000L) : (endDate > 10000000000L ? endDate / 1000 : endDate);

        // Discover DS names via a quick header probe (fallback)
        try {
            // ... (Probe logic using ctx.dsNames for list check)
            String probeCmd = String.format("\"C:\\rrdtool\\rrdtool.exe\" fetch \"%s\" AVERAGE --resolution 300 --start %d --end %d",
                    rrdFilePath, Math.max(s - 10, s), Math.min(e + 10, e));

            List<String> probeOut = exec.run(probeCmd);
            for (String line : probeOut.stream().map(String::trim).filter(l -> !l.isEmpty() && !l.contains(":")).collect(Collectors.toList())) {
                ctx.dsNames = Arrays.asList(line.split("\\s+"));
                break;
            }
        } catch (Exception ignored) { /* continue — we'll still try xport */ }

        // If DS names still unknown, fallback to common traffic DS names
        if (ctx.dsNames.isEmpty()) {
            ctx.dsNames = Arrays.asList("traffic_in", "traffic_out");
        }

        // Build DEF and XPORT arguments dynamically
        String defArgs = ctx.dsNames.stream()
                .map(ds -> String.format("DEF:ds%d=\"%s\":%s:AVERAGE", ctx.dsNames.indexOf(ds), rrdFilePath, ds))
                .collect(Collectors.joining(" "));
        String xportArgs = ctx.dsNames.stream()
                .map(ds -> String.format("XPORT:ds%d:\"%s\"", ctx.dsNames.indexOf(ds), ds))
                .collect(Collectors.joining(" "));

        String xportCmd = String.format("\"C:\\rrdtool\\rrdtool.exe\" xport --start %d --end %d %s %s",
                s, e, defArgs, xportArgs);

        // Parse xport output
        parser.parse(exec.run(xportCmd), false);

        // If xport produced nothing, fallback
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
        } catch (Exception ignored) {}
        return 300;
    }

    public TrafficReportResponse fetchFullReport(String path, Integer deviceId, Long startDate, Long endDate) throws Exception {

        TrafficData t = fetchTraffic(path, startDate, endDate);

        // Apply timestamp filters if provided
        if (startDate != null || endDate != null) {
            List<Long> filteredTs = new ArrayList<>();
            List<List<Double>> filteredValues = new ArrayList<>();

            for (int i = 0; i < t.getTimestamps().size(); i++) {
                long ts = t.getTimestamps().get(i);

                // Conditions
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

        // Set final timestamps after filtering
        if (!t.getTimestamps().isEmpty()) {
            res.setFromTimestamp(t.getTimestamps().get(0));
            res.setToTimestamp(t.getTimestamps().get(t.getTimestamps().size()-1));
        }

        res.setPollingIntervalSeconds(getPollingInterval(path));

        // Compute report stats
        List<ReportData> list = new ArrayList<>();

        for (int i = 0; i < t.getDsNames().size(); i++) {
            List<Double> col = new ArrayList<>();
            for (List<Double> row : t.getValues()) {
                Double v = row.get(i);
                if (v != null) col.add(v);
            }

            if (col.isEmpty()) continue;

            ReportData rd = new ReportData();
            rd.setDataSourceName(t.getDsNames().get(i));
            rd.setCurrentValue(col.get(col.size() - 1));
            rd.setMinValue(Collections.min(col));
            rd.setMaxValue(Collections.max(col));
            rd.setAvgValue(col.stream().mapToDouble(d -> d).average().orElse(0));

            list.add(rd);
        }

        res.setReports(list);
        return res;
    }

}
