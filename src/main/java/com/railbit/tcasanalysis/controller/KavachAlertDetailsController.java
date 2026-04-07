package com.railbit.tcasanalysis.controller;


import com.railbit.tcasanalysis.DTO.KavachAlertDetailsRequest;
import com.railbit.tcasanalysis.DTO.KavachAlertDetailsResponseDTO;
import com.railbit.tcasanalysis.entity.IncidentTrack;
import com.railbit.tcasanalysis.repository.KavachAlertDetailsRepository;
import com.railbit.tcasanalysis.service.KavachAlertDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tcasapi/alertDetails")
@CrossOrigin(origins = "*")
public class KavachAlertDetailsController {

    @Autowired
    private KavachAlertDetailsService service;
    @Autowired
    private KavachAlertDetailsRepository alertDetailsRepo;

    @PostMapping
    public ResponseEntity<Map<String, String>> create(@RequestBody KavachAlertDetailsRequest request) {
        service.save(request);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Incident created successfully");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> update(@PathVariable Long id, @RequestBody KavachAlertDetailsRequest request) {
        service.update(id, request);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Incident updated successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<KavachAlertDetailsResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getAlertDetailsWithTracks(id));
    }

    @PostMapping("/incident-track")
    public ResponseEntity<Map<String, String>> createIncidentTrack(@RequestBody IncidentTrack track) {
        service.saveIncidentTrack(track);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Incident track created successfully");
        return ResponseEntity.ok(response);
    }
    @GetMapping("/today-count")
    public ResponseEntity<?> getTodayCount() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        String today = LocalDate.now().format(formatter);

        String prefix = "PYG/" + today + "/";

        long count = alertDetailsRepo.countByTicketNoStartingWith(prefix);

        return ResponseEntity.ok(Map.of("count", count));
    }




}