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
import java.util.Map;

@RestController
@RequestMapping("/tcasapi/alertDetails")
@CrossOrigin(origins = "*")
public class KavachAlertDetailsController {

    @Autowired
    private KavachAlertDetailsService service;

    @Autowired
    private KavachAlertDetailsRepository alertDetailsRepo;

    /**
     * POST /alertDetails
     * Fresh ticket assignment. Creates kavach_alert_details + first incident_track.
     * Payload: { kavachAlertId, createdUserId, assignedToId, ticketNo,
     *            ticketStatus, ticketRemarks, incidentCreatedAt }
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> create(
            @RequestBody KavachAlertDetailsRequest request) {
        service.save(request);
        return ResponseEntity.ok(Map.of("message", "Incident created successfully"));
    }

    /**
     * PUT /alertDetails/{id}
     * Update existing ticket (Close / Re-Assign). Adds a new incident_track row.
     * {id} here is kavach_alert_details.id (NOT kavach_alert_id).
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> update(
            @PathVariable Long id,
            @RequestBody KavachAlertDetailsRequest request) {
        service.update(id, request);
        return ResponseEntity.ok(Map.of("message", "Incident updated successfully"));
    }

    /**
     * GET /alertDetails/{id}
     * Fetch ticket by kavach_alert_id (the alert id, NOT the ticket id).
     * Returns full response including:
     *   - kavachAlert       : full alert details for the view dialog
     *   - assignedTo        : full user object with designation (OEM check)
     *   - ticketStatus      : current status
     *   - incidentTracks[]  : all history with createdUser + designation
     */
    @GetMapping("/{id}")
    public ResponseEntity<KavachAlertDetailsResponseDTO> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(service.getAlertDetailsWithTracks(id));
    }

    /**
     * POST /alertDetails/incident-track
     * OEM user submits remarks. Always saves with ticketStatus = "OEM_REMARK".
     * Payload: { kavachAlertDetails: {id}, createdUser: {id},
     *            incidentCreatedAt, ticketStatus, ticketRemarks }
     */
    @PostMapping("/incident-track")
    public ResponseEntity<Map<String, String>> createIncidentTrack(
            @RequestBody IncidentTrack track) {
        service.saveIncidentTrack(track);
        return ResponseEntity.ok(Map.of("message", "OEM remarks submitted successfully"));
    }

    /**
     * GET /alertDetails/today-count
     * Returns count of tickets created today (for auto ticket number generation).
     */
    @GetMapping("/today-count")
    public ResponseEntity<?> getTodayCount() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        String prefix = "PYG/" + LocalDate.now().format(formatter) + "/";
        long count = alertDetailsRepo.countByTicketNoStartingWith(prefix);
        return ResponseEntity.ok(Map.of("count", count));
    }
}