package com.railbit.tcasanalysis.controller;

import com.railbit.tcasanalysis.entity.KavachAlert;
import com.railbit.tcasanalysis.repository.KavachAlertRepository;
import com.railbit.tcasanalysis.service.AlertCountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tcasapi/alerts")
public class KavachAlertController {
    @Autowired
    private AlertCountService alertCountService;

    @Autowired
    private KavachAlertRepository alertRepository;



    /**
     * GET /api/alerts?fromDate=2026-03-23 00:00:00&toDate=2026-03-23 23:59:59
     *               &locoId=37146&stnId=37006&severity=CRITICAL&category=EMERGENCY
     *
     * Required: fromDate, toDate
     * Optional: locoId, stnId, severity (CRITICAL/WARNING/INFO), category (EMERGENCY/BRAKE/TAG_LINK)
     */
    @GetMapping
    public ResponseEntity<?> getAlerts(

            @RequestParam("fromDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date fromDate,
            @RequestParam("toDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date toDate,
            @RequestParam(value = "locoId", required = false) Integer locoId,
            @RequestParam(value = "stnId", required = false) Integer stnId,
            @RequestParam(value = "severity", required = false) String severity,
            @RequestParam(value = "category", required = false) String category) {

        try {
            Pageable pageable = PageRequest.of(0, 100);
            List<Object[]> results = alertRepository.findByFiltersWithDetails(
                    fromDate, toDate, locoId, stnId, severity, category, pageable);

            List<Map<String, Object>> alerts = results.stream().map(row -> {
                KavachAlert alert = (KavachAlert) row[0];
                alert.setTicketNo((String) row[1]);
                alert.setTicketStatus((String) row[2]);

                // Convert to map for JSON response
                Map<String, Object> map = new HashMap<>();
                map.put("id", alert.getId());
                map.put("eventTime", alert.getEventTime());
                map.put("locoId", alert.getLocoId());
                map.put("stationId", alert.getStationId());
                map.put("alertCategory", alert.getAlertCategory());
                map.put("alertCode", alert.getAlertCode());
                map.put("alertMessage", alert.getAlertMessage());
                map.put("severity", alert.getSeverity());
                map.put("sourcePktType", alert.getSourcePktType());
                map.put("locoPacketId", alert.getLocoPacketId());
                map.put("trainSpeed", alert.getTrainSpeed());
                map.put("locoMode", alert.getLocoMode());
                map.put("absLocoLoc", alert.getAbsLocoLoc());
                map.put("latitude", alert.getLatitude());
                map.put("longitude", alert.getLongitude());
                map.put("createdAt", alert.getCreatedAt());
                map.put("lastRfidTag", alert.getLastRfidTag());
                map.put("ticketNo", alert.getTicketNo());
                map.put("ticketStatus", alert.getTicketStatus());
                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "count", alerts.size(),
                    "filters", Map.of(
                            "fromDate", fromDate,
                            "toDate", toDate,
                            "locoId", locoId != null ? locoId : "all",
                            "stnId", stnId != null ? stnId : "all",
                            "severity", severity != null ? severity : "all",
                            "category", category != null ? category : "all"
                    ),
                    "data", alerts
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
    @GetMapping("/counts")
    public ResponseEntity<?> getAlertCounts(
            @RequestParam("locoId") Integer locoId,
            @RequestParam("fromDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date fromDate,
            @RequestParam("toDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date toDate) {

        try {
            Map<String, Integer> counts = alertCountService.getAlertCounts(locoId, fromDate, toDate);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "locoId", locoId,
                    "fromDate", fromDate,
                    "toDate", toDate,
                    "alertCounts", counts
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @GetMapping("/nmsCategories")
    public ResponseEntity<?> getDistinctAlertCategories() {
        try {
            List<String> categories = alertRepository.findDistinctAlertCategories();

            List<Map<String, String>> categoryList = categories.stream()
                    .map(cat -> Map.of(
                            "label", cat,          // Display text  e.g. "EMERGENCY"
                            "value", cat           // Filter string e.g. "EMERGENCY"
                    ))
                    .collect(java.util.stream.Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "count", categoryList.size(),
                    "data", categoryList
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @GetMapping("/liveAlertMessage")
    public ResponseEntity<?> getLiveAlerts() {
        try {
            List<Object[]> results = alertRepository.findTop10TodayAlerts();

            List<Map<String, Object>> data = results.stream().map(row -> {
                Map<String, Object> map = new HashMap<>();
                map.put("alertMessage", row[0]);
                map.put("stationName", row[1]);
                map.put("eventTime", row[2]);
                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "count", data.size(),
                    "data", data
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
}
