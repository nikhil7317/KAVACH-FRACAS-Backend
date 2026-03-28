package com.railbit.tcasanalysis.controller;


import com.railbit.tcasanalysis.entity.KavachFaultPacket;
import com.railbit.tcasanalysis.repository.KavachFaultPacketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tcasapi/faults")
public class KavachFaultController {

    @Autowired
    private KavachFaultPacketRepository repository;

    /**
     * GET /api/faults?fromDate=2026-03-23 00:00:00&toDate=2026-03-23 23:59:59
     *               &subsystemId=37118&subsystemType=17
     *
     * Required: fromDate, toDate
     * Optional: subsystemId (3-byte KAVACH ID)
     *           subsystemType (0x11=Station, 0x22=Onboard, 0x33=TSRMS — pass decimal: 17, 34, 51)
     */
    @GetMapping
    public ResponseEntity<?> getFaults(
            @RequestParam("fromDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date fromDate,
            @RequestParam("toDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date toDate,
            @RequestParam(value = "subsystemId", required = false) Integer subsystemId,
            @RequestParam(value = "subsystemType", required = false) Integer subsystemType) {

        try {
            List<KavachFaultPacket> packets = repository.findByFilters(
                    fromDate, toDate, subsystemId, subsystemType);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "count", packets.size(),
                    "filters", Map.of(
                            "fromDate", fromDate,
                            "toDate", toDate,
                            "subsystemId", subsystemId != null ? subsystemId : "all",
                            "subsystemType", subsystemType != null ? subsystemType : "all"
                    ),
                    "data", packets
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}
