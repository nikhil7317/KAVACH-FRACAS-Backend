package com.railbit.tcasanalysis.controller;

import com.railbit.tcasanalysis.DTO.AutoTicketConfigDTO;
import com.railbit.tcasanalysis.service.AutoTicketConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/tcasapi/auto-ticket-config")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AutoTicketConfigController {

    private final AutoTicketConfigService configService;

    /**
     * GET /tcasapi/auto-ticket-config
     * Returns the current active configuration so the frontend form can be pre-filled.
     */
    @GetMapping
    public ResponseEntity<?> getConfig() {
        try {
            AutoTicketConfigDTO dto = configService.getActiveConfig();
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "data", dto
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    /**
     * POST /tcasapi/auto-ticket-config
     * Saves (or replaces) the active configuration.
     *
     * Request body:
     * {
     *   "selectedCategories": [1, 2, 3],
     *   "autoTicketEnabled": true,
     *   "autoEmailEnabled": false,
     *   "railwayUserId": 42,
     *   "oemUserId": 15,
     *   "createdByUserId": 1
     * }
     */
    @PostMapping
    public ResponseEntity<?> saveConfig(@RequestBody AutoTicketConfigDTO dto) {
        try {
            if (dto.getSelectedCategories() == null || dto.getSelectedCategories().isEmpty()) {
                return ResponseEntity.badRequest().body(
                        Map.of("status", "error", "message", "At least one category must be selected."));
            }

            // Only validate users if automation is enabled
            boolean automationEnabled = dto.isAutoTicketEnabled() || dto.isAutoEmailEnabled();
            if (automationEnabled && dto.getRailwayUserId() == null && dto.getOemUserId() == null) {
                return ResponseEntity.badRequest().body(
                        Map.of("status", "error", "message", "At least one user (Railway or OEM) must be selected when automation is enabled."));
            }

            AutoTicketConfigDTO saved = configService.saveConfig(dto);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Configuration saved successfully.",
                    "data", saved
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}