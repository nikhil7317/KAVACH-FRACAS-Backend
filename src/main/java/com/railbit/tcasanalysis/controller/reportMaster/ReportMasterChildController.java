package com.railbit.tcasanalysis.controller.reportMaster;

import com.railbit.tcasanalysis.entity.ReportMasterChild;
import com.railbit.tcasanalysis.service.ReportMasterChildService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tcasapi/reportChild")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReportMasterChildController {

    private final ReportMasterChildService service;

    @GetMapping("/{reportMasterId}")
    public ResponseEntity<List<ReportMasterChild>> getByReportMasterId(
            @PathVariable Integer reportMasterId) {

        return ResponseEntity.ok(service.getByReportMasterId(reportMasterId));
    }
}