package com.railbit.tcasanalysis.service.alertManagementService;



import com.railbit.tcasanalysis.repository.alertManagementRepo.EmergencyMonitoringRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class EmergencyMonitoringService {

    @Autowired
    private EmergencyMonitoringRepository repo;

    public Map<String, Object> getEmergencyMonitoring() {

        List<Object[]> counts = Optional.ofNullable(repo.getCriticalCounts())
                .orElse(new ArrayList<>());

        List<Map<String, Object>> cards = new ArrayList<>();

        for (Object[] row : counts) {
            Map<String, Object> card = new HashMap<>();

            String category = row[0] != null ? row[0].toString() : "Unknown";
            int count = row[1] != null ? ((Number) row[1]).intValue() : 0;

            card.put("label", category);
            card.put("value", count);

            cards.add(card);
        }

        List<Object[]> rows = Optional.ofNullable(repo.getTableData())
                .orElse(new ArrayList<>());

        List<Map<String, Object>> table = new ArrayList<>();

        for (Object[] r : rows) {

            String ticketStatus = r[6] != null ? r[6].toString().toUpperCase() : "";

            String notificationStatus =
                    ("OPEN".equals(ticketStatus) || "CLOSED".equals(ticketStatus))
                            ? "Sent"
                            : "Pending";

            Map<String, Object> rowMap = new HashMap<>();

            rowMap.put("time", r[0] != null ? r[0] : "");
            rowMap.put("locoNo", r[1] != null ? r[1] : "");
            rowMap.put("stationName", r[2] != null ? r[2] : "");
            rowMap.put("eventType", r[3] != null ? r[3] : "");
            rowMap.put("category", r[4] != null ? r[4] : "");
            rowMap.put("severity", r[5] != null ? r[5] : "");
            rowMap.put("notificationStatus", notificationStatus);

            table.add(rowMap);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("cards", cards);
        response.put("table", table);

        return response;
    }
}