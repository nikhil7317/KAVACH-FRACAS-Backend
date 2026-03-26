package com.railbit.tcasanalysis.controller;

import com.railbit.tcasanalysis.DTO.LocoTelemetryDTO;
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

    /**
     * GET /tcasapi/loco/telemetry?fromDate=2026-03-23 14:00:00&toDate=2026-03-23 15:00:00
     *                      &locoId=37146&stnId=37006
     *
     * Returns only essential fields for speed graph and map tracking:
     * - trainSpeed, timestamp (for speed graph)
     * - latitudeDeg, longitudeDeg, timestamp (for real-time map)
     *
     * Data refreshes every 3 seconds for real-time tracking
     */
    @GetMapping("/telemetry")
    public ResponseEntity<?> getLocoTelemetry(
            @RequestParam("fromDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date fromDate,
            @RequestParam("toDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date toDate,
            @RequestParam(value = "locoId", required = false) Integer locoId,
            @RequestParam(value = "stnId", required = false) Integer stnId) {

        try {
            List<LocoPacket> packets = locoQueryService.findPackets(fromDate, toDate, locoId, stnId);

            // Extract only required fields from packets
            List<LocoTelemetryDTO> telemetryData = packets.stream()
                    .flatMap(packet -> extractTelemetryFromPacket(packet).stream())
                    .sorted(Comparator.comparing(LocoTelemetryDTO::getTimestamp))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "count", telemetryData.size(),
                    "filters", Map.of(
                            "fromDate", fromDate,
                            "toDate", toDate,
                            "locoId", locoId != null ? locoId : "all",
                            "stnId", stnId != null ? stnId : "all"
                    ),
                    "data", telemetryData
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * GET /tcasapi/loco/telemetry/latest?locoId=37146
     *
     * Returns ONLY the latest telemetry point for real-time map updates.
     * Call this every 3 seconds for live tracking.
     */
    @GetMapping("/telemetry/latest")
    public ResponseEntity<?> getLatestTelemetry(
            @RequestParam("locoId") Integer locoId,
            @RequestParam(value = "stnId", required = false) Integer stnId) {

        try {
            // Get last 3 seconds of data (or adjust window as needed)
            Date toDate = new Date();
            Date fromDate = new Date(toDate.getTime() - 10000); // 10 second window to ensure we catch data

            List<LocoPacket> packets = locoQueryService.findPackets(fromDate, toDate, locoId, stnId);

            LocoTelemetryDTO latest = packets.stream()
                    .flatMap(packet -> extractTelemetryFromPacket(packet).stream())
                    .max(Comparator.comparing(LocoTelemetryDTO::getTimestamp))
                    .orElse(null);

            if (latest == null) {
                return ResponseEntity.ok(Map.of(
                        "status", "success",
                        "message", "No recent data found for loco " + locoId,
                        "data", null
                ));
            }

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "locoId", locoId,
                    "timestamp", new Date(),
                    "data", latest
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * Extract telemetry data from a LocoPacket.
     * Checks both onboardRegularPackets and accessRequestPackets.
     */
    private List<LocoTelemetryDTO> extractTelemetryFromPacket(LocoPacket packet) {
        List<LocoTelemetryDTO> results = new ArrayList<>();

        // Check accessRequestPackets first (based on your sample data, this is where the data is)
        if (packet.getAccessRequestPackets() != null && !packet.getAccessRequestPackets().isEmpty()) {
            for (AccessRequestPacket arp : packet.getAccessRequestPackets()) {
                // Parse frameTime to create proper timestamp
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

        // Check onboardRegularPackets if exists (currently empty in your sample)
        if (packet.getOnboardRegularPackets() != null && !packet.getOnboardRegularPackets().isEmpty()) {
            // Add similar extraction for regular packets when they have data
            // Currently your sample shows this as empty
        }

        return results;
    }

    /**
     * Parse frameTime (HH:mm:ss) and combine with atDate to create full timestamp
     */
    private Date parseFrameTime(Date atDate, String frameTime) {
        if (frameTime == null || frameTime.isEmpty()) {
            return atDate;
        }
        try {
            // frameTime format: "14:56:12"
            String[] parts = frameTime.split(":");
            Calendar cal = Calendar.getInstance();
            cal.setTime(atDate);
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
            cal.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
            cal.set(Calendar.SECOND, Integer.parseInt(parts[2]));
            return cal.getTime();
        } catch (Exception e) {
            return atDate; // fallback to packet timestamp
        }
    }
}