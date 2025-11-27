package com.railbit.tcasanalysis.controller.maintenancecontroller;

import com.railbit.tcasanalysis.DTO.LocoMovementDTO;
import com.railbit.tcasanalysis.DTO.ResponseDTO;
import com.railbit.tcasanalysis.DTO.incident.IncidentDTO;
import com.railbit.tcasanalysis.entity.MaintenanceReport;
import com.railbit.tcasanalysis.service.MaintenanceReportService;
import com.railbit.tcasanalysis.util.Constants;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/tcasapi/maint")
public class MaintenanceController {

    @Autowired
    MaintenanceReportService maintenanceReportService;

    @PostMapping("/add")
    public ResponseDTO<?> addMaintenanceData(@RequestBody MaintenanceReport report,@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseDTO.<Object>builder()
                .data(maintenanceReportService.addMaintenanceRecords(report,userDetails))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getmaintenance")
    public ResponseDTO<Page<MaintenanceReport>> getMaintenances(
            @RequestParam(required = false) Integer divId,
            @RequestParam(required = false) Integer stnId,
            @RequestParam(required = false) Integer locoId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<MaintenanceReport> filteredReports = maintenanceReportService.getFilteredReports(divId, stnId, locoId, startDate, endDate,page,size);

        return ResponseDTO.<Page<MaintenanceReport>>builder()
                .data(filteredReports)
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @DeleteMapping("delete/{id}")
    public ResponseDTO<?> deleteMaintenance(@PathVariable @Valid Long id){
        maintenanceReportService.deleteMaintenanceReportById(id);
        return ResponseDTO.<Object>builder()
                .data("Deleted Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @PutMapping("/")
    public ResponseDTO<?> updateMaintenanace(@Valid @RequestBody MaintenanceReport report){
        maintenanceReportService.updateMaintenance(report);
        return ResponseDTO.<Object>builder()
                .data("Updated Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
}