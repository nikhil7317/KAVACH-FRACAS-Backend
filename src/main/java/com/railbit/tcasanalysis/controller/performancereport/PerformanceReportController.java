package com.railbit.tcasanalysis.controller.performancereport;

import com.railbit.tcasanalysis.DTO.ResponseDTO;
import com.railbit.tcasanalysis.DTO.performanceReport.OverallPerformanceReportDTO;
import com.railbit.tcasanalysis.entity.User;
import com.railbit.tcasanalysis.entity.analysis.StringAndCount;
import com.railbit.tcasanalysis.service.PerformanceReportService;
import com.railbit.tcasanalysis.util.Constants;
import com.railbit.tcasanalysis.util.HelpingHand;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
@RequestMapping("/tcasapi/overallPerformanceReport")
public class PerformanceReportController {

    private static final Logger log = LogManager.getLogger(PerformanceReportController.class);
    private final PerformanceReportService performanceReportService;

    @GetMapping("/getOverallPerformanceReportMonthWise")
    public ResponseDTO<?> getOverallPerformanceReportMonthWise(
            @RequestParam(required = false) Long zoneId,
            @RequestParam(required = false) Long divisionId,
            @RequestParam(required = false) Integer year
    ) {

//        log.info("Year : {}", year);
//        User user = userService.getUserByUserId(HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication()));
//
//        if (user.getRole().getName().equalsIgnoreCase("ROLE_DIVISION") || user.getRole().getName().equalsIgnoreCase("ROLE_OEM")) {
//            divisionId = Long.valueOf(user.getDivision().getId());
//            zoneId = Long.valueOf(user.getDivision().getZone().getId());
//        } else if (user.getRole().getName().equalsIgnoreCase("ROLE_ZONE")){
//            zoneId = Long.valueOf(user.getDivision().getZone().getId());
//        }

        return ResponseDTO.<List<OverallPerformanceReportDTO>>builder()
                .data(performanceReportService.getOverallPerformanceReportMonthWise(zoneId,divisionId,year))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();

    }

}
