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
