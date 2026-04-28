package com.railbit.tcasanalysis.service.alertManagementService;

import com.railbit.tcasanalysis.repository.alertManagementRepo.AlertAnalysisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    public Map<String, Object> getStationHeatmap() {

        List<Object[]> rows = repo.getStationHeatmap();

        // ✅ Changed: Mon → Sun order
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

        LocalDate today = LocalDate.now();

        // ✅ Find this week's Monday (ISO start of week)
        int isoDow = today.getDayOfWeek().getValue(); // Mon=1 ... Sun=7
        LocalDate startOfWeek = today.minusDays(isoDow - 1); // always lands on Monday

        // ✅ Only generate dates from Monday UP TO today
        Map<String, String> dayDateMap = new LinkedHashMap<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = startOfWeek.plusDays(i);
            if (!date.isAfter(today)) {
                dayDateMap.put(days[i], date.toString());
            }
        }

        // Fill station data
        Map<String, int[]> map = new LinkedHashMap<>();
        for (Object[] r : rows) {
            String station = r[0] != null ? r[0].toString() : "";
            int mysqlDay = ((Number) r[1]).intValue(); // MySQL DAYOFWEEK: 1=Sun,2=Mon...7=Sat

            // ✅ Remap MySQL day to Mon-based index (Mon=0 ... Sun=6)
            int index = (mysqlDay == 1) ? 6 : mysqlDay - 2; // Sun→6, Mon→0, Tue→1 ... Sat→5

            int count = ((Number) r[3]).intValue();
            map.putIfAbsent(station, new int[7]);
            map.get(station)[index] = count;
        }

        // Build result — only include days up to today
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, int[]> entry : map.entrySet()) {
            Map<String, Object> obj = new LinkedHashMap<>();
            obj.put("station", entry.getKey());
            int[] counts = entry.getValue();
            for (int i = 0; i < 7; i++) {
                if (dayDateMap.containsKey(days[i])) {
                    obj.put(days[i], counts[i]);
                }
            }
            result.add(obj);
        }

        return Map.of("dates", dayDateMap, "data", result);
    }
    private Map<String, Object> pie(String label, long value, String color) {
        return Map.of(
                "label", label,
                "value", value,
                "color", color
        );
    }

    public Map<String, Object> getCollisionVsSos() {

        List<Object[]> rows = repo.getCollisionVsSosMonthly();

        String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

        int[] collision = new int[12];
        int[] sos = new int[12];

        for (Object[] r : rows) {
            int month = ((Number) r[0]).intValue(); // 1-12

            collision[month - 1] = ((Number) r[1]).intValue();
            sos[month - 1] = ((Number) r[2]).intValue();
        }

        return Map.of(
                "labels", months,
                "datasets", List.of(
                        dataset("Collision", "#9C27B0", collision),
                        dataset("SOS", "#F44336", sos)
                )
        );
    }

    private Map<String, Object> dataset(String label, String color, int[] data) {
        return Map.of(
                "label", label,
                "backgroundColor", color,
                "data", data
        );
    }
}