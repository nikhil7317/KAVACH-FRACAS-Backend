package com.railbit.tcasanalysis.service;

import com.railbit.tcasanalysis.DTO.TrafficData;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class RrdToolService {

    public TrafficData fetchTraffic(String rrdFilePath) throws Exception {

        String cmd = "\"C:\\rrdtool\\rrdtool.exe\" fetch \"" + rrdFilePath + "\" AVERAGE";
        Process process = Runtime.getRuntime().exec(cmd);

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
        );

        List<Long> timestamps = new ArrayList<>();
        List<List<Double>> values = new ArrayList<>();
        List<String> dsNames = new ArrayList<>();

        String line;
        boolean headerRead = false;

        while ((line = reader.readLine()) != null) {

            line = line.trim();
            if (line.isEmpty()) continue;

            // Read DS names (first non-empty line without ':')
            if (!headerRead && !line.contains(":")) {
                dsNames = Arrays.asList(line.trim().split("\\s+"));
                headerRead = true;
                continue;
            }

            if (!line.contains(":")) continue;

            String[] parts = line.split(":");
            long timestamp = Long.parseLong(parts[0].trim());

            String[] rawVals = parts[1].trim().split("\\s+");
            List<Double> row = new ArrayList<>();

            for (String v : rawVals) {
                if (v.equals("nan"))
                    row.add(null);
                else
                    row.add(Double.parseDouble(v));
            }

            // Store the row ONLY if at least 1 value is not null
            if (row.stream().anyMatch(Objects::nonNull)) {
                timestamps.add(timestamp);
                values.add(row);
            }
        }

        TrafficData data = new TrafficData();
        data.setTimestamps(timestamps);
        data.setValues(values);
        data.setDsNames(dsNames);

        return data;
    }
}

