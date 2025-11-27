package com.railbit.tcasanalysis.controller.analysiscontroller;

import com.google.gson.JsonArray;
import com.railbit.tcasanalysis.DTO.MonthWiseData;
import com.railbit.tcasanalysis.entity.User;
import com.railbit.tcasanalysis.entity.analysis.oemwiseanalysis.FirmAndAvgDay;
import com.railbit.tcasanalysis.DTO.ResponseDTO;
import com.railbit.tcasanalysis.entity.analysis.SingleIncidentAnalysisChartData;
import com.railbit.tcasanalysis.entity.analysis.StringAndCount;
import com.railbit.tcasanalysis.entity.analysis.YearlyReportExcelResponse;
import com.railbit.tcasanalysis.entity.analysis.oembargraph.BarGroupListContainer;
import com.railbit.tcasanalysis.entity.analysis.oemwiseanalysis.FirmIssueCategoryAvgDay;
import com.railbit.tcasanalysis.entity.analysis.oemwiseanalysis.FirmsAndStatusCounts;
import com.railbit.tcasanalysis.entity.analysis.yearlygraph.FirmMonthStatusCount;
import com.railbit.tcasanalysis.entity.analysis.yearlygraph.YearlyGraphData;
import com.railbit.tcasanalysis.service.GraphsAndChartsService;
import com.railbit.tcasanalysis.service.TcasBreakingInspectionService;
import com.railbit.tcasanalysis.service.UserService;
import com.railbit.tcasanalysis.util.Constants;
import com.railbit.tcasanalysis.util.HelpingHand;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
@RequestMapping("/tcasapi/analysis")
public class AnalysisController {
    private static final Logger log = LoggerFactory.getLogger(AnalysisController.class);
    private final TcasBreakingInspectionService tcasBreakingInspectionService;
    private final GraphsAndChartsService graphsAndChartsService;
    private final UserService userService;

    @GetMapping("/monthlyOverview")
    public ResponseDTO<?> getMonthlyOverview(@RequestParam int month,
                                             @RequestParam int year){
//        System.out.println(month + " " + year);
        return ResponseDTO.<List<SingleIncidentAnalysisChartData>>builder()
                .data(tcasBreakingInspectionService.getCountsByIssueCategoryForMonthAndYear(month,year))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    //Gets Pie Chart Graph Data according to Firm in a Date Range
    @GetMapping("/incidentsOverViewDatewise")
    public ResponseDTO<?> incidentsOverViewDatewise(
            @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate fromDate,
            @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate toDate,
            @RequestParam(required = false) Long zoneId,
            @RequestParam(required = false) Long divisionId
    ){
        User user = userService.getUserByUserId(HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication()));

        if (user.getRole().getName().equalsIgnoreCase("ROLE_DIVISION") || user.getRole().getName().equalsIgnoreCase("ROLE_OEM")) {
            divisionId = Long.valueOf(user.getDivision().getId());
            zoneId = Long.valueOf(user.getDivision().getZone().getId());
        } else if (user.getRole().getName().equalsIgnoreCase("ROLE_ZONE")){
            zoneId = Long.valueOf(user.getZone().getId());
        }
        return ResponseDTO.<List<SingleIncidentAnalysisChartData>>builder()
                .data(graphsAndChartsService.getIncidentsOverViewDatewise(zoneId,divisionId,fromDate.atTime(LocalTime.of(0, 0)),toDate.atTime(LocalTime.of(23, 59))))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/yearlyReport")
    public ResponseDTO<?> getYearlyReport(@RequestParam int divisionId){
//        log.info("Data : {}",graphsAndChartsService.getIssueWiseYearlyGraphData(2024));
        return ResponseDTO.<YearlyReportExcelResponse>builder()
                .data(tcasBreakingInspectionService.getYearlyReportExcelResponse(divisionId))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    // Yearly Graph's Data for Dashboards
    @GetMapping("/getIssueWiseYearlyGraphData")
    public ResponseDTO<?> getIssueWiseYearlyGraphData(
            @RequestParam(required = false) Long zoneId,
            @RequestParam(required = false) Long divisionId
    ) {
        User user = userService.getUserByUserId(HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication()));

        if (user.getRole().getName().equalsIgnoreCase("ROLE_DIVISION") || user.getRole().getName().equalsIgnoreCase("ROLE_OEM")) {
            divisionId = Long.valueOf(user.getDivision().getId());
            zoneId = Long.valueOf(user.getDivision().getZone().getId());
        } else if (user.getRole().getName().equalsIgnoreCase("ROLE_ZONE")){
            zoneId = Long.valueOf(user.getZone().getId());
        }
        return ResponseDTO.<List<MonthWiseData>>builder()
                .data(graphsAndChartsService.getIssueWiseYearlyGraphData(zoneId, divisionId))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    //Gets Pie Chart Graph Data according to Firm in a Date Range
    @GetMapping("/firmWiseOverview")
    public ResponseDTO<?> getCountsByFirmForMonthAndYear(@RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                                         @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                                                         @RequestParam int firmId){
        return ResponseDTO.<List<SingleIncidentAnalysisChartData>>builder()
                .data(graphsAndChartsService.getCountsByFirmAndDate(fromDate.atTime(LocalTime.of(0, 0)),toDate.atTime(LocalTime.of(23, 59)),firmId))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    // Firm Wise Bar Graph's Data for OEM Analysis
    @GetMapping("/getFirmWiseBarGraphData")
    public ResponseDTO<?> getFirmWiseBarGraphData(@RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                                  @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return ResponseDTO.<List<BarGroupListContainer>>builder()
                .data(graphsAndChartsService.getFirmWiseBarGraphData(fromDate.atTime(LocalTime.of(0, 0)),toDate.atTime(LocalTime.of(23, 59))))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getDashboardCounts")
    public ResponseDTO<?> getDashboardCounts() {
        return ResponseDTO.<List<StringAndCount>>builder()
                .data(graphsAndChartsService.getDashboardCounts())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getAvgDaysTakenToCloseByFirm")
    public ResponseDTO<?> getAvgDaysTakenToCloseByFirm() {
        return ResponseDTO.<List<FirmAndAvgDay>>builder()
                .data(graphsAndChartsService.getAvgDaysTakenToCloseByFirm())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getAvgDaysTakenToCloseByFirmAndIssueCategory")
    public ResponseDTO<?> getAvgDaysTakenToCloseByFirmAndIssueCategory() {
        return ResponseDTO.<List<FirmIssueCategoryAvgDay>>builder()
                .data(graphsAndChartsService.getAvgDaysTakenToCloseByFirmAndIssueCategory())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getAvgDaysTakenToCloseByFirmAndModeChange")
    public ResponseDTO<?> getAvgDaysTakenToCloseByFirmAndModeChange() {
        return ResponseDTO.<List<FirmAndAvgDay>>builder()
                .data(graphsAndChartsService.getAvgDaysTakenToCloseByFirmAndModeChange())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getAvgDaysTakenToCloseByFirmAndUndesirableBreaking")
    public ResponseDTO<?> getAvgDaysTakenToCloseByFirmAndUndesirableBreaking() {
        return ResponseDTO.<List<FirmAndAvgDay>>builder()
                .data(graphsAndChartsService.getAvgDaysTakenToCloseByFirmAndUndesirableBreaking())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getCountsByFirmsAndStatus")
    public ResponseDTO<?> getCountsByFirmsAndStatus() {
        return ResponseDTO.<List<FirmsAndStatusCounts>>builder()
                .data(graphsAndChartsService.getCountsByFirmsAndStatus())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getMonthlyInspectionCountsForHBLGroupByStatus")
    public ResponseDTO<?> getMonthlyInspectionCountsForHBLGroupByStatus(@RequestParam int year) {
        return ResponseDTO.<List<FirmMonthStatusCount>>builder()
                .data(graphsAndChartsService.getMonthlyInspectionCountsByFirmGroupByStatus(year,1))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getMonthlyInspectionCountsForKernexGroupByStatus")
    public ResponseDTO<?> getMonthlyInspectionCountsForKernexGroupByStatus(@RequestParam int year) {
        return ResponseDTO.<List<FirmMonthStatusCount>>builder()
                .data(graphsAndChartsService.getMonthlyInspectionCountsByFirmGroupByStatus(year,2))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getMonthlyInspectionCountsForMedhaGroupByStatus")
    public ResponseDTO<?> getMonthlyInspectionCountsForMedhaGroupByStatus(@RequestParam int year) {
        return ResponseDTO.<List<FirmMonthStatusCount>>builder()
                .data(graphsAndChartsService.getMonthlyInspectionCountsByFirmGroupByStatus(year,3))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getCountsByModeChangeGroupByFirm")
    public ResponseDTO<?> getCountsByModeChangeGroupByFirm() {
        return ResponseDTO.<List<FirmsAndStatusCounts>>builder()
                .data(graphsAndChartsService.getCountsByFirmAndIssueCategory(1))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getCountsByUndesirableBreakingGroupByFirm")
    public ResponseDTO<?> getCountsByUndesirableBreakingGroupByFirm() {
        return ResponseDTO.<List<FirmsAndStatusCounts>>builder()
                .data(graphsAndChartsService.getCountsByFirmAndIssueCategory(2))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
}
