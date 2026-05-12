package com.railbit.tcasanalysis.controller;

import com.railbit.tcasanalysis.DTO.LocoFailureListDTO;
import com.railbit.tcasanalysis.DTO.LocoFailureResponseDTO;
import com.railbit.tcasanalysis.entity.LocoFailure;
import com.railbit.tcasanalysis.repository.LocoFailureRepository;
import com.railbit.tcasanalysis.service.LocoFailureService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/tcasapi/loco-failure")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LocoFailureController {

    private final LocoFailureService service;
    private final LocoFailureRepository locoFailureRepository;
    @PostMapping
    public ResponseEntity<Map<String, String>> create(@RequestBody Map<String, Object> request) {
        service.save(request);
        return ResponseEntity.ok(Map.of("message", "Loco failure created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocoFailureResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getLocoFailureWithTracks(id));
    }

    @GetMapping
    public ResponseEntity<Page<LocoFailureListDTO>> getAll(
            @RequestParam(required = false) Integer locoId,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String ticketStatus,
            @RequestParam(required = false) String ticketNo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(service.getAllLocoFailures(
                locoId, fromDate, toDate, severity, ticketStatus, ticketNo, page, size));
    }
    @GetMapping("/today-count")
    public ResponseEntity<?> getTodayCount(@RequestParam Integer locoId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        String prefix = "PRYJ/" + locoId + "/" + LocalDate.now().format(formatter) + "/";
        long count = locoFailureRepository.countByTicketNoStartingWith(prefix);
        return ResponseEntity.ok(Map.of("count", count));
    }
}