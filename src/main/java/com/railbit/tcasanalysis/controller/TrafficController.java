package com.railbit.tcasanalysis.controller;


import com.railbit.tcasanalysis.DTO.TrafficData;
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
    public TrafficData getTraffic(@PathVariable String filename) throws Exception {

        String rrdPath = "C:/xampp/htdocs/cacti/rra/" + filename + ".rrd";

        return rrdToolService.fetchTraffic(rrdPath);
    }
}

