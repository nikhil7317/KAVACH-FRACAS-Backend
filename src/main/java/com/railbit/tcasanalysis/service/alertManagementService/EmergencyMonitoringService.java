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

        // 🔹 Dynamic Cards
        List<Object[]> counts = repo.getCriticalCounts();

        List<Map<String, Object>> cards = new ArrayList<>();

        for (Object[] row : counts) {
            String category = row[0] != null ? row[0].toString() : "Unknown";
            int count = ((Number) row[1]).intValue();

            cards.add(Map.of(
                    "label", category,
                    "value", count
            ));
        }

        // 🔹 Table
        List<Object[]> rows = repo.getTableData();

        List<Map<String, Object>> table = new ArrayList<>();

        for (Object[] r : rows) {

            String ticketStatus = r[6] != null ? r[6].toString().toUpperCase() : null;

            String notificationStatus =
                    ("OPEN".equals(ticketStatus) || "CLOSED".equals(ticketStatus))
                            ? "Sent"
                            : "Pending";
            table.add(Map.of(
                    "time", r[0],
                    "locoNo", r[1],
                    "stationName", r[2],
                    "eventType", r[3],
                    "category", r[4],
                    "severity", r[5],
                    "notificationStatus", notificationStatus
            ));
        }

        return Map.of(
                "cards", cards,
                "table", table
        );
    }
}