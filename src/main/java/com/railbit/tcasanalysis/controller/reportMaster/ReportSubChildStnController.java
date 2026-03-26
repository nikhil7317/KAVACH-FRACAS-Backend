package com.railbit.tcasanalysis.controller.reportMaster;

import com.railbit.tcasanalysis.entity.ReportSubChildStn;
import com.railbit.tcasanalysis.service.ReportSubChildStnService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tcasapi/reportSubChild")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReportSubChildStnController {

    private final ReportSubChildStnService service;

    @GetMapping("/{childId}")
    public ResponseEntity<List<ReportSubChildStn>> getByChildId(
            @PathVariable Integer childId) {

        return ResponseEntity.ok(service.getByChildId(childId));
    }
}