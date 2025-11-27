package com.railbit.tcasanalysis.controller;

import com.railbit.tcasanalysis.DTO.RequestDto;
import com.railbit.tcasanalysis.service.IncidentAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tcasapi/analysis")
public class IncidentAnalysisController {

    @Autowired
    private IncidentAnalysisService apiService;

    @PostMapping("/send")
    public String sendData(@RequestBody RequestDto requestDto) {
        return apiService.callTeammateApi(requestDto);
    }
}
