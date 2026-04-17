package com.railbit.tcasanalysis.controller.alertManagementController;

import com.railbit.tcasanalysis.service.alertManagementService.AlertLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/tcasapi/alertLog")
public class AlertLogController {

    @Autowired
    private AlertLogService service;

    @GetMapping
    public ResponseEntity<?> getAlertLogs() {
        try {
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "data", service.getAlertLogs()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
}