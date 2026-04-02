package com.railbit.tcasanalysis.controller;

import com.railbit.tcasanalysis.entity.KavachAlertDetails;
import com.railbit.tcasanalysis.entity.IncidentTrack;
import com.railbit.tcasanalysis.service.KavachAlertDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tcasapi/alertDetails")
@CrossOrigin(origins = "*")
public class KavachAlertDetailsController {

    @Autowired
    private KavachAlertDetailsService service;

    @PostMapping
    public ResponseEntity<Map<String, String>> create(@RequestBody KavachAlertDetails details) {
        service.save(details);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Incident created successfully");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> update(@PathVariable Long id, @RequestBody KavachAlertDetails details) {
        service.update(id, details);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Incident updated successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<KavachAlertDetails> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/by-alert/{kavachAlertId}")
    public ResponseEntity<KavachAlertDetails> getByKavachAlertId(@PathVariable Long kavachAlertId) {
        return ResponseEntity.ok(service.findByKavachAlertId(kavachAlertId));
    }

    @PostMapping("/incident-track")
    public ResponseEntity<Map<String, String>> createIncidentTrack(@RequestBody IncidentTrack track) {
        service.saveIncidentTrack(track);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Incident track created successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/incident-track/{kavachAlertDetailsId}")
    public ResponseEntity<List<IncidentTrack>> getIncidentTrack(@PathVariable Long kavachAlertDetailsId) {
        return ResponseEntity.ok(service.findIncidentTrackByKavachAlertDetailsId(kavachAlertDetailsId));
    }
}