package com.railbit.tcasanalysis.service.alertManagementService;

import com.railbit.tcasanalysis.repository.alertManagementRepo.HourlySeverityTrendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HourlySeverityTrendService {

    @Autowired
    private HourlySeverityTrendRepository repo;

    public Map<String, Object> getHourlyTrend() {

        List<Object[]> results = Optional.ofNullable(repo.getHourlyTrendLast1Hour())
                .orElse(new ArrayList<>());

        Map<Integer, Map<String, Integer>> hourMap = new TreeMap<>();

        for (Object[] row : results) {

            int hour = row[0] != null ? ((Number) row[0]).intValue() : 0;
            String severity = row[1] != null ? row[1].toString() : "";
            int count = row[2] != null ? ((Number) row[2]).intValue() : 0;

            hourMap.putIfAbsent(hour, new HashMap<>());
            hourMap.get(hour).put(severity, count);
        }

        List<String> labels = new ArrayList<>();
        List<Integer> critical = new ArrayList<>();
        List<Integer> warning = new ArrayList<>();
        List<Integer> medium = new ArrayList<>();

        for (int hr : hourMap.keySet()) {

            int hour12 = hr % 12 == 0 ? 12 : hr % 12;
            String ampm = hr < 12 ? "AM" : "PM";

            String label = String.format("%02d:00 %s", hour12, ampm);
            labels.add(label);

            Map<String, Integer> sevMap = hourMap.get(hr);

            critical.add(sevMap.getOrDefault("CRITICAL", 0));
            warning.add(sevMap.getOrDefault("WARNING", 0));
            medium.add(sevMap.getOrDefault("MEDIUM", 0));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("labels", labels);

        List<Map<String, Object>> datasets = new ArrayList<>();
        datasets.add(dataset("Critical", "#ff0000", critical));
        datasets.add(dataset("Warning", "#ff9800", warning));
        datasets.add(dataset("Medium", "#2196f3", medium));

        response.put("datasets", datasets);

        return response;
    }

    // ✅ SAFE helper
    private Map<String, Object> dataset(String label, String color, List<Integer> data) {

        Map<String, Object> map = new HashMap<>();
        map.put("label", label != null ? label : "");
        map.put("data", data != null ? data : new ArrayList<>());
        map.put("borderColor", color != null ? color : "");
        map.put("fill", false);

        return map;
    }
}