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

    // ✅ SAFE
    public List<Map<String, Object>> getAlertsCategoryWiseToday() {

        List<Object[]> rows = Optional.ofNullable(repo.countByAlertCategoryToday())
                .orElse(new ArrayList<>());

        List<Map<String, Object>> result = new ArrayList<>();
        int colourIdx = 0;

        for (Object[] row : rows) {
            Map<String, Object> item = new LinkedHashMap<>();

            String name = row[0] != null ? row[0].toString() : "Unknown";
            long count = row[1] != null ? ((Number) row[1]).longValue() : 0;

            item.put("name", name);
            item.put("count", count);
            item.put("colorCode", PALETTE.get(colourIdx % PALETTE.size()));

            result.add(item);
            colourIdx++;
        }

        return result;
    }

    // ✅ SAFE (REMOVED Map.of)
    public Map<String, Object> getResolutionStatusToday() {

        List<Object[]> rows = Optional.ofNullable(repo.getResolutionStatusToday())
                .orElse(new ArrayList<>());

        Map<String, Object> response = new HashMap<>();

        if (rows.isEmpty()) {
            response.put("total", 0);
            response.put("active", 0);
            response.put("pending", 0);
            response.put("resolved", 0);
            response.put("data", new ArrayList<>());
            return response;
        }

        Object[] row = rows.get(0);

        long total = row[0] != null ? ((Number) row[0]).longValue() : 0;
        long pending = row[1] != null ? ((Number) row[1]).longValue() : 0;
        long resolved = row[2] != null ? ((Number) row[2]).longValue() : 0;

        response.put("total", total);
        response.put("active", total);
        response.put("pending", pending);
        response.put("resolved", resolved);

        List<Map<String, Object>> data = new ArrayList<>();
        data.add(pie("Resolved", resolved, "#4CAF50"));
        data.add(pie("Active", total, "#F44336"));
        data.add(pie("Pending", pending, "#FF9800"));

        response.put("data", data);

        return response;
    }

    // ✅ SAFE
    public Map<String, Object> getStationHeatmap() {

        List<Object[]> rows = Optional.ofNullable(repo.getStationHeatmap())
                .orElse(new ArrayList<>());

        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        LocalDate today = LocalDate.now();

        int isoDow = today.getDayOfWeek().getValue();
        LocalDate startOfWeek = today.minusDays(isoDow - 1);

        Map<String, String> dayDateMap = new LinkedHashMap<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = startOfWeek.plusDays(i);
            if (!date.isAfter(today)) {
                dayDateMap.put(days[i], date.toString());
            }
        }

        Map<String, int[]> map = new LinkedHashMap<>();

        for (Object[] r : rows) {
            String station = r[0] != null ? r[0].toString() : "";
            int mysqlDay = r[1] != null ? ((Number) r[1]).intValue() : 0;

            int index = (mysqlDay == 1) ? 6 : mysqlDay - 2;

            int count = r[3] != null ? ((Number) r[3]).intValue() : 0;

            map.putIfAbsent(station, new int[7]);
            map.get(station)[index] = count;
        }

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

        Map<String, Object> response = new HashMap<>();
        response.put("dates", dayDateMap);
        response.put("data", result);

        return response;
    }

    // ✅ SAFE
    public Map<String, Object> getCollisionVsSos() {

        List<Object[]> rows = Optional.ofNullable(repo.getCollisionVsSosMonthly())
                .orElse(new ArrayList<>());

        String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

        int[] collision = new int[12];
        int[] sos = new int[12];

        for (Object[] r : rows) {
            int month = r[0] != null ? ((Number) r[0]).intValue() : 1;

            collision[month - 1] = r[1] != null ? ((Number) r[1]).intValue() : 0;
            sos[month - 1] = r[2] != null ? ((Number) r[2]).intValue() : 0;
        }

        Map<String, Object> response = new HashMap<>();
        response.put("labels", months);

        List<Map<String, Object>> datasets = new ArrayList<>();
        datasets.add(dataset("Collision", "#9C27B0", collision));
        datasets.add(dataset("SOS", "#F44336", sos));

        response.put("datasets", datasets);

        return response;
    }

    // ✅ SAFE helper
    private Map<String, Object> pie(String label, long value, String color) {
        Map<String, Object> map = new HashMap<>();
        map.put("label", label != null ? label : "");
        map.put("value", value);
        map.put("color", color != null ? color : "");
        return map;
    }

    // ✅ SAFE helper
    private Map<String, Object> dataset(String label, String color, int[] data) {
        Map<String, Object> map = new HashMap<>();
        map.put("label", label != null ? label : "");
        map.put("backgroundColor", color != null ? color : "");
        map.put("data", data != null ? data : new int[0]);
        return map;
    }
}