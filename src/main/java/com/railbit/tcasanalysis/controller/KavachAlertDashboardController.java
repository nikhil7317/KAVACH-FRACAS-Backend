package com.railbit.tcasanalysis.controller;

import com.railbit.tcasanalysis.service.KavachAlertDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/tcasapi/alert")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class KavachAlertDashboardController {

    private final KavachAlertDashboardService dashboardService;

    /**
     * GET /kavach/dashboard/getLastAlertDate
     * Returns the most recent alert event_time date.
     * Response: { "data": "2026-03-28", "message": "Success", "status": 200, "totalRecords": null }
     */
    @GetMapping("/dashboard/getLastAlertDate")
    public ResponseEntity<Map<String, Object>> getLastAlertDate() {
        String lastDate = dashboardService.getLastAlertDate();
        return ResponseEntity.ok(buildResponse(lastDate));
    }

    /**
     * GET /kavach/dashboard/getDashboardCountCards?fromDate=2026-03-01&toDate=2026-04-14
     * Returns summary count cards: Total Incidents, With/Without Ticket, Open/Closed Tickets.
     */
    @GetMapping("/dashboard/getDashboardCountCards")
    public ResponseEntity<Map<String, Object>> getDashboardCountCards(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate,
            @RequestParam(required = false) String divisionId) {
        int divId=1;
        if (divisionId == null || divisionId.isEmpty() || divisionId.equalsIgnoreCase("All")) {
            divId = 1;
        }else{
            try{
                divId = Integer.parseInt(divisionId);
            } catch (Exception e) {
                divId=1;
            }
        }

        return ResponseEntity.ok(buildResponse(dashboardService.getDashboardCountCards(fromDate, toDate, divId)));
    }

    /**
     * GET /kavach/dashboard/getAlertsLocoWise?fromDate=2026-03-01&toDate=2026-04-14
     * Returns alert counts grouped by loco_id.
     */
    @GetMapping("/dashboard/getAlertsLocoWise")
    public ResponseEntity<Map<String, Object>> getAlertsLocoWise(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate,
            @RequestParam String divisionId) {
        int divId=1;
        if (divisionId == null || divisionId.isEmpty() || divisionId.equalsIgnoreCase("All")) {
            divId = 1;
        }else{
            try{
                divId = Integer.parseInt(divisionId);
            } catch (Exception e) {
                divId=1;
            }
        }
        return ResponseEntity.ok(buildResponse(dashboardService.getAlertsLocoWise(fromDate, toDate,divId)));
    }

    /**
     * GET /kavach/dashboard/getAlertsCategoryWise?fromDate=2026-03-01&toDate=2026-04-14
     * Returns alert counts grouped by alert_category (replaces "cause wise").
     */
    @GetMapping("/dashboard/getAlertsCategoryWise")
    public ResponseEntity<Map<String, Object>> getAlertsCategoryWise(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate,
            @RequestParam String divisionId) {
        int divId=1;
        if (divisionId == null || divisionId.isEmpty() || divisionId.equalsIgnoreCase("All")) {
            divId = 1;
        }else{
            try{
                divId = Integer.parseInt(divisionId);
            } catch (Exception e) {
                divId=1;
            }
        }

        return ResponseEntity.ok(buildResponse(dashboardService.getAlertsCategoryWise(fromDate, toDate, divId)));
    }

    /**
     * GET /kavach/dashboard/getAlertsStationWise?fromDate=2026-03-01&toDate=2026-04-14
     * Returns alert counts grouped by station_id.
     */
    @GetMapping("/dashboard/getAlertsStationWise")
    public ResponseEntity<Map<String, Object>> getAlertsStationWise(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate,
            @RequestParam String divisionId) {
        int divId=1;
        if (divisionId == null || divisionId.isEmpty() || divisionId.equalsIgnoreCase("All")) {
            divId = 1;
        }else{
            try{
                divId = Integer.parseInt(divisionId);
            } catch (Exception e) {
                divId=1;
            }
        }
        return ResponseEntity.ok(buildResponse(dashboardService.getAlertsStationWise(fromDate, toDate,divId)));
    }

    /**
     * GET /kavach/analysis/getCategoryWiseYearlyGraphData
     * Returns monthly bar graph data grouped by alert_category for the last 12 months.
     */
    @GetMapping("/analysis/getCategoryWiseYearlyGraphData")
    public ResponseEntity<Map<String, Object>> getCategoryWiseYearlyGraphData(
            @RequestParam(required = false) String divisionId) {

        int divId=1;
        if (divisionId == null || divisionId.isEmpty() || divisionId.equalsIgnoreCase("All")) {
            divId = 1;
        }else{
            try{
                divId = Integer.parseInt(divisionId);
            } catch (Exception e) {
                divId=1;
            }
        }

        return ResponseEntity.ok(
                buildResponse(
                        dashboardService.getCategoryWiseYearlyGraphData(divId)
                )
        );
    }

    // ── Reusable response builder ──────────────────────────────────────────────
    private Map<String, Object> buildResponse(Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        response.put("message", "Success");
        response.put("status", 200);
        response.put("totalRecords", null);
        return response;
    }
}