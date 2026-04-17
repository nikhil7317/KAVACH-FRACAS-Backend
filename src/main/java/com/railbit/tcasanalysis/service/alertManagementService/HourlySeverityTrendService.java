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

        List<Object[]> results = repo.getHourlyTrendLast1Hour();

        // Prepare structure
        Map<Integer, Map<String, Integer>> hourMap = new TreeMap<>();

        for (Object[] row : results) {
            int hour = ((Number) row[0]).intValue();
            String severity = (String) row[1];
            int count = ((Number) row[2]).intValue();

            hourMap.putIfAbsent(hour, new HashMap<>());
            hourMap.get(hour).put(severity, count);
        }

        List<String> labels = new ArrayList<>();
        List<Integer> critical = new ArrayList<>();
        List<Integer> warning = new ArrayList<>();
        List<Integer> medium = new ArrayList<>();

        for (int hr : hourMap.keySet()) {
            labels.add(String.format("%02d", hr));

            Map<String, Integer> sevMap = hourMap.get(hr);

            critical.add(sevMap.getOrDefault("CRITICAL", 0));
            warning.add(sevMap.getOrDefault("WARNING", 0));
            medium.add(sevMap.getOrDefault("MEDIUM", 0));
        }

        return Map.of(
                "labels", labels,
                "datasets", List.of(
                        dataset("Critical", "#ff0000", critical),
                        dataset("Warning", "#ff9800", warning),
                        dataset("Medium", "#2196f3", medium)
                )
        );
    }

    private Map<String, Object> dataset(String label, String color, List<Integer> data) {
        return Map.of(
                "label", label,
                "data", data,
                "borderColor", color,
                "fill", false
        );
    }
}
