package com.railbit.tcasanalysis.controller.incidentTicketTrack;

import com.railbit.tcasanalysis.DTO.ResponseDTO;
import com.railbit.tcasanalysis.entity.Firm;
import com.railbit.tcasanalysis.entity.IncidentTicket;
import com.railbit.tcasanalysis.entity.IncidentTicketTrack;
import com.railbit.tcasanalysis.entity.User;
import com.railbit.tcasanalysis.service.IncidentTicketTrackService;
import com.railbit.tcasanalysis.service.UserService;
import com.railbit.tcasanalysis.util.Constants;
import com.railbit.tcasanalysis.util.HelpingHand;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
@RequestMapping("/tcasapi/incidentTicketTrack")
public class IncidentTicketTrackController {

    private static final Logger log = LogManager.getLogger(IncidentTicketTrackController.class);
    private final IncidentTicketTrackService incidentTicketTrackService;
    private final UserService userService;

    @GetMapping("/getAllTracksByIncidentTicketId/{id}")
    public ResponseDTO<List<IncidentTicketTrack>> getAllTracksByIncidentTicketId(@PathVariable @Valid Long id){
        return ResponseDTO.<List<IncidentTicketTrack>>builder()
                .data(incidentTicketTrackService.getAllByIncidentTicketIdOrderByIdAsc(id))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/")
    public ResponseDTO<List<IncidentTicketTrack>> getAllIncidentTicketTracks(){
        return ResponseDTO.<List<IncidentTicketTrack>>builder()
                .data(incidentTicketTrackService.getAllIncidentTicketTracks())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/{id}")
    public ResponseDTO<?> getIncidentTicketTrackById(@PathVariable @Valid Long id){
        return ResponseDTO.<IncidentTicketTrack>builder()
                .data(incidentTicketTrackService.getIncidentTicketTrackById(id))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @PostMapping("/")
    public ResponseDTO<?>addIncidentTicketTrack(@Valid @RequestBody IncidentTicketTrack incidentTicketTrack) {
        System.out.println("Running");
        return ResponseDTO.<Object>builder()
                .data(incidentTicketTrackService.postIncidentTicketTrack(incidentTicketTrack))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @PostMapping("/addIncidentTicketRemark/")
    public ResponseDTO<?> addIncidentTicketRemark(@RequestBody IncidentTicketTrack incidentTicketTrack) throws BadRequestException {
//        System.out.println("Received Remark: " + remark);
        log.info("Received Remark: {}", incidentTicketTrack);
        // Check if ticketId or remarks are missing
        // Check if ticketId or remarks are missing
        if (incidentTicketTrack.getIncidentTicket() == null || incidentTicketTrack.getIncidentTicket().getId() == null || incidentTicketTrack.getRemarks() == null || incidentTicketTrack.getRemarks().isEmpty()) {
            throw new BadRequestException("Ticket ID and Remarks are required");
        }

        User user = userService.getUserByUserId(HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication()));

        IncidentTicketTrack incidentTicketTrackNew = new IncidentTicketTrack();
        incidentTicketTrackNew.setIncidentTicket(incidentTicketTrack.getIncidentTicket());
        incidentTicketTrackNew.setRemarks(incidentTicketTrack.getRemarks());
        incidentTicketTrackNew.setUser(user);
        String status = "Remarks Added";
        if (user.getRole().getName().equalsIgnoreCase("ROLE_OEM")) {
            Firm firm = user.getFirm();
            status = "Remarks Added";
        }
        incidentTicketTrackNew.setStatus(status);

        incidentTicketTrackService.addIncidentTicketRemark(incidentTicketTrackNew);

        return ResponseDTO.<Object>builder()
                .data("Updated Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @PutMapping("/")
    public ResponseDTO<?> updateIncidentTicketTrack(@Valid @RequestBody IncidentTicketTrack incidentTicketTrack) {
        incidentTicketTrackService.updateIncidentTicketTrack(incidentTicketTrack);
        return ResponseDTO.<Object>builder()
                .data("Updated Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseDTO<?> deleteIncidentTicketTrack(@PathVariable @Valid Long id){
        incidentTicketTrackService.deleteIncidentTicketTrackById(id);
        return ResponseDTO.<Object>builder()
                .data("Deleted Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PostMapping("/import/")
    public ResponseDTO<?> importIncidentTicketTracks(@Valid MultipartFile excelSheet) throws Exception {

        if (excelSheet == null){
            throw new Exception("Excel File Required");
        }

        int rowInserted = incidentTicketTrackService.importByExcelSheet(excelSheet);

        return ResponseDTO.<Object>builder()
                .data(rowInserted + " Rows Inserted")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

}
