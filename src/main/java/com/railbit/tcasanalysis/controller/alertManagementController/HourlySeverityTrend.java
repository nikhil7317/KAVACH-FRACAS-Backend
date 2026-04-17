package com.railbit.tcasanalysis.controller.alertManagementController;


import com.railbit.tcasanalysis.service.alertManagementService.HourlySeverityTrendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/tcasapi/alertSeverityTrend")
public class HourlySeverityTrend {

    @Autowired
    private HourlySeverityTrendService service;

    @GetMapping
    public ResponseEntity<?> getHourlyTrend() {
        try {
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "data", service.getHourlyTrend()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
}
