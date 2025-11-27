package com.railbit.tcasanalysis.controller.assignedincidentcontroller;

import com.railbit.tcasanalysis.DTO.ResponseDTO;
import com.railbit.tcasanalysis.entity.AssignedIncident;
import com.railbit.tcasanalysis.entity.User;
import com.railbit.tcasanalysis.service.AssignedIncidentService;
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
@RequestMapping("/tcasapi/assignedIncident")
public class AssignedIncidentController {
    private final AssignedIncidentService assignedIncidentService;
    @GetMapping("/")
    public ResponseDTO<List<AssignedIncident>> getAllAssignedIncidents(){
        return ResponseDTO.<List<AssignedIncident>>builder()
                .data(assignedIncidentService.getAllAssignedIncident())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @GetMapping("/{id}")
    public ResponseDTO<?> getAssignedIncidentById(@PathVariable @Valid Long id){
        return ResponseDTO.<AssignedIncident>builder()
                .data(assignedIncidentService.getAssignedIncidentById(id))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PostMapping("/")
    public ResponseDTO<?>addAssignedIncident(@Valid @RequestBody AssignedIncident assignedIncident) {

        return ResponseDTO.<AssignedIncident>builder()
                .data(assignedIncidentService.postAssignedIncident(assignedIncident))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();

    }
    @PutMapping("/")
    public ResponseDTO<?> updateAssignedIncident(@Valid @RequestBody AssignedIncident assignedIncident) {
        assignedIncidentService.updateAssignedIncident(assignedIncident);
        return ResponseDTO.<Object>builder()
                .data("Updated Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @DeleteMapping("/{id}")
    public ResponseDTO<?> deleteAssignedIncident(@PathVariable @Valid Long id){
        assignedIncidentService.deleteAssignedIncidentById(id);
        return ResponseDTO.<Object>builder()
                .data("Deleted Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
}
