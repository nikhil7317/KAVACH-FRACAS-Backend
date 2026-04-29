package com.railbit.tcasanalysis.controller.alertManagementController;

import com.railbit.tcasanalysis.service.alertManagementService.AlertAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/tcasapi/alertAnalysis")
public class AlertAnalysisController {

    @Autowired
    private AlertAnalysisService service;

    @GetMapping("/categoryWiseToday")
    public ResponseEntity<?> getAlertsCategoryWiseToday() {
        try {
            Object data = service.getAlertsCategoryWiseToday();

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("data", data != null ? data : new HashMap<>());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return buildErrorResponse(e);
        }
    }

    @GetMapping("/resolutionStatusToday")
    public ResponseEntity<?> getResolutionStatusToday() {
        try {
            Object data = service.getResolutionStatusToday();

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("data", data != null ? data : new HashMap<>());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return buildErrorResponse(e);
        }
    }

    @GetMapping("/stationHeatmap")
    public ResponseEntity<?> getStationHeatmap() {
        try {
            Object data = service.getStationHeatmap();

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("data", data != null ? data : new HashMap<>());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return buildErrorResponse(e);
        }
    }

    @GetMapping("/collisionVsSos")
    public ResponseEntity<?> getCollisionVsSos() {
        try {
            Object data = service.getCollisionVsSos();

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("data", data != null ? data : new HashMap<>());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return buildErrorResponse(e);
        }
    }

    // ✅ Common error handler (clean code)
    private ResponseEntity<Map<String, Object>> buildErrorResponse(Exception e) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", "error");
        error.put("message", e.getMessage() != null ? e.getMessage() : "Something went wrong");
        return ResponseEntity.badRequest().body(error);
    }
}