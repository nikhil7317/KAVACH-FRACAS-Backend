package com.railbit.tcasanalysis.controller.dashboardcontroller;

import com.railbit.tcasanalysis.DTO.LastTripDateNoDTO;
import com.railbit.tcasanalysis.DTO.ResponseDTO;
import com.railbit.tcasanalysis.DTO.dashboard.OpenTicketWithOEMDTO;
import com.railbit.tcasanalysis.entity.Firm;
import com.railbit.tcasanalysis.entity.LocoMovementData;
import com.railbit.tcasanalysis.entity.User;
import com.railbit.tcasanalysis.entity.analysis.SingleIncidentAnalysisChartData;
import com.railbit.tcasanalysis.entity.analysis.StringAndCount;
import com.railbit.tcasanalysis.entity.analysis.YearlyReportExcelResponse;
import com.railbit.tcasanalysis.entity.analysis.oembargraph.BarGroupListContainer;
import com.railbit.tcasanalysis.entity.analysis.oemwiseanalysis.FirmAndAvgDay;
import com.railbit.tcasanalysis.entity.analysis.oemwiseanalysis.FirmIssueCategoryAvgDay;
import com.railbit.tcasanalysis.entity.analysis.oemwiseanalysis.FirmsAndStatusCounts;
import com.railbit.tcasanalysis.entity.analysis.yearlygraph.FirmMonthStatusCount;
import com.railbit.tcasanalysis.entity.analysis.yearlygraph.YearlyGraphData;
import com.railbit.tcasanalysis.entity.nmspackets.AlertsCount;
import com.railbit.tcasanalysis.service.*;
import com.railbit.tcasanalysis.util.Constants;
import com.railbit.tcasanalysis.util.HelpingHand;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
@RequestMapping("/tcasapi/dashboard")
public class DashboardController {
    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);
    private final DashboardService dashboardService;
    private final DashBoardService1 dashboardService1;
    private final LocoMovementDataService locoMovementDataService;
    private final UserService userService;
    private final FirmService firmService;

    @GetMapping("/getMajorIncidentsStationWise")
    public ResponseDTO<?> getMajorIncidentsStationWise(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate toDate,
            @RequestParam(required = false) Long zoneId,
            @RequestParam(required = false) Long divisionId,
            @RequestParam(required = false) Long firmId
    ){
        User user = userService.getUserByUserId(HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication()));

        if (user.getRole().getName().equalsIgnoreCase("ROLE_DIVISION") || user.getRole().getName().equalsIgnoreCase("ROLE_OEM")) {
            divisionId = Long.valueOf(user.getDivision().getId());
            zoneId = Long.valueOf(user.getDivision().getZone().getId());
        } else if (user.getRole().getName().equalsIgnoreCase("ROLE_ZONE")){
            zoneId = Long.valueOf(user.getZone().getId());
        }
        return ResponseDTO.<List<StringAndCount>>builder()
                .data(dashboardService1.findTopStationsWithMostIssuesBetweenDates(zoneId,divisionId,firmId,fromDate,toDate, PageRequest.of(0, 5)))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getMajorIncidentsLocoWise")
    public ResponseDTO<?> getMajorIncidentsLocoWise(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate toDate,
            @RequestParam(required = false) Long zoneId,
            @RequestParam(required = false) Long divisionId,
            @RequestParam(required = false) Long firmId
    ){
        User user = userService.getUserByUserId(HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication()));

        if (user.getRole().getName().equalsIgnoreCase("ROLE_DIVISION") || user.getRole().getName().equalsIgnoreCase("ROLE_OEM")) {
            divisionId = Long.valueOf(user.getDivision().getId());
            zoneId = Long.valueOf(user.getDivision().getZone().getId());
        } else if (user.getRole().getName().equalsIgnoreCase("ROLE_ZONE")){
            zoneId = Long.valueOf(user.getZone().getId());
        }
        return ResponseDTO.<List<StringAndCount>>builder()
                .data(dashboardService1.findTopLocosWithMostIssuesBetweenDates(zoneId,divisionId,firmId,fromDate,toDate, PageRequest.of(0, 5)))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getMajorIncidentsCauseWise")
    public ResponseDTO<?> getMajorIncidentsCauseWise(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate toDate,
            @RequestParam(required = false) Long zoneId,
            @RequestParam(required = false) Long divisionId,
            @RequestParam(required = false) Long firmId
    ){
        User user = userService.getUserByUserId(HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication()));

        if (user.getRole().getName().equalsIgnoreCase("ROLE_DIVISION") || user.getRole().getName().equalsIgnoreCase("ROLE_OEM")) {
            divisionId = Long.valueOf(user.getDivision().getId());
            zoneId = Long.valueOf(user.getDivision().getZone().getId());
        } else if (user.getRole().getName().equalsIgnoreCase("ROLE_ZONE")){
            zoneId = Long.valueOf(user.getZone().getId());
        }
        return ResponseDTO.<List<StringAndCount>>builder()
                .data(dashboardService1.findTopCausesWithMostIssuesBetweenDates(zoneId,divisionId,firmId,fromDate,toDate, PageRequest.of(0, 5)))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getMajorIncidentsOEMWise")
    public ResponseDTO<?> getMajorIncidentsOEMWise(@RequestParam String filter){
        log.info("Filter {}",filter);
        return ResponseDTO.<List<StringAndCount>>builder()
                .data(dashboardService.getMajorIncidentsOEMWise(filter))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getMajorIncidentsDivisionWise")
    public ResponseDTO<?> getMajorIncidentsDivisionWise(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate toDate,
            @RequestParam(required = false) Long zoneId,
            @RequestParam(required = false) Long divisionId,
            @RequestParam(required = false) Long firmId
    ){

        User user = userService.getUserByUserId(HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication()));

        if (user.getRole().getName().equalsIgnoreCase("ROLE_DIVISION") || user.getRole().getName().equalsIgnoreCase("ROLE_OEM")) {
            divisionId = Long.valueOf(user.getDivision().getId());
            zoneId = Long.valueOf(user.getDivision().getZone().getId());
        } else if (user.getRole().getName().equalsIgnoreCase("ROLE_ZONE")){
            zoneId = Long.valueOf(user.getZone().getId());
        }
        return ResponseDTO.<List<StringAndCount>>builder()
                .data(dashboardService1.findTopDivisionWithMostIssuesBetweenDates(zoneId,divisionId,firmId,fromDate,toDate, PageRequest.of(0, 5)))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getDashboardCountCards")
    public ResponseDTO<?> getDashboardCounts(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate toDate,
            @RequestParam(required = false) Long zoneId,
            @RequestParam(required = false) Long divisionId,
            @RequestParam(required = false) Long firmId
    ) {

        log.info("Dashboard Counts Filter From Date : - {}", fromDate);
        log.info("Dashboard Counts Filter To Date : - {}", toDate);
        log.info("Dashboard Counts Filter Zone ID : - {}", zoneId);
        log.info("Dashboard Counts Filter Division ID : - {}", divisionId);

        User user = userService.getUserByUserId(HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication()));

        if (user.getRole().getName().equalsIgnoreCase("ROLE_DIVISION") || user.getRole().getName().equalsIgnoreCase("ROLE_OEM")) {
            divisionId = Long.valueOf(user.getDivision().getId());
            zoneId = Long.valueOf(user.getDivision().getZone().getId());
        } else if (user.getRole().getName().equalsIgnoreCase("ROLE_ZONE")){
            zoneId = Long.valueOf(user.getZone().getId());
        }

        return ResponseDTO.<List<StringAndCount>>builder()
                .data(dashboardService1.getDashboardCounts(zoneId,divisionId,firmId,fromDate,toDate,user.getRole().getName()))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getLastTripDate")
    public ResponseDTO<?> getLastTripDate(
            @RequestParam(required = false) Long zoneId,
            @RequestParam(required = false) Long divisionId
    ) {
        // Retrieve the authenticated user
        User user = userService.getUserByUserId(
                HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication())
        );

        // Adjust zoneId and divisionId based on user role
        if (user.getRole().getName().equalsIgnoreCase("ROLE_DIVISION") || user.getRole().getName().equalsIgnoreCase("ROLE_OEM")) {
            divisionId = Long.valueOf(user.getDivision().getId());
            zoneId = Long.valueOf(user.getDivision().getZone().getId());
        } else if (user.getRole().getName().equalsIgnoreCase("ROLE_ZONE")) {
            zoneId = Long.valueOf(user.getZone().getId());
        }

        // Call service to get the last trip date
        LocalDate lastTripDate = dashboardService1.getLastTripDateByZoneAndDivision(zoneId, divisionId);

        // Return the response
        return ResponseDTO.<LocalDate>builder()
                .data(lastTripDate)
                .message(lastTripDate != null ? Constants.SUCCESS_MSG : "No trip date found for the given criteria.")
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getLastTripDateNo")
    public ResponseDTO<?> getLastTripDateNo(
            @RequestParam(required = false) Long zoneId,
            @RequestParam(required = false) Long divisionId
    ) {
        // Retrieve the authenticated user
        User user = userService.getUserByUserId(
                HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication())
        );

        // Adjust zoneId and divisionId based on user role
        if (user.getRole().getName().equalsIgnoreCase("ROLE_DIVISION") || user.getRole().getName().equalsIgnoreCase("ROLE_OEM")) {
            divisionId = Long.valueOf(user.getDivision().getId());
            zoneId = Long.valueOf(user.getDivision().getZone().getId());
        } else if (user.getRole().getName().equalsIgnoreCase("ROLE_ZONE")) {
            zoneId = Long.valueOf(user.getZone().getId());
        }

        // Call service to get the last trip date
        LastTripDateNoDTO lastTripDateNo = dashboardService1.getLastTripDateAndTripNoByZoneAndDivision(zoneId, divisionId);

        // Return the response
        return ResponseDTO.<LastTripDateNoDTO>builder()
                .data(lastTripDateNo)
                .message(lastTripDateNo != null ? Constants.SUCCESS_MSG : "No trip date found for the given criteria.")
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getOpenTicketsFirmWise")
    public ResponseDTO<List<OpenTicketWithOEMDTO>> getOpenTicketsFirmWise(
            @RequestParam(required = false) Long zoneId,
            @RequestParam(required = false) Long divisionId
    ) {

        // Retrieve the authenticated user
        User user = userService.getUserByUserId(
                HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication())
        );

        List<Firm> firmList = firmService.getAllFirm();

        // Adjust zoneId and divisionId based on user role
        if (user.getRole().getName().equalsIgnoreCase("ROLE_DIVISION")) {
            divisionId = Long.valueOf(user.getDivision().getId());
            zoneId = Long.valueOf(user.getDivision().getZone().getId());
        } else if (user.getRole().getName().equalsIgnoreCase("ROLE_ZONE")) {
            zoneId = Long.valueOf(user.getZone().getId());
        } else if (user.getRole().getName().equalsIgnoreCase("ROLE_OEM")) {
            divisionId = Long.valueOf(user.getDivision().getId());
            zoneId = Long.valueOf(user.getDivision().getZone().getId());
            Integer firmId = user.getFirm().getId();
            firmList = new ArrayList<>();
            firmList.add(firmService.getFirmById(firmId));
        }

        List<OpenTicketWithOEMDTO> openTicketWithOEMDTOList = dashboardService1.getOpenTicketsWithOEMDetails(zoneId,divisionId,firmList,true);

        // Return the response
        return ResponseDTO.<List<OpenTicketWithOEMDTO>>builder()
                .data(openTicketWithOEMDTOList)
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();

    }

    @GetMapping("/getAlertCounts")
    public ResponseDTO<?> getAlertCounts(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate toDate
    ){
        return ResponseDTO.<AlertsCount>builder()
                .data(locoMovementDataService.getAlertsCount(fromDate,toDate))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getLocoMovementDataByAlert")
    public ResponseDTO<List<LocoMovementData>> getLocoMovementDataByAlert(
            @RequestParam(required = false) String alertType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate toDate
    ){
        return ResponseDTO.<List<LocoMovementData>>builder()
                .data(locoMovementDataService.getLocoMovementListByAlertAndDateRange(alertType,fromDate,toDate))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getAvgClosureTime")
    public ResponseDTO<Map<String, Object>> getAvgClosureTime(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate toDate
    ){
        return ResponseDTO.<Map<String, Object>>builder()
                .data(dashboardService1.getAvgClosureTime(fromDate,toDate))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }



}
