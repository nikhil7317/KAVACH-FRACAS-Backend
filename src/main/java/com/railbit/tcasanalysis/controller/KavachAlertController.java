package com.railbit.tcasanalysis.controller;

import com.railbit.tcasanalysis.entity.KavachAlert;
import com.railbit.tcasanalysis.repository.KavachAlertRepository;
import com.railbit.tcasanalysis.service.AlertCountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
            List<KavachAlert> alerts = alertRepository.findByFilters(
                    fromDate, toDate, locoId, stnId, severity, category);

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
}
