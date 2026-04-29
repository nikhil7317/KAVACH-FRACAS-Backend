package com.railbit.tcasanalysis.controller.alertManagementController;

import com.railbit.tcasanalysis.service.alertManagementService.AlertCardsCountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/tcasapi/alertCards")
public class AlertCardsCount {

    @Autowired
    private AlertCardsCountService service;

    @GetMapping("/today")
    public ResponseEntity<?> getTodayAlertCards() {
        try {
            Object data = service.getTodayAlertCards();

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("data", data != null ? data : new HashMap<>());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage() != null ? e.getMessage() : "Something went wrong");

            return ResponseEntity.badRequest().body(error);
        }
    }
}