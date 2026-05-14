package com.railbit.tcasanalysis.controller;

import com.railbit.tcasanalysis.DTO.ShedRemarksRequest;
import com.railbit.tcasanalysis.DTO.ShedRemarksResponseDTO;
import com.railbit.tcasanalysis.service.ShedRemarksService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tcasapi/shed-remarks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ShedRemarksController {

    private final ShedRemarksService shedRemarksService;

    @PostMapping
    public ResponseEntity<ShedRemarksResponseDTO> create(
            @RequestBody ShedRemarksRequest request) {
        ShedRemarksResponseDTO saved = shedRemarksService.save(request);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/check/{locoFailureId}")
    public ResponseEntity<Map<String, Boolean>> check(
            @PathVariable Long locoFailureId) {
        boolean has = shedRemarksService.hasShedRemarks(locoFailureId);
        return ResponseEntity.ok(Map.of("hasRemarks", has));
    }

    @GetMapping("/{locoFailureId}")
    public ResponseEntity<List<ShedRemarksResponseDTO>> getByLocoFailure(
            @PathVariable Long locoFailureId) {
        return ResponseEntity.ok(shedRemarksService.getByLocoFailure(locoFailureId));
    }
}