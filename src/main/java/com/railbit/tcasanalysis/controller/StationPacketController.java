package com.railbit.tcasanalysis.controller;

import com.railbit.tcasanalysis.entity.StationaryPacket;
import com.railbit.tcasanalysis.service.StationQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tcasapi/allstation")
public class StationPacketController {

    @Autowired
    private StationQueryService stationQueryService;

    /**
     * GET /api/station/packets?fromDate=2026-03-23 14:00:00&toDate=2026-03-23 15:00:00
     *                         &stnCode=14011&locoId=30191
     *
     * Required: fromDate, toDate
     * Optional: stnCode, locoId
     *
     * Examples:
     *   /api/station/packets?fromDate=2026-03-23 00:00:00&toDate=2026-03-23 23:59:59
     *   /api/station/packets?fromDate=2026-03-23 08:00:00&toDate=2026-03-23 09:00:00&stnCode=14011
     *   /api/station/packets?fromDate=2026-03-23 08:00:00&toDate=2026-03-23 09:00:00&locoId=30191
     *   /api/station/packets?fromDate=2026-03-23 08:00:00&toDate=2026-03-23 09:00:00&stnCode=14011&locoId=30191
     */
    @GetMapping("/packets")
    public ResponseEntity<?> getStationPackets(
            @RequestParam("fromDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date fromDate,
            @RequestParam("toDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date toDate,
            @RequestParam(value = "stnCode", required = false) Integer stnCode,
            @RequestParam(value = "locoId", required = false) Integer locoId) {

        try {
            List<StationaryPacket> packets = stationQueryService.findPackets(fromDate, toDate, stnCode, locoId);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "count", packets.size(),
                    "filters", Map.of(
                            "fromDate", fromDate,
                            "toDate", toDate,
                            "stnCode", stnCode != null ? stnCode : "all",
                            "locoId", locoId != null ? locoId : "all"
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
