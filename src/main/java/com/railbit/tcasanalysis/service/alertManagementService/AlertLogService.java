package com.railbit.tcasanalysis.service.alertManagementService;

import com.railbit.tcasanalysis.repository.alertManagementRepo.AlertLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AlertLogService {

    @Autowired
    private AlertLogRepository repo;

    public List<Map<String, Object>> getAlertLogs() {

        List<Object[]> results = repo.getAlertLogs();

        return results.stream().map(row -> {

            String ticketStatus = row[5] != null ? row[5].toString().toUpperCase() : null;

            String notificationStatus =
                    ("OPEN".equals(ticketStatus) || "CLOSED".equals(ticketStatus))
                            ? "Sent"
                            : "Pending";

            return Map.of(
                    "locoNo", row[0],
                    "stationName", row[1],
                    "alertMessage", row[2],
                    "severity", row[3],
                    "eventTime", row[4],
                    "notificationStatus", notificationStatus
            );

        }).toList();
    }
}