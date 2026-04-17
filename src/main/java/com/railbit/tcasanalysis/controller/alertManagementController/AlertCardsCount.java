package com.railbit.tcasanalysis.controller.alertManagementController;

import com.railbit.tcasanalysis.service.alertManagementService.AlertCardsCountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/tcasapi/alertCards")
public class AlertCardsCount {

    @Autowired
    private AlertCardsCountService service;

    @GetMapping("/today")
    public ResponseEntity<?> getTodayAlertCards() {
        try {
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "data", service.getTodayAlertCards()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
}