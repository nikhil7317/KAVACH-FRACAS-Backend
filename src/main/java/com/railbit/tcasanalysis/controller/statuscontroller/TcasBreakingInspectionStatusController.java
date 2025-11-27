package com.railbit.tcasanalysis.controller.statuscontroller;

import com.railbit.tcasanalysis.DTO.ResponseDTO;
import com.railbit.tcasanalysis.controller.inspectioncontroller.TcasBreakingInspectionController;
import com.railbit.tcasanalysis.entity.TcasBreakingInspection;
import com.railbit.tcasanalysis.entity.TcasBreakingInspectionStatus;
import com.railbit.tcasanalysis.entity.User;
import com.railbit.tcasanalysis.service.TcasBreakingInspectionService;
import com.railbit.tcasanalysis.service.TcasBreakingInspectionStatusService;
import com.railbit.tcasanalysis.util.Constants;
import com.railbit.tcasanalysis.util.HelpingHand;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
@RequestMapping("/tcasapi/status/tcasbreakinginspection")
public class TcasBreakingInspectionStatusController {
    private final TcasBreakingInspectionStatusService tcasBreakingInspectionStatusService;
    private final TcasBreakingInspectionService tcasBreakingInspectionService;

    @GetMapping("/{id}")
    public ResponseDTO<?> getTcasBreakingInspectionStatusById(@PathVariable @Valid Long id){
        return ResponseDTO.<TcasBreakingInspectionStatus>builder()
                .data(tcasBreakingInspectionStatusService.getTcasBreakingInspectionStatusById(id))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @GetMapping("/")
    public ResponseDTO<List<TcasBreakingInspectionStatus>> getAllTcasBreakingInspectionStatus(){
        return ResponseDTO.<List<TcasBreakingInspectionStatus>>builder()
                .data(tcasBreakingInspectionStatusService.getAllTcasBreakingInspectionStatus())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/inspection/{id}")
    public ResponseDTO<List<TcasBreakingInspectionStatus>> getAllTcasBreakingInspectionStatusByInspection(@PathVariable @Valid Long id){
        return ResponseDTO.<List<TcasBreakingInspectionStatus>>builder()
                .data(tcasBreakingInspectionStatusService.getAllTcasBreakingInspectionStatusByInspection(id))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @PostMapping("/")
    public ResponseDTO<?> addTcasBreakingInspectionStatus(@Valid @RequestBody TcasBreakingInspectionStatus tcasBreakingInspectionStatus) throws Exception {
//        System.out.println(tcasBreakingInspectionStatus.toString());
        User user = new User();
        user.setId(HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication()));
        tcasBreakingInspectionStatus.setUser(user);
        Long insertId = tcasBreakingInspectionStatusService.postTcasBreakingInspectionStatus(tcasBreakingInspectionStatus).getId();

        return ResponseDTO.<Object>builder()
                .data(insertId)
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();

    }
    @PutMapping("/")
    public ResponseDTO<?> updateTcasBreakingInspectionStatus(@Valid @RequestBody TcasBreakingInspectionStatus tcasBreakingInspectionStatus) {
        tcasBreakingInspectionStatusService.updateTcasBreakingInspectionStatus(tcasBreakingInspectionStatus);
        return ResponseDTO.<Object>builder()
                .data("Updated Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @DeleteMapping("/{id}")
    public ResponseDTO<?> deleteTcasBreakingInspectionStatus(@PathVariable @Valid Long id){
        tcasBreakingInspectionStatusService.deleteTcasBreakingInspectionStatusById(id);
        return ResponseDTO.<Object>builder()
                .data("Deleted Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @PostMapping("/addInspectionStatusWithActions/")
    public ResponseDTO<?> addInspectionStatusWithActions(@RequestParam Long inspectionId,
                                                         @RequestParam String remarks,
                                                         @RequestParam boolean preventiveAction,
                                                         @RequestParam boolean correctiveAction) throws Exception {

        TcasBreakingInspectionStatus tcasBreakingInspectionStatus = new TcasBreakingInspectionStatus();
        TcasBreakingInspection tcasBreakingInspection = new TcasBreakingInspection();
        tcasBreakingInspection.setId(inspectionId);
        tcasBreakingInspectionStatus.setTcasBreakingInspection(tcasBreakingInspection);
        User user = new User();
        user.setId(HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication()));
        tcasBreakingInspectionStatus.setUser(user);
        tcasBreakingInspectionStatus.setRemarks(remarks);

        tcasBreakingInspectionService.addInspectionStatusWithActions(tcasBreakingInspectionStatus,preventiveAction,correctiveAction);

        return ResponseDTO.<Object>builder()
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

}
