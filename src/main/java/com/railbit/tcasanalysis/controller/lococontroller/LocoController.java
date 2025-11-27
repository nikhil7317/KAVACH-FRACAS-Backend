package com.railbit.tcasanalysis.controller.lococontroller;

import com.railbit.tcasanalysis.DTO.ResponseDTO;
import com.railbit.tcasanalysis.DTO.incident.IncidentDTO;
import com.railbit.tcasanalysis.DTO.incident.NMSIncidentDTO;
import com.railbit.tcasanalysis.entity.TcasBreakingInspection;
import com.railbit.tcasanalysis.entity.loco.Loco;
import com.railbit.tcasanalysis.service.LocoService;
import com.railbit.tcasanalysis.service.serviceImpl.LocoMovementAnalytics;
import com.railbit.tcasanalysis.util.Constants;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
@RequestMapping("/tcasapi/loco")
public class LocoController {
    private static final Logger log = LogManager.getLogger(LocoController.class);
    private final LocoService locoService;

    private final LocoMovementAnalytics locoMovementAnalytics;

    @GetMapping("/getLocoVersions/")
    public ResponseDTO<List<String>> getDistinctVersions() {
        return ResponseDTO.<List<String>>builder()
                .data(locoService.getDistinctVersions())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/")
    public ResponseDTO<List<Loco>> getAllLocos() {
        return ResponseDTO.<List<Loco>>builder()
                .data(locoService.getAllLoco())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getFilteredLoco")
    public ResponseDTO<Page<Loco>> getFilteredLoco(
            @RequestParam(required = false, defaultValue = "") String searchTerm,
            @RequestParam(required = false) Integer locoType,
            @RequestParam(required = false) Integer firm,
            @RequestParam(required = false) Integer shed,
            @RequestParam(required = false) String month,
            @RequestParam(required = false) String version,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) {

        // Create Pageable with sorting
        Pageable pageable;
        if (sort.length > 1) {
            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sort[1]), sort[0]));
        } else {
            pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        }

        // Call service with filters and pagination
        Page<Loco> locos = locoService.getLocos(searchTerm, locoType, firm, shed, month, version, pageable);

        return ResponseDTO.<Page<Loco>>builder()
                .data(locos)
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getLocoByNo/{no}")
    public ResponseDTO<?> getLocoByNo(@PathVariable @Valid String no) {
        return ResponseDTO.<Loco>builder()
                .data(locoService.findByLocoNo(no))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/{id}")
    public ResponseDTO<?> getLocoById(@PathVariable @Valid Integer id) {
        return ResponseDTO.<Loco>builder()
                .data(locoService.getLocoById(id))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @PostMapping("/")
    public ResponseDTO<?> addLoco(@Valid @RequestBody Loco loco) {
        System.out.println("Running");
        return ResponseDTO.<Object>builder()
                .data(locoService.postLoco(loco))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @PutMapping("/")
    public ResponseDTO<?> updateLoco(@Valid @RequestBody Loco loco) {
        locoService.updateLoco(loco);
        return ResponseDTO.<Object>builder()
                .data("Updated Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseDTO<?> deleteLoco(@PathVariable @Valid Integer id) {
        locoService.deleteLocoById(id);
        return ResponseDTO.<Object>builder()
                .data("Deleted Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @PostMapping("/import/")
    public ResponseDTO<?> importLocos(@Valid MultipartFile excelSheet) throws Exception {

        if (excelSheet == null) {
            throw new Exception("Excel File Required");
        }

        int rowInserted = locoService.importByExcelSheet(excelSheet);

        return ResponseDTO.<Object>builder()
                .data(rowInserted + " Rows Inserted")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getLocoNos/")
    public ResponseDTO<List<Loco>> getAllLocoNos() {
        return ResponseDTO.<List<Loco>>builder()
                .data(locoService.getAllLocos())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getFilteredIncident")
    public ResponseDTO<Page<NMSIncidentDTO>> getFilteredIncidents(
            Pageable pageable,
            @RequestParam(required = false, defaultValue = "") String searchQuery,
            @RequestParam(required = false) Long zoneId,
            @RequestParam(required = false) Long divisionId,
            @RequestParam(required = false) Long stationId,
            @RequestParam(required = false) Long locoId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate) {

        // Create date-time range
        if (fromDate != null) {
            fromDate = fromDate.with(LocalTime.MIN);
        }
        if (toDate != null) {
            toDate = toDate.with(LocalTime.MIN);
        }

        Page<NMSIncidentDTO> modeChangeDescriptions = locoMovementAnalytics.fetchLocoIncidents(zoneId, divisionId, stationId, locoId, category, fromDate, toDate, searchQuery, pageable);

        // Return response
        return ResponseDTO.<Page<NMSIncidentDTO>>builder()
                .data(modeChangeDescriptions)
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
}
