package com.railbit.tcasanalysis.controller.cmsabndashboardcontroller;

import com.railbit.tcasanalysis.DTO.ResponseDTO;
import com.railbit.tcasanalysis.entity.User;
import com.railbit.tcasanalysis.entity.analysis.StringAndCount;
import com.railbit.tcasanalysis.service.CMSAbnDashBoardService;
import com.railbit.tcasanalysis.service.DashBoardService1;
import com.railbit.tcasanalysis.service.DashboardService;
import com.railbit.tcasanalysis.service.UserService;
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
import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
@RequestMapping("/tcasapi/cmsAbnDashboard")
public class CMSAbnDashboardController {
    private static final Logger log = LoggerFactory.getLogger(CMSAbnDashboardController.class);
    private final CMSAbnDashBoardService cmsAbnDashBoardService;
    private final UserService userService;

    @GetMapping("/getMajorIncidentsStationWise")
    public ResponseDTO<?> getMajorIncidentsStationWise(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate toDate,
            @RequestParam(required = false) Long zoneId,
            @RequestParam(required = false) Long divisionId
    ){
//        log.info("Filter {}",filter);
        User user = userService.getUserByUserId(HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication()));

        if (user.getRole().getName().equalsIgnoreCase("ROLE_DIVISION") || user.getRole().getName().equalsIgnoreCase("ROLE_OEM")) {
            divisionId = Long.valueOf(user.getDivision().getId());
            zoneId = Long.valueOf(user.getDivision().getZone().getId());
        } else if (user.getRole().getName().equalsIgnoreCase("ROLE_ZONE")){
            zoneId = Long.valueOf(user.getZone().getId());
        }
        return ResponseDTO.<List<StringAndCount>>builder()
                .data(cmsAbnDashBoardService.findTopStationsWithMostIssuesBetweenDates(zoneId,divisionId,fromDate,toDate, PageRequest.of(0, 5)))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
//
    @GetMapping("/getMajorIncidentsLocoWise")
    public ResponseDTO<?> getMajorIncidentsLocoWise(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate toDate,
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
        return ResponseDTO.<List<StringAndCount>>builder()
                .data(cmsAbnDashBoardService.findTopLocosWithMostIssuesBetweenDates(zoneId,divisionId,fromDate,toDate, PageRequest.of(0, 5)))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
//
    @GetMapping("/getMajorIncidentsCauseWise")
    public ResponseDTO<?> getMajorIncidentsCauseWise(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate toDate,
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
        return ResponseDTO.<List<StringAndCount>>builder()
                .data(cmsAbnDashBoardService.findTopCausesWithMostIssuesBetweenDates(zoneId,divisionId,fromDate,toDate, PageRequest.of(0, 5)))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

//    @GetMapping("/getMajorIncidentsOEMWise")
//    public ResponseDTO<?> getMajorIncidentsOEMWise(@RequestParam String filter){
//        log.info("Filter {}",filter);
//        return ResponseDTO.<List<StringAndCount>>builder()
//                .data(dashboardService.getMajorIncidentsOEMWise(filter))
//                .message(Constants.SUCCESS_MSG)
//                .status(HttpStatus.OK.value())
//                .build();
//    }

    @GetMapping("/getMajorIncidentsDivisionWise")
    public ResponseDTO<?> getMajorIncidentsDivisionWise(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate toDate,
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
        return ResponseDTO.<List<StringAndCount>>builder()
                .data(cmsAbnDashBoardService.findTopDivisionsWithMostIssuesBetweenDates(zoneId,divisionId,fromDate,toDate, PageRequest.of(0, 5)))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getDashboardCountCards")
    public ResponseDTO<?> getDashboardCounts(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate toDate,
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

        log.info("From Date : {}", fromDate);
        log.info("To Date : {}", toDate);
        log.info("Zone : {}", zoneId);
        log.info("Division : {}", divisionId);

        return ResponseDTO.<List<StringAndCount>>builder()
                .data(cmsAbnDashBoardService.getDashboardCounts(zoneId, divisionId, fromDate, toDate))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

}
