package com.railbit.tcasanalysis.service.alertManagementService;

import com.railbit.tcasanalysis.repository.alertManagementRepo.AlertAnalysisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AlertAnalysisService {

    @Autowired
    private AlertAnalysisRepository repo;

    private static final List<String> PALETTE = List.of(
            "#ff6384", "#36a2eb", "#ffce56",
            "#4bc0c0", "#9966ff", "#ff9f40"
    );

    public List<Map<String, Object>> getAlertsCategoryWiseToday() {

        List<Object[]> rows = repo.countByAlertCategoryToday();

        List<Map<String, Object>> result = new ArrayList<>();
        int colourIdx = 0;

        for (Object[] row : rows) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", row[0]);
            item.put("count", ((Number) row[1]).longValue());
            item.put("colorCode", PALETTE.get(colourIdx % PALETTE.size()));
            result.add(item);
            colourIdx++;
        }

        return result;
    }

    public Map<String, Object> getResolutionStatusToday() {

        List<Object[]> rows = repo.getResolutionStatusToday();

        if (rows.isEmpty()) {
            return Map.of(
                    "total", 0,
                    "active", 0,
                    "pending", 0,
                    "resolved", 0,
                    "data", List.of()
            );
        }

        Object[] row = rows.get(0);

        long total = row[0] != null ? ((Number) row[0]).longValue() : 0;
        long pending = row[1] != null ? ((Number) row[1]).longValue() : 0;
        long resolved = row[2] != null ? ((Number) row[2]).longValue() : 0;

        long active = total;

        return Map.of(
                "total", total,
                "active", active,
                "pending", pending,
                "resolved", resolved,
                "data", List.of(
                        pie("Resolved", resolved, "#4CAF50"),
                        pie("Active", active, "#F44336"),
                        pie("Pending", pending, "#FF9800")
                )
        );
    }
    private Map<String, Object> pie(String label, long value, String color) {
        return Map.of(
                "label", label,
                "value", value,
                "color", color
        );
    }
}