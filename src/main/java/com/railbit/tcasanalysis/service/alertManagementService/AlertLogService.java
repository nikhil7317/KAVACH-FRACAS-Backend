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

        List<Object[]> results = Optional.ofNullable(repo.getAlertLogs())
                .orElse(new ArrayList<>());

        List<Map<String, Object>> response = new ArrayList<>();

        for (Object[] row : results) {

            String ticketStatus = row[5] != null ? row[5].toString().toUpperCase() : "";

            String notificationStatus =
                    ("OPEN".equals(ticketStatus) || "CLOSED".equals(ticketStatus))
                            ? "Sent"
                            : "Pending";

            Map<String, Object> map = new HashMap<>();

            map.put("locoNo", row[0] != null ? row[0] : "");
            map.put("stationName", row[1] != null ? row[1] : "");
            map.put("alertMessage", row[2] != null ? row[2] : "");
            map.put("severity", row[3] != null ? row[3] : "");
            map.put("eventTime", row[4] != null ? row[4] : "");
            map.put("notificationStatus", notificationStatus);

            response.add(map);
        }

        return response;
    }
}