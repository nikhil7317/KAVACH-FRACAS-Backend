package com.railbit.tcasanalysis.controller.cmsabncontroller;

import com.railbit.tcasanalysis.DTO.ResponseDTO;
import com.railbit.tcasanalysis.entity.Firm;
import com.railbit.tcasanalysis.entity.User;
import com.railbit.tcasanalysis.entity.cmsabn.CMSAbn;
import com.railbit.tcasanalysis.service.CMSAbnService;
import com.railbit.tcasanalysis.service.UserService;
import com.railbit.tcasanalysis.util.Constants;
import com.railbit.tcasanalysis.util.HelpingHand;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
@RequestMapping("/tcasapi/cmsAbn")
public class CMSAbnController {

    private static final Logger log = LogManager.getLogger(CMSAbnController.class);
    private final CMSAbnService cMSAbnService;
    private final UserService userService;
    
    @GetMapping("/")
    public ResponseDTO<List<CMSAbn>> getAllCMSAbns(){
        return ResponseDTO.<List<CMSAbn>>builder()
                .data(cMSAbnService.getAllCMSAbns())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getFilteredCMSAbns")
    public ResponseDTO<Page<CMSAbn>> getFilteredCMSAbns(
            @RequestParam(required = false) Long zoneId,
            @RequestParam(required = false) Long divisionId,
            @RequestParam(required = false) String abnType,
            @RequestParam(required = false) String subHead,
            @RequestParam(required = false) Long stationId,
            @RequestParam(required = false) Long locoId,
            @RequestParam(required = false) Long tcasId,
            @RequestParam(required = false) String stcasLtcas,
            @RequestParam(required = false) Long rootCauseCategory,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String searchQuery,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate toDate,

            @PageableDefault(size = 10) Pageable pageable
    ) {

        log.info("zoneId: {}", zoneId);
        log.info("divisionId: {}", divisionId);
        log.info("abnType: {}", abnType);
        log.info("subHead: {}", subHead);
        log.info("stationId: {}", stationId);
        log.info("locoId: {}", locoId);
        log.info("tcasId: {}", tcasId);
        log.info("stcasLtcas: {}", stcasLtcas);
        log.info("rootCauseCategory: {}", rootCauseCategory);
        log.info("status: {}", status);
        log.info("searchQuery: {}", searchQuery);
        log.info("fromDate: {}", fromDate);
        log.info("toDate: {}", toDate);
        log.info("pageable: {}", pageable);

        Page<CMSAbn> filteredCMSAbns = cMSAbnService.getFilteredCMSAbns(
                zoneId,
                divisionId,
                abnType,
                subHead,
                stationId,
                locoId,
                tcasId,
                stcasLtcas,
                rootCauseCategory,
                status,
                fromDate != null ? fromDate.atTime(LocalTime.of(0, 0)) : null,
                toDate != null ? toDate.atTime(LocalTime.of(23, 59)) : null,
                searchQuery.trim(),
                pageable
        );

        return ResponseDTO.<Page<CMSAbn>>builder()
                .data(filteredCMSAbns)
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/{id}")
    public ResponseDTO<?> getCMSAbnById(@PathVariable @Valid Long id){
        return ResponseDTO.<CMSAbn>builder()
                .data(cMSAbnService.getCMSAbnById(id))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @PostMapping("/")
    public ResponseDTO<?>addCMSAbn(@Valid @RequestBody CMSAbn cMSAbn) {
        System.out.println("Running");
        return ResponseDTO.<Object>builder()
                .data(cMSAbnService.postCMSAbn(cMSAbn))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @PutMapping("/")
    public ResponseDTO<?> updateCMSAbn(@Valid @RequestBody CMSAbn cMSAbn) throws Exception {
        cMSAbnService.updateCMSAbn(cMSAbn);
        return ResponseDTO.<Object>builder()
                .data("Updated Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseDTO<?> deleteCMSAbn(@PathVariable @Valid Long id){
        cMSAbnService.deleteCMSAbnById(id);
        return ResponseDTO.<Object>builder()
                .data("Deleted Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PostMapping("/import/")
    public ResponseDTO<?> importCMSAbns(@Valid MultipartFile excelSheet) throws Exception {

        if (excelSheet == null){
            throw new Exception("Excel File Required");
        }

        return ResponseDTO.<Object>builder()
                .data(cMSAbnService.importByExcelSheet(excelSheet))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PostMapping("/addCMSAbnRemark")
    public ResponseDTO<?> addCMSAbnRemark(@RequestBody CMSAbn cmsAbn) throws BadRequestException {
        log.info("Received Remark: {}", cmsAbn);
        if (cmsAbn.getId() == null || cmsAbn.getAppRemarks() == null) {
            throw new BadRequestException("Abn Id and Remarks are required");
        }
        User user = userService.getUserByUserId(HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication()));
        CMSAbn cmsAbn1 = cMSAbnService.getCMSAbnById(cmsAbn.getId());
        if (cmsAbn1 == null) {
            throw new BadRequestException("CMSAbn with given ID not found");
        }
        cmsAbn1.setAppRemarks(cmsAbn.getAppRemarks());
        String status = "Remarks Added";
        if (user.getRole().getName().equalsIgnoreCase("ROLE_OEM")) {
            Firm firm = user.getFirm();
            status = "Remarks Added";
        }
        cmsAbn1.setStatus(status);
        cMSAbnService.addCMSAbnRemark(cmsAbn1);
        return ResponseDTO.<Object>builder()
                .data("Updated Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }


}
