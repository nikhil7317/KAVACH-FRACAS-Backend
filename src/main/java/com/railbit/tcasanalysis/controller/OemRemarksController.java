package com.railbit.tcasanalysis.controller;

import com.railbit.tcasanalysis.DTO.OemRemarksRequest;
import com.railbit.tcasanalysis.DTO.OemRemarksResponseDTO;
import com.railbit.tcasanalysis.service.OemRemarksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tcasapi/oem-remarks")
@CrossOrigin(origins = "*")
public class OemRemarksController {

    @Autowired
    private OemRemarksService oemRemarksService;

    /**
     * POST /oem-remarks
     * OEM user submits remarks for a ticket.
     *
     * Payload:
     * {
     *   "kavachAlertDetails": { "id": 1 },
     *   "createdUser":        { "id": 78 },
     *   "incidentCreatedAt":  "2026-04-11T15:00:00",
     *   "ticketRemarks":      "OEM verified the tag link issue on loco 37160"
     * }
     */
    @PostMapping
    public ResponseEntity<OemRemarksResponseDTO> create(
            @RequestBody OemRemarksRequest request) {
        OemRemarksResponseDTO saved = oemRemarksService.save(request);
        return ResponseEntity.ok(saved);
    }

    /**
     * GET /oem-remarks/check/{kavachAlertDetailsId}
     * Returns { "hasRemarks": true/false }
     * Admin frontend calls this to decide whether Close/Re-Assign is allowed.
     *
     * Example: GET /oem-remarks/check/1  →  { "hasRemarks": true }
     */
    @GetMapping("/check/{kavachAlertDetailsId}")
    public ResponseEntity<Map<String, Boolean>> check(
            @PathVariable Long kavachAlertDetailsId) {
        boolean has = oemRemarksService.hasOemRemarks(kavachAlertDetailsId);
        return ResponseEntity.ok(Map.of("hasRemarks", has));
    }

    /**
     * GET /oem-remarks/{kavachAlertDetailsId}
     * Returns all OEM remarks for a ticket (for display in the dialog).
     *
     * Example: GET /oem-remarks/1
     */
    @GetMapping("/{kavachAlertDetailsId}")
    public ResponseEntity<List<OemRemarksResponseDTO>> getByTicket(
            @PathVariable Long kavachAlertDetailsId) {
        return ResponseEntity.ok(oemRemarksService.getByTicket(kavachAlertDetailsId));
    }
}