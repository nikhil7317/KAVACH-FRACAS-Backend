package com.railbit.tcasanalysis.controller.nmsstattionstatuscontroller;

import com.railbit.tcasanalysis.DTO.ResponseDTO;
import com.railbit.tcasanalysis.entity.nmspackets.stationarypackets.NMSStationStatus;
import com.railbit.tcasanalysis.repository.dslrepos.NMSStationStatusRepository;
import com.railbit.tcasanalysis.service.NMSStationStatusService;
import com.railbit.tcasanalysis.util.Constants;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
@RequestMapping("/tcasapi/nmsStationStatus")
public class NMSStationStatusController {

    private final NMSStationStatusService nmsStationStatusService;
    private final NMSStationStatusRepository nmsStationStatusRepository;

    @GetMapping("/filter")
    public ResponseDTO<List<NMSStationStatus>> getFilteredStations(
            @RequestParam(required = false) Integer zoneId,
            @RequestParam(required = false) Integer divisionId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String searchQuery) {

        return ResponseDTO.<List<NMSStationStatus>>builder()
                .data(nmsStationStatusRepository.getFilteredStationStatuses(zoneId, divisionId, status, searchQuery))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @GetMapping("/all")
    public ResponseDTO<?> getAllStationStatusWithoutPagination() {
        return ResponseDTO.<List<NMSStationStatus>>builder()
                .data(nmsStationStatusService.findAll())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/view")
    public ResponseDTO<Object> getAllStationStatus(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "100") Integer size) {

        Page<NMSStationStatus> stationStatusPage = nmsStationStatusService.findAll(PageRequest.of(page, size));

        Map<String, Object> response = new HashMap<>();
        response.put("data", stationStatusPage.getContent());
        response.put("totalRecords", stationStatusPage.getTotalElements());

        return ResponseDTO.builder()
                .status(HttpStatus.OK.value())
                .message(Constants.SUCCESS_MSG)
                .data(response.get("data"))
                .totalRecords(Integer.valueOf(response.get("totalRecords").toString()))
                .build();
    }

    @GetMapping("/{id}")
    public ResponseDTO<?> getStationStatusById(@PathVariable @Valid Long id) {
        return ResponseDTO.<NMSStationStatus>builder()
                .data(nmsStationStatusService.findById(id))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @PostMapping("/")
    public ResponseDTO<?> createStationStatus(@Valid @RequestBody NMSStationStatus nmsStationStatus) {
        return ResponseDTO.<NMSStationStatus>builder()
                .data(nmsStationStatusService.create(nmsStationStatus))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @PostMapping("/bulk")
    public ResponseDTO<?> createBulkStationStatus(@Valid @RequestBody List<NMSStationStatus> nmsStationStatuses) {
        return ResponseDTO.<List<NMSStationStatus>>builder()
                .data(nmsStationStatusService.createAll(nmsStationStatuses))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @PutMapping("/{id}")
    public ResponseDTO<?> updateStationStatus(
            @PathVariable @Valid Long id,
            @Valid @RequestBody NMSStationStatus nmsStationStatus) {
        return ResponseDTO.<NMSStationStatus>builder()
                .data(nmsStationStatusService.update(id, nmsStationStatus))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseDTO<?> deleteStationStatus(@PathVariable @Valid Long id) {
        nmsStationStatusService.delete(id);
        return ResponseDTO.builder()
                .data("Deleted Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
}
