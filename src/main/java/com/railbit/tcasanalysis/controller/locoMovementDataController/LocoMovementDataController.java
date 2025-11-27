package com.railbit.tcasanalysis.controller.locoMovementDataController;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.type.DateTime;
import com.railbit.tcasanalysis.DTO.LocoMovementDTO;
import com.railbit.tcasanalysis.DTO.ResponseDTO;
import com.railbit.tcasanalysis.entity.LocoMovementData;
import com.railbit.tcasanalysis.service.LocoMovementDataService;
import com.railbit.tcasanalysis.util.Constants;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tcasapi/locomovement")
public class LocoMovementDataController {

    private static final Logger log = LogManager.getLogger(LocoMovementDataController.class);
    @Autowired
    private LocoMovementDataService locoDataService;

    @GetMapping("/weekly-summary")
    public ResponseEntity<List<Map<String, Object>>> getWeeklySummary(
            @RequestParam(value = "weekStart", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {
        if (weekStart == null) {
            // Default to last week's Monday
            weekStart = LocalDate.now().with(DayOfWeek.MONDAY).minusWeeks(1);
        }
        List<Map<String, Object>> summary = locoDataService.getWeeklySummary(weekStart);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/view/")
    public ResponseDTO<Object>viewLocoMovementData(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "100") Integer size,
                                                   Integer locoId, Integer stationId,
                                                   @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime startDate,
                                                   @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime endDate) throws ParseException {

        log.info("Start Date : {}",startDate);
        log.info("End Date : {}",endDate);

        Map<String, Object> locoMovementData = locoDataService.getFilteredLocoMovementData(page, size, locoId, stationId, startDate, endDate);
        return ResponseDTO.builder()
                .status(HttpStatus.OK.value())
                .message(Constants.SUCCESS_MSG)
                .data(locoMovementData.get("data"))
                .totalRecords(Integer.valueOf(locoMovementData.get("totalRecords").toString()))
                .build();
    }

    @PostMapping("/addLocoMovementData/")
    public ResponseDTO<?>addLocoMovementData(@Valid @RequestBody LocoMovementDTO locoMovementData) {
        // System.out.println("Running");
        return ResponseDTO.<Object>builder()
                .data(locoDataService.postLocoMovementData(locoMovementData))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/running-locos")
    public ResponseDTO<?>addLocoMovementData() {
//        System.out.println("Running");
        Map<String, String> locoData = locoDataService.getRunningLocosWithLastPacket();
        return ResponseDTO.<Object>builder()
                .data(locoData)
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .totalRecords(locoData.size())
                .build();
    }

}