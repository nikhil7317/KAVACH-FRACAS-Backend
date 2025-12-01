package com.railbit.tcasanalysis.controller;

import com.railbit.tcasanalysis.DTO.TrafficData;
import com.railbit.tcasanalysis.DTO.TrafficReportResponse;
import com.railbit.tcasanalysis.service.RrdToolService;

import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/tcasapi/traffic")
public class TrafficController {

    private final RrdToolService rrdToolService;

    public TrafficController(RrdToolService rrdToolService) {
        this.rrdToolService = rrdToolService;
    }

    @GetMapping("/{filename}")
    public TrafficData getTraffic(
            @PathVariable String filename,
            @RequestParam(required = false) Long startDate,
            @RequestParam(required = false) Long endDate
    ) throws Exception {

        String rrdPath = "C:/xampp/htdocs/cacti/rra/" + filename + ".rrd";
        return rrdToolService.fetchTraffic(rrdPath, startDate, endDate);
    }


    @GetMapping("/report/{filename}/{deviceId}")
    public TrafficReportResponse getFullReport(
            @PathVariable String filename,
            @PathVariable Integer deviceId,
            @RequestParam(required = false) Long startDate,
            @RequestParam(required = false) Long endDate
    ) throws Exception {

        String rrdPath = "C:/xampp/htdocs/cacti/rra/" + filename + ".rrd";
        return rrdToolService.fetchFullReport(rrdPath, deviceId, startDate, endDate);
    }

}
