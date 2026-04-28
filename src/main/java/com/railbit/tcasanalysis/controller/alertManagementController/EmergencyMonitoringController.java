package com.railbit.tcasanalysis.controller.alertManagementController;



import com.railbit.tcasanalysis.service.alertManagementService.EmergencyMonitoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

        import java.util.Map;

@RestController
@RequestMapping("/tcasapi/alerts")
public class EmergencyMonitoringController {

    @Autowired
    private EmergencyMonitoringService service;

    @GetMapping("/emergencyMonitorings")
    public ResponseEntity<?> getSeverityMonitoring() {
        try {
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "data", service.getEmergencyMonitoring()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
}