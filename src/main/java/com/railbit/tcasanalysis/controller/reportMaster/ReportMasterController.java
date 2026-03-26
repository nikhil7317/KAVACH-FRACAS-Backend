package com.railbit.tcasanalysis.controller.reportMaster;

import com.railbit.tcasanalysis.entity.ReportMaster;
import com.railbit.tcasanalysis.service.ReportMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tcasapi/reportMaster")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // restrict in production
public class ReportMasterController {

    private final ReportMasterService service;

    @GetMapping
    public ResponseEntity<List<ReportMaster>> getAllReportMasters() {
        return ResponseEntity.ok(service.getAllReportMasters());
    }
}