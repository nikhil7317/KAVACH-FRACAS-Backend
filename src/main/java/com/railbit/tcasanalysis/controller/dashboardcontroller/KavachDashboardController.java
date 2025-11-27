package com.railbit.tcasanalysis.controller.dashboardcontroller;

import com.railbit.tcasanalysis.DTO.ResponseDTO;
import com.railbit.tcasanalysis.entity.KavachDashboard;
import com.railbit.tcasanalysis.service.KavachDashboardService;
import com.railbit.tcasanalysis.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tcasapi/kavachDashboard")  // Base URL for the API
public class KavachDashboardController {

    @Autowired
    private KavachDashboardService kavachDashboardService;

    @GetMapping("/getAll")
    public ResponseDTO<List<KavachDashboard>> getAllKavachDashboards(){
        return ResponseDTO.<List<KavachDashboard>>builder()
                .data(kavachDashboardService.getAllKavachDashboards())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
}
