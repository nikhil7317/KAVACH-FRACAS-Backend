package com.railbit.tcasanalysis.controller;


import com.railbit.tcasanalysis.DTO.RepeatedRFIDReportDTO;
import com.railbit.tcasanalysis.service.RepeatedRFIDReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * Repeated RFID Report
 *
 * GET /api/v1/reports/repeated-rfid
 *
 * Query params (all optional):
 *   locoId    – filter to a specific loco
 *   fromDate  – start of event window  (yyyy-MM-dd HH:mm:ss)
 *   toDate    – end   of event window  (yyyy-MM-dd HH:mm:ss)
 *   page      – 0-based page index     (default 0)
 *   size      – records per page       (default 20, max 200)
 */
@Slf4j
@RestController
@RequestMapping("/tcasapi")
@RequiredArgsConstructor
public class RepeatedRFIDReportController {

    private final RepeatedRFIDReportService repeatedRFIDReportService;

    @GetMapping("/repeated-rfid")
    public ResponseEntity<Page<RepeatedRFIDReportDTO>> getRepeatedRFIDReport(
            @RequestParam(required = false) Integer locoId,
            @RequestParam(required = false) Integer lastRfidTag,

            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date fromDate,

            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date toDate,

            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("RepeatedRFIDReport — locoId={}, lastRfidTag={}, fromDate={}, toDate={}, page={}, size={}",
                locoId, lastRfidTag, fromDate, toDate, page, size);

        Page<RepeatedRFIDReportDTO> response =
                repeatedRFIDReportService.getRepeatedRFIDReport(locoId, lastRfidTag, fromDate, toDate, page, size);

        return ResponseEntity.ok(response);
    }
}