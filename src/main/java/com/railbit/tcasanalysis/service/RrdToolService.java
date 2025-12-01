package com.railbit.tcasanalysis.service;

import com.railbit.tcasanalysis.DTO.*;

import com.railbit.tcasanalysis.cactiRepo.CactiHostRepository;
import com.railbit.tcasanalysis.entity.cactiEntity.CactiHost;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class RrdToolService {

    private final CactiHostRepository cactiRepo;

    public RrdToolService(CactiHostRepository cactiRepo) {
        this.cactiRepo = cactiRepo;
    }

    public TrafficData fetchTraffic(String rrdFilePath, Long startDate, Long endDate) throws Exception {
        String cmd = "\"C:\\rrdtool\\rrdtool.exe\" fetch \"" + rrdFilePath + "\" AVERAGE";
        Process p = Runtime.getRuntime().exec(cmd);
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));

        List<Long> timestamps = new ArrayList<>();
        List<List<Double>> values = new ArrayList<>();
        List<String> dsNames = new ArrayList<>();

        boolean header = false;
        String line;

        while ((line = r.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;

            if (!header && !line.contains(":")) {
                dsNames = Arrays.asList(line.split("\\s+"));
                header = true;
                continue;
            }

            if (!line.contains(":")) continue;

            String[] parts = line.split(":");
            long ts = Long.parseLong(parts[0].trim());

            String[] raw = parts[1].trim().split("\\s+");
            List<Double> row = new ArrayList<>();

            for (String v : raw) {
                row.add(v.equals("nan") ? null : Double.parseDouble(v));
            }

            if (row.stream().anyMatch(Objects::nonNull)) {
                timestamps.add(ts);
                values.add(row);
            }
        }

        // -----------------------------
        // APPLY FILTER (startDate, endDate)
        // -----------------------------
        if (startDate != null || endDate != null) {
            List<Long> filteredTs = new ArrayList<>();
            List<List<Double>> filteredValues = new ArrayList<>();

            for (int i = 0; i < timestamps.size(); i++) {
                long ts = timestamps.get(i);

                boolean afterStart = (startDate == null || ts >= startDate);
                boolean beforeEnd  = (endDate == null || ts <= endDate);

                if (afterStart && beforeEnd) {
                    filteredTs.add(ts);
                    filteredValues.add(values.get(i));
                }
            }

            if (!filteredTs.isEmpty()) {
                timestamps = filteredTs;
                values = filteredValues;
            }
        }

        TrafficData d = new TrafficData();
        d.setTimestamps(timestamps);
        d.setValues(values);
        d.setDsNames(dsNames);
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
