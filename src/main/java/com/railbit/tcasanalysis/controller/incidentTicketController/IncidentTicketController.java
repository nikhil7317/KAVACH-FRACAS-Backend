package com.railbit.tcasanalysis.controller.incidentTicketController;

import com.railbit.tcasanalysis.DTO.ResponseDTO;
import com.railbit.tcasanalysis.entity.Firm;
import com.railbit.tcasanalysis.entity.IncidentTicket;
import com.railbit.tcasanalysis.entity.TcasBreakingInspection;
import com.railbit.tcasanalysis.service.IncidentTicketService;
import com.railbit.tcasanalysis.util.Constants;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
@RequestMapping("/tcasapi/incidentTicket")
public class IncidentTicketController {

    @Autowired
    private IncidentTicketService incidentTicketService;

    @GetMapping("/getTicketsByStatus/")
    public ResponseDTO<List<IncidentTicket>> getTicketsByStatus(@RequestParam Boolean status,
                                                                @RequestParam Integer divisionId){

        List<IncidentTicket> tickets = incidentTicketService.getTicketsByStatus(divisionId,status);

        return ResponseDTO.<List<IncidentTicket>>builder()
                .data(tickets)
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/")
    public ResponseDTO<List<IncidentTicket>> getAllIncidentTickets(){
        return ResponseDTO.<List<IncidentTicket>>builder()
                .data(incidentTicketService.getAllIncidentTicket())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getAssignedFirmsByTicketId/{ticketId}")
    public ResponseDTO<Set<Firm>> getAssignedFirmsByTicketId(@PathVariable Long ticketId) {
        Set<Firm> assignedFirms = incidentTicketService.getAssignedFirmsByTicketId(ticketId);
        return ResponseDTO.<Set<Firm>>builder()
                .data(assignedFirms)
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getIncidentsByTicketId/{ticketId}")
    public ResponseDTO<List<TcasBreakingInspection>> getIncidentsByTicketId(@PathVariable Long ticketId) {
        List<TcasBreakingInspection> incidents = incidentTicketService.getInspectionsByIncidentTicketId(ticketId);
        return ResponseDTO.<List<TcasBreakingInspection>>builder()
                .data(incidents)
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/{id}")
    public ResponseDTO<?> getIncidentTicketById(@PathVariable @Valid Long id){
        return ResponseDTO.<IncidentTicket>builder()
                .data(incidentTicketService.getIncidentTicketById(id))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getIncidentTicketByNo")
    public ResponseDTO<?> getIncidentTicketByNo(@RequestParam @Valid String ticketNo){
        return ResponseDTO.<IncidentTicket>builder()
                .data(incidentTicketService.getIncidentTicketByNo(ticketNo))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @PostMapping("/")
    public ResponseDTO<?>addIncidentTicket(@Valid @RequestBody IncidentTicket incidentTicket) {
        System.out.println("Running");
        return ResponseDTO.<Object>builder()
                .data(incidentTicketService.postIncidentTicket(incidentTicket))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @PutMapping("/")
    public ResponseDTO<?> updateIncidentTicket(@Valid @RequestBody IncidentTicket incidentTicket) {
        incidentTicketService.updateIncidentTicket(incidentTicket);
        return ResponseDTO.<Object>builder()
                .data("Updated Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseDTO<?> deleteIncidentTicket(@PathVariable @Valid Long id){
        incidentTicketService.deleteIncidentTicketById(id);
        return ResponseDTO.<Object>builder()
                .data("Deleted Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @PostMapping("/import/")
    public ResponseDTO<?> importIncidentTickets(@Valid MultipartFile excelSheet) throws Exception {

        if (excelSheet == null){
            throw new Exception("Excel File Required");
        }

        int rowInserted = incidentTicketService.importByExcelSheet(excelSheet);

        return ResponseDTO.<Object>builder()
                .data(rowInserted + " Rows Inserted")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

}
