package com.railbit.tcasanalysis.controller;


import com.railbit.tcasanalysis.locomodal.LocoPacket;

import com.railbit.tcasanalysis.service.LocoQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tcasapi/loco")
public class LocoPacketController {

    @Autowired
    private LocoQueryService locoQueryService;

    /**
     * GET /api/loco/packets?fromDate=2026-03-23 14:00:00&toDate=2026-03-23 15:00:00
     *                      &locoId=37146&stnId=37006
     *
     * Required: fromDate, toDate
     * Optional: locoId, stnId
     *
     * Examples:
     *   /api/loco/packets?fromDate=2026-03-23 00:00:00&toDate=2026-03-23 23:59:59
     *   /api/loco/packets?fromDate=2026-03-23 14:00:00&toDate=2026-03-23 15:00:00&locoId=37146
     *   /api/loco/packets?fromDate=2026-03-23 14:00:00&toDate=2026-03-23 15:00:00&stnId=37006
     *   /api/loco/packets?fromDate=2026-03-23 14:00:00&toDate=2026-03-23 15:00:00&locoId=37146&stnId=37006
     */
    @GetMapping("/packets")
    public ResponseEntity<?> getLocoPackets(
            @RequestParam("fromDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date fromDate,
            @RequestParam("toDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date toDate,
            @RequestParam(value = "locoId", required = false) Integer locoId,
            @RequestParam(value = "stnId", required = false) Integer stnId) {

        try {
            List<LocoPacket> packets = locoQueryService.findPackets(fromDate, toDate, locoId, stnId);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "count", packets.size(),
                    "filters", Map.of(
                            "fromDate", fromDate,
                            "toDate", toDate,
                            "locoId", locoId != null ? locoId : "all",
                            "stnId", stnId != null ? stnId : "all"
                    ),
                    "data", packets
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
}
