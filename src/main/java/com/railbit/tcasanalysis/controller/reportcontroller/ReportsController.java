package com.railbit.tcasanalysis.controller.reportcontroller;

import com.railbit.tcasanalysis.DTO.*;
import com.railbit.tcasanalysis.DTO.reports.*;
import com.railbit.tcasanalysis.entity.TcasBreakingInspection;
import com.railbit.tcasanalysis.entity.User;
import com.railbit.tcasanalysis.entity.analysis.PieChartData;
import com.railbit.tcasanalysis.service.DashboardService;
import com.railbit.tcasanalysis.service.ReportsService;
import com.railbit.tcasanalysis.service.UserService;
import com.railbit.tcasanalysis.util.Constants;
import com.railbit.tcasanalysis.util.HelpingHand;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
@RequestMapping("/tcasapi/report")
public class ReportsController {
    private static final Logger log = LoggerFactory.getLogger(ReportsController.class);
    private final UserService userService;
    private final ReportsService reportsService;

        @GetMapping("/getRepeatedIncidentsReport")
    public ResponseDTO<?> getRepeatedIncidentsReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate toDate,
            @RequestParam(required = false) Integer zoneId,
            @RequestParam(required = false) Integer divisionId,
            @RequestParam(required = false) Integer stationId,
            @RequestParam(required = false) String searchQuery,
            Pageable pageable){

        User user = new User();
        user.setId(HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication()));
        Long userId = user.getId();

        Page<CausesWiseRepeatedIncidentsReportDTO> data = reportsService.getRepeatedIncidentsReport(userId,stationId,divisionId,zoneId,fromDate,toDate,searchQuery,pageable);
        return ResponseDTO.<Page<CausesWiseRepeatedIncidentsReportDTO>>builder()
                .data(data)
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .totalRecords(data.getNumberOfElements())
                .build();
    }
    @GetMapping("/getRepeatedIncidentAnalysis")
        public ResponseDTO<?> getRepeatedIncidentAnalysis(
                @RequestParam String issueCategory,
                @RequestParam String possibleRootCause,
                @RequestParam String rootCauseSubCategory,
                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate fromDate,
                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate toDate,
                @RequestParam(required = false) Integer zoneId,
                @RequestParam(required = false) Integer divisionId,
                @RequestParam(required = false) Integer stationId) {

        User user = new User();
        user.setId(HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication()));
        Long userId = user.getId();

            RepeatedIncidentAnalysisDTO data = reportsService.getRepeatedIncidentAnalysis(
                    userId, stationId, divisionId, zoneId, fromDate, toDate,
                    issueCategory, possibleRootCause, rootCauseSubCategory
            );
    
            return ResponseDTO.<RepeatedIncidentAnalysisDTO>builder()
                    .data(data)
                    .message(Constants.SUCCESS_MSG)
                    .status(HttpStatus.OK.value())
                    .build();
        }


    @GetMapping("/getRepeatedIncidentTicketDetails")
    public ResponseDTO<?> getIncidentTicketDetails(
            @RequestParam(required = false) String issueCategory,
            @RequestParam(required = false) String possibleRootCause,
            @RequestParam(required = false) String rootCauseSubCategory,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate toDate,
            @RequestParam(required = false) Integer zoneId,
            @RequestParam(required = false) Integer divisionId,
            @RequestParam(required = false) Integer stationId) {

        // ✅ User verification and role-based filtering
        User user = new User();
        user.setId(HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication()));
        Long userId = user.getId();

        List<TicketDetailsDTO> result = reportsService.getIncidentTicketDetails(
                userId, issueCategory, possibleRootCause, rootCauseSubCategory,
                fromDate, toDate, zoneId, divisionId, stationId
        );

        return ResponseDTO.<Map<String, Object>>builder()
                .data(Map.of("ticketDetails", result))
                .message("Success")
                .status(HttpStatus.OK.value())
                .totalRecords(result.size())
                .build();
    }
    @GetMapping("/getLocoRepeatedIncidentAnalysis")
    public ResponseDTO<?> getLocoRepeatedIncidentAnalysis(
            @RequestParam String issueCategory,
            @RequestParam String possibleRootCause,
            @RequestParam String rootCauseSubCategory,
            @RequestParam(required = false) Integer zoneId,
            @RequestParam(required = false) Integer divisionId,
            @RequestParam(required = false) Integer stationId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate toDate,
            @RequestParam Integer locoId) {

        User user = new User();
        user.setId(HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication()));
        Long userId = user.getId();


        RepeatedIncidentAnalysisDTO data = reportsService.getLocoRepeatedIncidentAnalysis(
                userId, stationId, divisionId, zoneId,
                fromDate, toDate,
                issueCategory, possibleRootCause, rootCauseSubCategory,
                locoId
        );


        return ResponseDTO.<RepeatedIncidentAnalysisDTO>builder()
                .data(data)
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @GetMapping("/getLocoIncidentTicketDetails")
    public ResponseDTO<?> getLocoIncidentTicketDetails(
            @RequestParam(required = false) String issueCategory,
            @RequestParam(required = false) String possibleRootCause,
            @RequestParam(required = false) String rootCauseSubCategory,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate toDate,
            @RequestParam(required = false) Integer zoneId,
            @RequestParam(required = false) Integer divisionId,
            @RequestParam(required = false) Integer stationId,
            @RequestParam(required = false) Integer locoId
    ) {
        // ✅ User verification and role-based filtering
        User user = new User();
        user.setId(HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication()));
        Long userId = user.getId();

        // ✅ Call updated service method with locoId
        List<TicketDetailsDTO> result = reportsService.getLocoIncidentTicketDetails(
                userId, issueCategory, possibleRootCause, rootCauseSubCategory,
                fromDate, toDate, zoneId, divisionId, stationId, locoId
        );

        return ResponseDTO.<Map<String, Object>>builder()
                .data(Map.of("ticketDetails", result))
                .message("Success")
                .status(HttpStatus.OK.value())
                .totalRecords(result.size())
                .build();
    }

    @GetMapping("/getStationIncidentTicketDetails")
    public ResponseDTO<?> getStationIncidentTicketDetails(
            @RequestParam(required = false) String issueCategory,
            @RequestParam(required = false) String possibleRootCause,
            @RequestParam(required = false) String rootCauseSubCategory,
            @RequestParam(required = false) Integer divisionId,
            @RequestParam(required = false) Integer stationId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate toDate
    ) {
        // ✅ Extract userId from authentication
        User user = new User();
        user.setId(HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication()));
        Long userId = user.getId();

        // ✅ Call the new service method
        List<TicketDetailsDTO> result = reportsService.getStationIncidentTicketDetails(
                userId,
                issueCategory,
                possibleRootCause,
                rootCauseSubCategory,
                divisionId,
                stationId,
                fromDate,
                toDate
        );

        return ResponseDTO.<Map<String, Object>>builder()
                .data(Map.of("ticketDetails", result))
                .message("Success")
                .status(HttpStatus.OK.value())
                .totalRecords(result.size())
                .build();
    }

    @GetMapping("/getStationBasedRepeatedIncidentsReport")
    public ResponseDTO<?> getStationBasedRepeatedIncidentsReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate toDate,
            @RequestParam(required = false) Integer zoneId,
            @RequestParam(required = false) Integer divisionId,
            @RequestParam(required = false) Integer stationId,
            @RequestParam(required = false) String searchQuery,
            Pageable pageable){

        User user = new User();
        user.setId(HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication()));
        Long userId = user.getId();

        Page<StationRepeatedIncidentReportDTO> data = reportsService.getStationBasedRepeatedIncidentsReport(userId,stationId,divisionId,zoneId,fromDate,toDate,searchQuery,pageable);
//        Page<RepeatedIncidentsReportDTO> data = Page.empty();

        return ResponseDTO.<Page<StationRepeatedIncidentReportDTO>>builder()
                .data(data)
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .totalRecords(data.getNumberOfElements())
                .build();
    }


    @GetMapping("/getRootCauseWiseIncidentsReport")
    public ResponseDTO<?> getRootCauseWiseIncidentsReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate toDate,
            @RequestParam(required = false) Integer zoneId,
            @RequestParam(required = false) Integer divisionId,
            @RequestParam(required = false) Integer stationId,
            @RequestParam(required = false) String searchQuery,
            Pageable pageable) {

        User user = new User();
        user.setId(HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication()));
        Long userId = user.getId();

        Page<RootCausesWiseIncidentsReportDTO> data =
                reportsService.getRootCauseWiseIncidentsReport(userId,stationId,divisionId,zoneId,fromDate,toDate,searchQuery,pageable);
//        Page<RepeatedIncidentsReportDTO> data = Page.empty();

        return ResponseDTO.<Page<RootCausesWiseIncidentsReportDTO>>builder()
                .data(data)
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .totalRecords(data.getNumberOfElements())
                .build();
    }

    @GetMapping("/getOEMBasedRepeatedIncidentsReport")
    public ResponseDTO<?> getOEMBasedRepeatedIncidentsReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate toDate,
            @RequestParam(required = false) Integer zoneId,
            @RequestParam(required = false) Integer divisionId,
            @RequestParam(required = false) Integer stationId,
            @RequestParam(required = false) String searchQuery,
            Pageable pageable){

        User user = new User();
        user.setId(HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication()));
        Long userId = user.getId();

        Page<OEMRepeatedIncidentReportDTO> data = reportsService.getOEMBasedRepeatedIncidentsReport(userId,stationId,divisionId,zoneId,fromDate,toDate,searchQuery,pageable);
//        Page<RepeatedIncidentsReportDTO> data = Page.empty();

        return ResponseDTO.<Page<OEMRepeatedIncidentReportDTO>>builder()
                .data(data)
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .totalRecords(data.getNumberOfElements())
                .build();
    }

    @GetMapping("/getLocoBasedRepeatedIncidentsReport")
    public ResponseDTO<?> getLocoBasedRepeatedIncidentsReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate toDate,
            @RequestParam(required = false) Integer zoneId,
            @RequestParam(required = false) Integer divisionId,
            @RequestParam(required = false) Integer stationId,
            @RequestParam(required = false) Integer locoId,
            @RequestParam(required = false) String searchQuery,
            Pageable pageable){

        User user = new User();
        user.setId(HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication()));
        Long userId = user.getId();

        Page<LocoRepeatedIncidentReportDTO> data = reportsService.getLocoBasedRepeatedIncidentsReport(userId,stationId,divisionId,zoneId,locoId,fromDate,toDate,searchQuery,pageable);
//        Page<RepeatedIncidentsReportDTO> data = Page.empty();

        return ResponseDTO.<Page<LocoRepeatedIncidentReportDTO>>builder()
                .data(data)
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .totalRecords(data.getNumberOfElements())
                .build();
    }


    @GetMapping("/getStationRepeatedIncidentAnalysis")
    public ResponseDTO<?> getStationRepeatedIncidentAnalysis(
            @RequestParam String issueCategory,
            @RequestParam String possibleRootCause,
            @RequestParam String rootCauseSubCategory,
            @RequestParam Integer divisionId,
            @RequestParam Integer stationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate toDate
    ) {
        Long userId = HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication());

        RepeatedIncidentAnalysisDTO data = reportsService.getStationRepeatedIncidentAnalysis(
                userId,
                stationId,
                divisionId,
                fromDate,
                toDate,
                issueCategory,
                possibleRootCause,
                rootCauseSubCategory
        );

        return ResponseDTO.<RepeatedIncidentAnalysisDTO>builder()
                .data(data)
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }


    @GetMapping("/getClosureIncidentsReport")
    public ResponseDTO<?> getClosureIncidentsReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate toDate,
            @RequestParam(required = false) Integer zoneId,
            @RequestParam(required = false) Integer divisionId,
            @RequestParam(required = false) Integer stationId,
            @RequestParam(required = false) String searchQuery,
            Pageable pageable){

        User user = new User();
        user.setId(HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication()));
        Long userId = user.getId();


//        log.info("Filter userId {}",userId);
        Page<TcasBreakingInspection> data = reportsService.getClosureIncidentsReport(userId,zoneId,divisionId,stationId,fromDate,toDate,searchQuery,pageable);
//        Page<RepeatedIncidentsReportDTO> data = Page.empty();

        return ResponseDTO.<Page<TcasBreakingInspection>>builder()
                .data(data)
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .totalRecords(data.getNumberOfElements())
                .build();
    }

    @GetMapping("/getOpenTicketReport")
    public ResponseDTO<?> getOpenTicketReport(
            @RequestParam(required = false) Long zoneId,
            @RequestParam(required = false) Long divisionId,
            @RequestParam(required = false) Long firmId,
            @RequestParam(required = false) Long issueCategoryId,
            @RequestParam(required = false) Long rootCauseCategoryId,
            @RequestParam(required = false) Long rootCauseSubCategoryId,
            @RequestParam(required = false) String searchQuery,
            Pageable pageable){
//        log.info("Filter fromDate {}",fromDate);
//        log.info("Filter toDate {}",toDate);
//        log.info("Filter zoneId {}",zoneId);
//        log.info("Filter divisionId {}",divisionId);
        log.info("Filter firmId {}",firmId);
//        log.info("Filter searchQuery {}",searchQuery);

        // Retrieve the authenticated user
        User user = userService.getUserByUserId(
                HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication())
        );

        // Adjust zoneId and divisionId based on user role
        if (user.getRole().getName().equalsIgnoreCase("ROLE_DIVISION") || user.getRole().getName().equalsIgnoreCase("ROLE_OEM")) {
            divisionId = Long.valueOf(user.getDivision().getId());
            zoneId = Long.valueOf(user.getDivision().getZone().getId());
        } else if (user.getRole().getName().equalsIgnoreCase("ROLE_ZONE")) {
            zoneId = Long.valueOf(user.getDivision().getZone().getId());
        }
//        log.info("Filter userId {}",userId);

        Page<OpenTicketReportDTO> data = reportsService.getOpenTicketReport(
                pageable,
                zoneId,
                divisionId,
                firmId,
                issueCategoryId,
                rootCauseCategoryId,
                rootCauseSubCategoryId,
                true,               // ticketStatus
                searchQuery          // 👈 add this
        );
//        Page<RepeatedIncidentsReportDTO> data = Page.empty();

        return ResponseDTO.<Page<OpenTicketReportDTO>>builder()
                .data(data)
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .totalRecords(data.getNumberOfElements())
                .build();
    }

    @GetMapping("/getAvgTicketClosureTimeReport")
    public ResponseDTO<?> getAvgTicketClosureTimeReport(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Long zoneId,
            @RequestParam(required = false) Long divisionId) {
//        log.info("Filter fromDate {}",fromDate);
//        log.info("Filter toDate {}",toDate);
//        log.info("Filter zoneId {}",zoneId);
//        log.info("Filter divisionId {}",divisionId);
//        log.info("Filter stationId {}",stationId);
//        log.info("Filter searchQuery {}",searchQuery);

        // Retrieve the authenticated user
        User user = userService.getUserByUserId(
                HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication())
        );

        // Adjust zoneId and divisionId based on user role
        if (user.getRole().getName().equalsIgnoreCase("ROLE_DIVISION") || user.getRole().getName().equalsIgnoreCase("ROLE_OEM")) {
            divisionId = Long.valueOf(user.getDivision().getId());
            zoneId = Long.valueOf(user.getDivision().getZone().getId());
        } else if (user.getRole().getName().equalsIgnoreCase("ROLE_ZONE")) {
            zoneId = Long.valueOf(user.getDivision().getZone().getId());
        }
//        log.info("Filter userId {}",userId);

        List<MonthWiseData> data = reportsService.getAvgClosingTimeDataByMonth(zoneId,divisionId,year);
//        Page<RepeatedIncidentsReportDTO> data = Page.empty();

        return ResponseDTO.<List<MonthWiseData>>builder()
                .data(data)
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .totalRecords(data.size())
                .build();
    }

//    @GetMapping("/getTripReport")
//    public ResponseDTO<?> getTripReport(
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate fromDate,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate toDate,
//            @RequestParam(required = false) Long zoneId,
//            @RequestParam(required = false) Long divisionId,
//            @RequestParam(required = false) String issueStatus,
//            @RequestParam(required = false) String searchQuery,
//            Pageable pageable) {
////        log.info("Filter fromDate {}",fromDate);
////        log.info("Filter toDate {}",toDate);
////        log.info("Filter zoneId {}",zoneId);
////        log.info("Filter divisionId {}",divisionId);
////        log.info("Filter stationId {}",stationId);
////        log.info("Filter searchQuery {}",searchQuery);
//
//        // Retrieve the authenticated user
//        User user = userService.getUserByUserId(
//                HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication())
//        );
//
//        // Adjust zoneId and divisionId based on user role
//        if (user.getRole().getName().equalsIgnoreCase("ROLE_DIVISION") || user.getRole().getName().equalsIgnoreCase("ROLE_OEM")) {
//            divisionId = Long.valueOf(user.getDivision().getId());
//            zoneId = Long.valueOf(user.getDivision().getZone().getId());
//        } else if (user.getRole().getName().equalsIgnoreCase("ROLE_ZONE")) {
//            zoneId = Long.valueOf(user.getDivision().getZone().getId());
//        }
////        log.info("Filter userId {}",userId);
//
//        Page<TripsReportDTO> data = reportsService.getTripsReportByDivisionAndZone(zoneId,divisionId,fromDate,toDate,issueStatus,pageable);
////        Page<RepeatedIncidentsReportDTO> data = Page.empty();
//
//        return ResponseDTO.<Page<TripsReportDTO>>builder()
//                .data(data)
//                .message(Constants.SUCCESS_MSG)
//                .status(HttpStatus.OK.value())
//                .totalRecords(String.valueOf(data.getNumberOfElements()))
//                .build();
//    }

@GetMapping("/getStationRelatedAndLocoRelatedOpenTicket")
public ResponseDTO<?> getStationRelatedAndLocoRelatedOpenTicket(
        @RequestParam(required = false) Long zoneId,
        @RequestParam(required = false) Long divisionId,
        @RequestParam(required = false) Long firmId,
        @RequestParam(required = false) String searchQuery,
        Pageable pageable){
//        log.info("Filter fromDate {}",fromDate);
//        log.info("Filter toDate {}",toDate);
//        log.info("Filter zoneId {}",zoneId);
//        log.info("Filter divisionId {}",divisionId);
//        log.info("Filter stationId {}",stationId);
//        log.info("Filter searchQuery {}",searchQuery);

    // Retrieve the authenticated user
    User user = userService.getUserByUserId(
            HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication())
    );

    // Adjust zoneId and divisionId based on user role
    if (user.getRole().getName().equalsIgnoreCase("ROLE_DIVISION") || user.getRole().getName().equalsIgnoreCase("ROLE_OEM")) {
        divisionId = Long.valueOf(user.getDivision().getId());
        zoneId = Long.valueOf(user.getDivision().getZone().getId());
    } else if (user.getRole().getName().equalsIgnoreCase("ROLE_ZONE")) {
        zoneId = Long.valueOf(user.getDivision().getZone().getId());
    }
//        log.info("Filter userId {}",userId);

    List<PieChartData> data = reportsService.getStationRelatedAndLocoRelatedOpenTicket(zoneId,divisionId,firmId,true);
//        Page<RepeatedIncidentsReportDTO> data = Page.empty();

    return ResponseDTO.<List<PieChartData>>builder()
            .data(data)
            .message(Constants.SUCCESS_MSG)
            .status(HttpStatus.OK.value())
            .totalRecords(data.size())
            .build();
}

    @GetMapping("/getMonthWiseTicketData")
    public ResponseDTO<?> getMonthWiseTicketData(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Long zoneId,
            @RequestParam(required = false) Long divisionId) {
//        log.info("Filter fromDate {}",fromDate);
//        log.info("Filter toDate {}",toDate);
//        log.info("Filter zoneId {}",zoneId);
//        log.info("Filter divisionId {}",divisionId);
//        log.info("Filter stationId {}",stationId);
//        log.info("Filter searchQuery {}",searchQuery);

        // Retrieve the authenticated user
        User user = userService.getUserByUserId(
                HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication())
        );

        // Adjust zoneId and divisionId based on user role
        if (user.getRole().getName().equalsIgnoreCase("ROLE_DIVISION") || user.getRole().getName().equalsIgnoreCase("ROLE_OEM")) {
            divisionId = Long.valueOf(user.getDivision().getId());
            zoneId = Long.valueOf(user.getDivision().getZone().getId());
        } else if (user.getRole().getName().equalsIgnoreCase("ROLE_ZONE")) {
            zoneId = Long.valueOf(user.getDivision().getZone().getId());
        }
//        log.info("Filter userId {}",userId);

        List<MonthWiseData> data = reportsService.getMonthWiseTicketData(zoneId,divisionId,year);
//        Page<RepeatedIncidentsReportDTO> data = Page.empty();

        return ResponseDTO.<List<MonthWiseData>>builder()
                .data(data)
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .totalRecords(data.size())
                .build();
    }

    @GetMapping("/getOEMAnalysisReport")
    public ResponseDTO<?> getOEMAnalysisReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate toDate,
            @RequestParam(required = false) Long zoneId,
            @RequestParam(required = false) Long divisionId){

        log.info("Filter fromDate {}",fromDate);
        log.info("Filter toDate {}",toDate);
        log.info("Filter zoneId {}",zoneId);
        log.info("Filter divisionId {}",divisionId);

        User user = new User();
        user.setId(HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication()));
        Long userId = user.getId();


        log.info("Filter userId {}",userId);
        List<OEMIncidentsAnalysisDTO> data = reportsService.getOEMAnalysisReport(zoneId,divisionId,fromDate,toDate);
//        Page<RepeatedIncidentsReportDTO> data = Page.empty();

        return ResponseDTO.<List<OEMIncidentsAnalysisDTO>>builder()
                .data(data)
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .totalRecords(data.size())
                .build();
    }

    @GetMapping("/getMonthlyTripCounts")
    public ResponseDTO<List<MonthWiseData>> getMonthlyTripCounts() {

        return ResponseDTO.<List<MonthWiseData>>builder()
                .data(reportsService.getMonthlyTripCounts())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();

    }

    @GetMapping("/getMonthlyTicketCounts")
    public ResponseDTO<List<MonthWiseData>> getMonthlyTicketCounts() {

        return ResponseDTO.<List<MonthWiseData>>builder()
                .data(reportsService.getMonthlyTicketCounts())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();

    }

}
