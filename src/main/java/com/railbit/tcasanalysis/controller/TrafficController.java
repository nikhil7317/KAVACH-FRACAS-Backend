package com.railbit.tcasanalysis.controller;

import com.railbit.tcasanalysis.DTO.TrafficData;
import com.railbit.tcasanalysis.DTO.TrafficReportResponse;
import com.railbit.tcasanalysis.service.RrdToolService;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.sql.DataSource;

@RestController
@CrossOrigin("*")
@RequestMapping("/tcasapi/traffic")
public class TrafficController {

    private final RrdToolService rrdToolService;

    public TrafficController(RrdToolService rrdToolService) {
        this.rrdToolService = rrdToolService;
    }

    // ============================
    // TEST CACTI DATASOURCE HERE
    // ============================

    @Autowired
    @Qualifier("cactiDataSource")
    private DataSource cactiDS;

    @GetMapping("/check-cacti-url")
    public String checkCactiURL() throws Exception {
        return cactiDS.getConnection().getMetaData().getURL();
    }

    // ============================
    // ORIGINAL ENDPOINTS
    // ============================

    @GetMapping("/{filename}")
    public TrafficData getTraffic(@PathVariable String filename) throws Exception {
        String rrdPath = "C:/xampp/htdocs/cacti/rra/" + filename + ".rrd";
        return rrdToolService.fetchTraffic(rrdPath);
    }

    @GetMapping("/report/{filename}/{deviceId}")
    public TrafficReportResponse getFullReport(
            @PathVariable String filename,
            @PathVariable Integer deviceId
    ) throws Exception {
        String rrdPath = "C:/xampp/htdocs/cacti/rra/" + filename + ".rrd";
        return rrdToolService.fetchFullReport(rrdPath, deviceId);
    }
}
