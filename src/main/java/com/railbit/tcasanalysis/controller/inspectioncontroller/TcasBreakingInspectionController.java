package com.railbit.tcasanalysis.controller.inspectioncontroller;

import com.railbit.tcasanalysis.DTO.ResponseDTO;
import com.railbit.tcasanalysis.entity.TcasBreakingInspection;
import com.railbit.tcasanalysis.entity.TcasBreakingInspectionStatus;
import com.railbit.tcasanalysis.entity.User;
import com.railbit.tcasanalysis.service.UserService;
import com.railbit.tcasanalysis.service.serviceImpl.FCMNotificationServiceImpl;
import com.railbit.tcasanalysis.service.TcasBreakingInspectionService;
import com.railbit.tcasanalysis.util.Constants;
import com.railbit.tcasanalysis.util.HelpingHand;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
@RequestMapping("/tcasapi/tcasBreakingInspection")
public class TcasBreakingInspectionController {
    private static final Logger log = LogManager.getLogger(TcasBreakingInspectionController.class);
    private final TcasBreakingInspectionService tcasBreakingInspectionService;
    private final UserService userService;
    @Autowired
    private FCMNotificationServiceImpl fcmService;
    @GetMapping("/")
    public ResponseDTO<List<TcasBreakingInspection>> getAllTcasBreakingInspections(){

        return ResponseDTO.<List<TcasBreakingInspection>>builder()
                .data(tcasBreakingInspectionService.getAllTcasBreakingInspectionByLatestTripDate())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getAllIncidentsByLatestTripDate/")
    public ResponseDTO<List<TcasBreakingInspection>> getAllIncidentsByLatestTripDate(){

        return ResponseDTO.<List<TcasBreakingInspection>>builder()
                .data(tcasBreakingInspectionService.getAllIncidentsByLatestTripDate())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getAllByDivisionId/{divisionID}")
    public ResponseDTO<List<TcasBreakingInspection>> getAllByDivisionId(@PathVariable @Valid Integer divisionID){
        return ResponseDTO.<List<TcasBreakingInspection>>builder()
                .data(tcasBreakingInspectionService.getAllTcasBreakingInspectionByDivisionId(divisionID))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @GetMapping("/{id}")
    public ResponseDTO<?> getTcasBreakingInspectionById(@PathVariable @Valid Long id){
        return ResponseDTO.<TcasBreakingInspection>builder()
                .data(tcasBreakingInspectionService.getTcasBreakingInspectionById(id))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PostMapping("/")
    public ResponseDTO<?>addTcasBreakingInspection(@RequestParam(required = false) List<MultipartFile> fileList
            ,@Valid @RequestPart("tcasBreakingInspection") TcasBreakingInspection tcasBreakingInspection) throws Exception {
        User user = new User();
        user.setId(HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication()));
        tcasBreakingInspection.setUser(user);
        return ResponseDTO.<Object>builder()
                .data(tcasBreakingInspectionService.addTcasBreakingInspection(tcasBreakingInspection,fileList))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @PostMapping("/addIncident/")
    public ResponseDTO<?>addIncident(@Valid @RequestBody TcasBreakingInspection tcasBreakingInspection) throws Exception {

        return ResponseDTO.<Object>builder()
                .data(tcasBreakingInspectionService.addIncident(tcasBreakingInspection))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();

    }

    @PutMapping("/")
    public ResponseDTO<?> updateTcasBreakingInspection(@RequestBody TcasBreakingInspection tcasBreakingInspection) throws Exception {
        tcasBreakingInspectionService.updateTcasBreakingInspection(tcasBreakingInspection);
        return ResponseDTO.<Object>builder()
                .data("Updated Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PostMapping("/import/")
    public ResponseDTO<?> importTcasBreakingInspections(@Valid MultipartFile excelSheet) throws Exception {

        Long userId = HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication());
        int rowsAdded = tcasBreakingInspectionService.importByExcelSheet(excelSheet, userId);

        return ResponseDTO.<Object>builder()
                .data(rowsAdded + " rows added successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();

    }

    @PostMapping("/closeInspectionIssue/")
    public ResponseDTO<?> closeInspectionIssue(@Valid @RequestBody TcasBreakingInspectionStatus tcasBreakingInspectionStatus) throws Exception {

        User user = new User();
        user.setId(HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication()));
        tcasBreakingInspectionStatus.setUser(user);
        tcasBreakingInspectionService.closeInspectionIssue(tcasBreakingInspectionStatus);

        return ResponseDTO.<Object>builder()
                .data("Issue Closed Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getFilteredInspections")
    public ResponseDTO<Page<TcasBreakingInspection>> getFilteredInspections(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate toDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer issueCategoryId,
            @RequestParam(required = false) Integer possibleIssueId,
            @RequestParam(required = false) Integer possibleRootCauseId,
            @RequestParam(required = false) Integer rootCauseSubCategoryId,
            @RequestParam(required = false) Integer stationId,
            @RequestParam(required = false) Integer zoneId,
            @RequestParam(required = false) Integer divisionId,
            @RequestParam(required = false) Integer firmId,
            @RequestParam(required = false) Integer assignFirmId,
            @RequestParam(required = false) Integer locoId,
            @RequestParam(required = false) Integer locoTypeId,
            @RequestParam(required = false) String locoVersion,
            @RequestParam(required = false) String withIssue,
            @RequestParam(required = false) String locoCondemned,
            @RequestParam(required = false) String searchQuery,
            Pageable pageable) {

        // Retrieve the authenticated user
        User user = userService.getUserByUserId(
                HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication())
        );

        // Adjust zoneId and divisionId based on user role
        if (user.getRole().getName().equalsIgnoreCase("ROLE_DIVISION")) {
            divisionId = user.getDivision().getId();
            zoneId = user.getDivision().getZone().getId();
        } else if (user.getRole().getName().equalsIgnoreCase("ROLE_ZONE")) {
            zoneId = user.getZone().getId();
        } else if (user.getRole().getName().equalsIgnoreCase("ROLE_OEM")) {
            divisionId = user.getDivision().getId();
            zoneId = user.getDivision().getZone().getId();
            firmId = user.getFirm().getId();
        }

        return ResponseDTO.<Page<TcasBreakingInspection>>builder()
                .data(tcasBreakingInspectionService.getFilteredInspections(
                        fromDate.atTime(LocalTime.of(0, 0)),
                        toDate.atTime(LocalTime.of(23, 59)),
                        status,
                        issueCategoryId,
                        possibleIssueId,
                        possibleRootCauseId,
                        rootCauseSubCategoryId,
                        stationId,
                        zoneId,
                        divisionId,
                        withIssue,
                        assignFirmId,
                        firmId,
//                        sectionFirmId,
                        locoId,
                        locoTypeId,
                        locoVersion,
                        locoCondemned,
                        searchQuery.trim(),
                        pageable

                ))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/downloadFilteredInspections")
    public ResponseDTO<List<TcasBreakingInspection>> downloadFilteredInspections(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate toDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer issueCategoryId,
            @RequestParam(required = false) Integer possibleIssueId,
            @RequestParam(required = false) Integer possibleRootCauseId,
            @RequestParam(required = false) Integer rootCauseSubCategoryId,
            @RequestParam(required = false) Integer stationId,
            @RequestParam(required = false) Integer zoneId,
            @RequestParam(required = false) Integer divisionId,
            @RequestParam(required = false) Integer firmId,
            @RequestParam(required = false) Integer assignFirmId,
            @RequestParam(required = false) Integer locoId,
            @RequestParam(required = false) Integer locoTypeId,
            @RequestParam(required = false) String locoVersion,
            @RequestParam(required = false) String withIssue,
            @RequestParam(required = false) String locoCondemned,
            @RequestParam(required = false) String searchQuery) {

        // Retrieve the authenticated user
        User user = userService.getUserByUserId(
                HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication())
        );

        // Adjust zoneId and divisionId based on user role
        if (user.getRole().getName().equalsIgnoreCase("ROLE_DIVISION")) {
            divisionId = user.getDivision().getId();
            zoneId = user.getDivision().getZone().getId();
        } else if (user.getRole().getName().equalsIgnoreCase("ROLE_ZONE")) {
            zoneId = user.getZone().getId();
        } else if (user.getRole().getName().equalsIgnoreCase("ROLE_OEM")) {
            divisionId = user.getDivision().getId();
            zoneId = user.getDivision().getZone().getId();
            firmId = user.getFirm().getId();
        }

        return ResponseDTO.<List<TcasBreakingInspection>>builder()
                .data(tcasBreakingInspectionService.downloadFilteredInspections(
                        fromDate.atTime(LocalTime.of(0, 0)),
                        toDate.atTime(LocalTime.of(23, 59)),
                        status,
                        issueCategoryId,
                        possibleIssueId,
                        possibleRootCauseId,
                        rootCauseSubCategoryId,
                        stationId,
                        zoneId,
                        divisionId,
                        withIssue,
                        assignFirmId,
                        firmId,
//                        sectionFirmId,
                        locoId,
                        locoTypeId,
                        locoVersion,
                        locoCondemned,
                        searchQuery.trim()

                ))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getAllByMonth/{month}")
    public ResponseDTO<List<TcasBreakingInspection>> getAllByMonth(@PathVariable @Valid Integer month){
        return ResponseDTO.<List<TcasBreakingInspection>>builder()
                .data(tcasBreakingInspectionService.getAllByMonth(month))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getAllByUserId/{userID}")
    public ResponseDTO<List<TcasBreakingInspection>> getAllByUserId(@PathVariable @Valid Long userID){
        return ResponseDTO.<List<TcasBreakingInspection>>builder()
                .data(tcasBreakingInspectionService.getAllByUserId(userID))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getAllByAssignedUserId/{userId}")
    public ResponseDTO<List<TcasBreakingInspection>> getAllByAssignedUserId(@PathVariable @Valid Long userId){
        return ResponseDTO.<List<TcasBreakingInspection>>builder()
                .data(tcasBreakingInspectionService.getAllByAssignedToUser(userId))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getAllByFirmId/{firmID}")
    public ResponseDTO<List<TcasBreakingInspection>> getAllByFirmId(@PathVariable @Valid Integer firmID){
        return ResponseDTO.<List<TcasBreakingInspection>>builder()
                .data(tcasBreakingInspectionService.getAllByFirmId(firmID))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getAllOpenIncidents")
    public ResponseDTO<List<TcasBreakingInspection>> getAllOpenIncidents(){
        return ResponseDTO.<List<TcasBreakingInspection>>builder()
                .data(tcasBreakingInspectionService.getPendingInspections())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getAllCloseIncidents")
    public ResponseDTO<List<TcasBreakingInspection>> getAllCloseIncidents(){
        return ResponseDTO.<List<TcasBreakingInspection>>builder()
                .data(tcasBreakingInspectionService.getClosedInspections())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseDTO<?> deleteIncident(@PathVariable @Valid Long id){
        tcasBreakingInspectionService.deleteIncidentById(id);
        return ResponseDTO.<Object>builder()
                .data("Deleted Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

}
