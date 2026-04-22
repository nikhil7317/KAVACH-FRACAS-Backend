package com.railbit.tcasanalysis.controller.alertManagementController;

import com.railbit.tcasanalysis.service.alertManagementService.AlertAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/tcasapi/alertAnalysis")
public class AlertAnalysisController {

    @Autowired
    private AlertAnalysisService service;

    @GetMapping("/categoryWiseToday")
    public ResponseEntity<?> getAlertsCategoryWiseToday() {
        try {
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "data", service.getAlertsCategoryWiseToday()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/resolutionStatusToday")
    public ResponseEntity<?> getResolutionStatusToday() {
        try {
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "data", service.getResolutionStatusToday()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/stationHeatmap")
    public ResponseEntity<?> getStationHeatmap() {
        try {
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "data", service.getStationHeatmap()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
    @GetMapping("/collisionVsSos")
    public ResponseEntity<?> getCollisionVsSos() {
        try {
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "data", service.getCollisionVsSos()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
}