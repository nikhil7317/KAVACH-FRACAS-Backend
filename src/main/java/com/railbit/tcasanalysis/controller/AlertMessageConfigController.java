package com.railbit.tcasanalysis.controller;

import com.railbit.tcasanalysis.DTO.AlertMessageConfigRequest;
import com.railbit.tcasanalysis.DTO.AlertMessageConfigResponse;
import com.railbit.tcasanalysis.service.AlertMessageConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CRUD + toggle endpoints for alert-message configuration.
 *
 * Base URL : /tcasapi/config/alert-messages
 *
 * GET    /               → list all
 * GET    /{id}           → get one
 * POST   /               → create
 * PUT    /{id}           → update
 * DELETE /{id}           → delete
 * PATCH  /{id}/toggle    → flip enabled flag
 */
@RestController
@RequestMapping("/tcasapi/config/alert-messages")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AlertMessageConfigController {

    private final AlertMessageConfigService service;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll() {
        List<AlertMessageConfigResponse> data = service.getAll();
        return ResponseEntity.ok(build(data, data.size()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(build(service.getById(id), null));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(
            @Valid @RequestBody AlertMessageConfigRequest req) {
        AlertMessageConfigResponse created = service.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(build(created, null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(
            @PathVariable Long id,
            @Valid @RequestBody AlertMessageConfigRequest req) {
        return ResponseEntity.ok(build(service.update(id, req), null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        service.delete(id);
        Map<String, Object> resp = new HashMap<>();
        resp.put("data",         null);
        resp.put("message",      "Deleted successfully");
        resp.put("status",       200);
        resp.put("totalRecords", null);
        return ResponseEntity.ok(resp);
    }

    /**
     * PATCH /tcasapi/config/alert-messages/{id}/toggle
     * Flips the 'enabled' flag (true ↔ false).
     */
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Map<String, Object>> toggle(@PathVariable Long id) {
        return ResponseEntity.ok(build(service.toggle(id), null));
    }

    // ── Response builder (matches your existing pattern) ─────────────────────

    private Map<String, Object> build(Object data, Integer totalRecords) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("data",         data);
        resp.put("message",      "Success");
        resp.put("status",       200);
        resp.put("totalRecords", totalRecords);
        return resp;
    }
}