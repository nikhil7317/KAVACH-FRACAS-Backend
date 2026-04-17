package com.railbit.tcasanalysis.controller;

import com.railbit.tcasanalysis.entity.Severity;
import com.railbit.tcasanalysis.service.SeverityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/tcasapi/severities")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SeverityController {

    private final SeverityService severityService;

    // GET /api/severities → used by frontend to populate severity dropdown
    @GetMapping
    public ResponseEntity<List<Severity>> getAll() {
        return ResponseEntity.ok(severityService.getAllSeverities());
    }
}