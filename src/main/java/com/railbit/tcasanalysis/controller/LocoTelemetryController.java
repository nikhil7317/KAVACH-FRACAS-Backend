package com.railbit.tcasanalysis.controller;

import com.railbit.tcasanalysis.DTO.LocoTelemetryDTO;
import com.railbit.tcasanalysis.cache.LiveTelemetryCache;
import com.railbit.tcasanalysis.locomodal.AccessRequestPacket;
import com.railbit.tcasanalysis.locomodal.LocoPacket;
import com.railbit.tcasanalysis.service.LocoQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tcasapi/loco")
public class LocoTelemetryController {

    @Autowired
    private LocoQueryService locoQueryService;

    @Autowired
    private LiveTelemetryCache liveCache;


    // =========================================================================
    // EXISTING: Full date-range telemetry (unchanged — used for history/charts)
    // GET /tcasapi/loco/telemetry?fromDate=...&toDate=...&locoId=...
    // =========================================================================
    @GetMapping("/telemetry")
    public ResponseEntity<?> getLocoTelemetry(
            @RequestParam("fromDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date fromDate,
            @RequestParam("toDate")   @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date toDate,
            @RequestParam(value = "locoId", required = false) Integer locoId,
            @RequestParam(value = "stnId",  required = false) Integer stnId) {

        try {
            List<LocoPacket> packets = locoQueryService.findPackets(fromDate, toDate, locoId, stnId);

            List<LocoTelemetryDTO> telemetryData = packets.stream()
                    .flatMap(packet -> extractTelemetryFromPacket(packet).stream())
                    .sorted(Comparator.comparing(LocoTelemetryDTO::getTimestamp))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "count",  telemetryData.size(),
                    "filters", Map.of(
                            "fromDate", fromDate,
                            "toDate",   toDate,
                            "locoId",   locoId != null ? locoId : "all",
                            "stnId",    stnId  != null ? stnId  : "all"
                    ),
                    "data", telemetryData
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status",  "error",
                    "message", e.getMessage()
            ));
        }
    }


    // =========================================================================
    // NEW: Live telemetry — reads from in-memory cache, ZERO DB hits.
    //
    // GET /tcasapi/loco/telemetry/live?locoId=37146
    //
    // The cache is refreshed every 60 seconds by LiveTelemetryScheduler.
    // Call this endpoint from the frontend every 60 seconds for live updates.
    // Response is always the last 60-second snapshot — tiny, fast, no freeze.
    // =========================================================================
    @GetMapping("/telemetry/live")
    public ResponseEntity<?> getLiveTelemetry(
            @RequestParam("locoId") Integer locoId) {

        try {
            List<LocoTelemetryDTO> data = liveCache.get(locoId);
            Date lastRefreshed = liveCache.getLastRefreshed(locoId);

            return ResponseEntity.ok(Map.of(
                    "status",        "success",
                    "locoId",        locoId,
                    "count",         data.size(),
                    "windowSeconds", 60,
                    "lastRefreshed", lastRefreshed != null ? lastRefreshed : "not yet refreshed",
                    "data",          data
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status",  "error",
                    "message", e.getMessage()
            ));
        }
    }


    // =========================================================================
    // NEW: Live telemetry for ALL locos at once (for the map overview).
    //
    // GET /tcasapi/loco/telemetry/live/all
    //
    // Returns the latest cached snapshot for every loco that was active in
    // the last 60-second window. No parameters needed.
    // =========================================================================
    @GetMapping("/telemetry/live/all")
    public ResponseEntity<?> getAllLiveTelemetry() {
        try {
            Map<Integer, List<LocoTelemetryDTO>> all = liveCache.getAll();

            // Build a flat list of the latest point per loco (for the map pins)
            List<Map<String, Object>> latestPerLoco = all.entrySet().stream()
                    .filter(e -> !e.getValue().isEmpty())
                    .map(e -> {
                        LocoTelemetryDTO latest = e.getValue().get(e.getValue().size() - 1);
                        Map<String, Object> entry = new LinkedHashMap<>();
                        entry.put("locoId",       e.getKey());
                        entry.put("latest",       latest);
                        entry.put("pointsInWindow", e.getValue().size());
                        entry.put("lastRefreshed",  liveCache.getLastRefreshed(e.getKey()));
                        return entry;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "status",        "success",
                    "activeLocos",   latestPerLoco.size(),
                    "windowSeconds", 60,
                    "data",          latestPerLoco
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status",  "error",
                    "message", e.getMessage()
            ));
        }
    }


    // =========================================================================
    // EXISTING: Latest single point (unchanged — kept for compatibility)
    // GET /tcasapi/loco/telemetry/latest?locoId=37146
    // =========================================================================
    @GetMapping("/telemetry/latest")
    public ResponseEntity<?> getLatestTelemetry(
            @RequestParam("locoId") Integer locoId,
            @RequestParam(value = "stnId", required = false) Integer stnId) {

        try {
            Date toDate   = new Date();
            Date fromDate = new Date(toDate.getTime() - 10_000);

            List<LocoPacket> packets = locoQueryService.findPackets(fromDate, toDate, locoId, stnId);

            LocoTelemetryDTO latest = packets.stream()
                    .flatMap(packet -> extractTelemetryFromPacket(packet).stream())
                    .max(Comparator.comparing(LocoTelemetryDTO::getTimestamp))
                    .orElse(null);

            if (latest == null) {
                return ResponseEntity.ok(Map.of(
                        "status",  "success",
                        "message", "No recent data found for loco " + locoId,
                        "data",    Collections.emptyMap()
                ));
            }

            return ResponseEntity.ok(Map.of(
                    "status",    "success",
                    "locoId",    locoId,
                    "timestamp", new Date(),
                    "data",      latest
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status",  "error",
                    "message", e.getMessage()
            ));
        }
    }


    // =========================================================================
    // Helpers
    // =========================================================================

    private List<LocoTelemetryDTO> extractTelemetryFromPacket(LocoPacket packet) {
        List<LocoTelemetryDTO> results = new ArrayList<>();

        if (packet.getAccessRequestPackets() != null && !packet.getAccessRequestPackets().isEmpty()) {
            for (AccessRequestPacket arp : packet.getAccessRequestPackets()) {
                Date timestamp = parseFrameTime(packet.getAtDate(), arp.getFrameTime());
                results.add(new LocoTelemetryDTO(
                        packet.getLocoId(),
                        timestamp,
                        arp.getTrainSpeed(),
                        arp.getLatitudeDeg(),
                        arp.getLongitudeDeg(),
                        "accessRequest"
                ));
            }
        }



        return results;
    }

    private Date parseFrameTime(Date atDate, String frameTime) {
        if (frameTime == null || frameTime.isEmpty()) return atDate;
        try {
            String[] parts = frameTime.split(":");
            Calendar cal = Calendar.getInstance();
            cal.setTime(atDate);
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
            cal.set(Calendar.MINUTE,      Integer.parseInt(parts[1]));
            cal.set(Calendar.SECOND,      Integer.parseInt(parts[2]));
            return cal.getTime();
        } catch (Exception e) {
            return atDate;
        }
    }
}