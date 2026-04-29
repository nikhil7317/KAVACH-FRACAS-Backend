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
            @RequestParam("toDate")   @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date toDate,
            @RequestParam(value = "locoId",    required = false) Integer locoId,
            @RequestParam(value = "stnId",     required = false) Integer stnId,
            @RequestParam(value = "severity",  required = false) String severity,
            @RequestParam(value = "category",  required = false) String category,
            @RequestParam(value = "ticketStatus", required = false) String ticketStatus,
            @RequestParam(value = "userId", required = false) Integer userId) {  // ← NEW PARAMETER

        try {
            Pageable pageable = PageRequest.of(0, 100);



            List<Object[]> results = alertRepository.findByFiltersWithDetails(
                    fromDate, toDate, locoId, stnId, severity, category, userId, ticketStatus, pageable);

            List<Map<String, Object>> alerts = results.stream().map(row -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id",            row[0]);
                map.put("eventTime",     row[1]);
                map.put("locoId",        row[2]);
                map.put("stationId",     row[3]);   // ✅ NEW
                map.put("stationCode",   row[4]);   // shifted
                map.put("alertCategory", row[5]);
                map.put("alertCode",     row[6]);
                map.put("alertMessage",  row[7]);
                map.put("severity",      row[8]);
                map.put("sourcePktType", row[9]);
                map.put("locoPacketId",  row[10]);
                map.put("trainSpeed",    row[11]);
                map.put("locoMode",      row[12]);
                map.put("absLocoLoc",    row[13]);
                map.put("latitude",      row[14]);
                map.put("longitude",     row[15]);
                map.put("createdAt",     row[16]);
                map.put("lastRfidTag",   row[17]);
                map.put("isNotified",    row[18]);
                map.put("ticketNo",      row[19]);
                map.put("ticketStatus",  row[20]);
                map.put("isPopupDialogAck", row[21]);
                map.put("adminRemarks",  row[22]);
                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "count",  alerts.size(),
                    "filters", Map.of(
                            "fromDate", fromDate,
                            "toDate",   toDate,
                            "locoId",   locoId   != null ? locoId   : "all",
                            "stnId", stnId != null ? stnId : "all",
                            "severity", severity != null ? severity : "all",
                            "category", category != null ? category : "all",
                            "ticketStatus", ticketStatus != null ? ticketStatus : "all",
                            "userId", userId != null ? userId : "none"
                    ),
                    "data", alerts
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("status", "error", "message", e.getMessage()));
        }
    }
    @GetMapping("/counts")
    public ResponseEntity<?> getAlertCounts(
            @RequestParam(value = "locoId", required = false) Integer locoId,
            @RequestParam("fromDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date fromDate,
            @RequestParam("toDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date toDate) {

        try {
            Map<String, Integer> counts = alertCountService.getAlertCounts(locoId, fromDate, toDate);

            // Use HashMap instead of Map.of() to allow null values, or convert null to "all"
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("locoId", locoId != null ? locoId : "all");
            response.put("fromDate", fromDate);
            response.put("toDate", toDate);
            response.put("alertCounts", counts);

            return ResponseEntity.ok(response);
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

    @PatchMapping("/{id}/notify")
    public ResponseEntity<?> markAsNotified(@PathVariable Long id) {
        try {
            int updated = alertRepository.markAsNotified(id);
            if (updated == 0) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(Map.of("status", "success", "message", "Alert marked as notified"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("status", "error", "message", e.getMessage()));
        }
    }

    // Bulk endpoint — for marking multiple alerts seen at once
    @PatchMapping("/notify-batch")
    public ResponseEntity<?> markBatchAsNotified(@RequestBody List<Long> ids) {
        try {
            int updated = alertRepository.markAllAsNotified(ids);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "updated", updated
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @GetMapping("/alertNotifications")
    public ResponseEntity<?> getAlertNotifications(
            @RequestParam("fromDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date fromDate,
            @RequestParam("toDate")   @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date toDate) {

        try {
            List<Object[]> results = alertRepository.findLatest10Alerts(fromDate, toDate);

            List<Map<String, Object>> alerts = results.stream().map(row -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id",            row[0]);
                map.put("eventTime",     row[1]);
                map.put("locoId",        row[2]);
                map.put("stationId",     row[3]);
                map.put("alertCategory", row[4]);
                map.put("alertCode",     row[5]);
                map.put("alertMessage",  row[6]);
                map.put("severity",      row[7]);
                map.put("sourcePktType", row[8]);
                map.put("locoPacketId",  row[9]);
                map.put("trainSpeed",    row[10]);
                map.put("locoMode",      row[11]);
                map.put("absLocoLoc",    row[12]);
                map.put("latitude",      row[13]);
                map.put("longitude",     row[14]);
                map.put("createdAt",     row[15]);
                map.put("lastRfidTag",   row[16]);
                map.put("isNotified",    row[17]);
                map.put("ticketNo",      row[18]);
                map.put("ticketStatus",  row[19]);

                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "count",  alerts.size(),
                    "data",   alerts
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("status", "error", "message", e.getMessage()));
        }
    }
    @PostMapping("/ack-popup")
    public ResponseEntity<?> acknowledgePopup(
            @RequestParam("alertId") Long alertId) {
        try {
            KavachAlert alert = alertRepository
                    .findById(alertId)
                    .orElseThrow(() -> new RuntimeException("Alert not found: " + alertId));

            alert.setIsPopupDialogAck(true);
            alertRepository.save(alert);

            return ResponseEntity.ok(Map.of(
                    "status",  "success",
                    "alertId", alertId,
                    "acked",   true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("status", "error", "message", e.getMessage()));
        }
    }
}
