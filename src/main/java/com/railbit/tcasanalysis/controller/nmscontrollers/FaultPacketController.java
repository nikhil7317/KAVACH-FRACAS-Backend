package com.railbit.tcasanalysis.controller.nmscontrollers;

import com.railbit.tcasanalysis.DTO.FaultPacketDTO;
import com.railbit.tcasanalysis.DTO.ResponseDTO;
import com.railbit.tcasanalysis.DTO.FaultCodeCountDTO;
import com.railbit.tcasanalysis.entity.nmspackets.FaultPacket;
import com.railbit.tcasanalysis.service.FaultPacketService;
import com.railbit.tcasanalysis.util.Constants;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
@RequestMapping("/tcasapi/faultPacket")
public class FaultPacketController {
    private final FaultPacketService faultPacketService;

    @GetMapping("/view/")
    public ResponseDTO<Object>viewFaultPacketData(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "100") Integer size,
                                                  @RequestParam(value = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime fromDate,
                                                  @RequestParam(value = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime toDate) throws ParseException {

        Map<String, Object> faultPacketData = faultPacketService.getAllFaultPacketData(page, size, fromDate, toDate);
        return ResponseDTO.builder()
                .status(HttpStatus.OK.value())
                .message(Constants.SUCCESS_MSG)
                .data(faultPacketData.get("data"))
                .totalRecords(Integer.valueOf(faultPacketData.get("totalRecords").toString()))
                .build();
    }

    @GetMapping("/{id}")
    public ResponseDTO<?> getFaultPacketById(@PathVariable @Valid Long id){
        return ResponseDTO.<FaultPacket>builder()
                .data(faultPacketService.getFaultPacketById(id))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getFaultCodeCountsByTime")
    public ResponseDTO<?> getFaultCodeCountsByTime(){
        return ResponseDTO.<List<FaultCodeCountDTO> >builder()
                .data(faultPacketService.getFaultCodeCountsByTime())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @PostMapping("/")
    public ResponseDTO<?>addFaultPacket(@Valid @RequestBody FaultPacketDTO faultPacket) {
        //System.out.println("Running");
        return ResponseDTO.<Object>builder()
                .data(faultPacketService.postFaultPacket(faultPacket))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @PostMapping("/addFaultyPacket/")
    public ResponseDTO<?>addFaultyPacket(@Valid @RequestBody FaultPacketDTO faultPacket) {
        return ResponseDTO.<Object>builder()
                .data(faultPacketService.postFaultPacket(faultPacket))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @PutMapping("/")
    public ResponseDTO<?> updateFaultPacket(@Valid @RequestBody FaultPacket faultPacket) {
        faultPacketService.updateFaultPacket(faultPacket);
        return ResponseDTO.<Object>builder()
                .data("Updated Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseDTO<?> deleteFaultPacket(@PathVariable @Valid Long id){
        faultPacketService.deleteFaultPacketById(id);
        return ResponseDTO.<Object>builder()
                .data("Deleted Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @PostMapping("/import/")
    public ResponseDTO<?> importFaultPackets(@Valid MultipartFile excelSheet) throws Exception {

        if (excelSheet == null){
            throw new Exception("Excel File Required");
        }

        int rowInserted = faultPacketService.importByExcelSheet(excelSheet);

        return ResponseDTO.<Object>builder()
                .data(rowInserted + " Rows Inserted")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

}
