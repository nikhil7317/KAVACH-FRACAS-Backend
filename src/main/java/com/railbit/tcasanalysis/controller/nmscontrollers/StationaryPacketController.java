package com.railbit.tcasanalysis.controller.nmscontrollers;

import com.railbit.tcasanalysis.DTO.ResponseDTO;
import com.railbit.tcasanalysis.entity.nmspackets.stationarypackets.StationaryPacket;
import com.railbit.tcasanalysis.service.StationaryPacketService;
import com.railbit.tcasanalysis.util.Constants;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
@RequestMapping("/tcasapi/stationaryPacket")
public class StationaryPacketController {
    private final StationaryPacketService stationaryPacketService;
    @GetMapping("/")
    public ResponseDTO<List<StationaryPacket>> getAllStationaryPackets(){
        return ResponseDTO.<List<StationaryPacket>>builder()
                .data(stationaryPacketService.getAllStationaryPacket())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/latest100")
    public ResponseDTO<List<StationaryPacket>> get100StationaryPacket(){
        return ResponseDTO.<List<StationaryPacket>>builder()
                .data(stationaryPacketService.get100StationaryPacket())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/{id}")
    public ResponseDTO<?> getStationaryPacketById(@PathVariable @Valid Long id){
        return ResponseDTO.<StationaryPacket>builder()
                .data(stationaryPacketService.getStationaryPacketById(id))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

//    @GetMapping("/getFaultCodeCountsByTime")
//    public ResponseDTO<?> getFaultCodeCountsByTime(){
//        return ResponseDTO.<List<FaultCodeCountDTO> >builder()
//                .data(stationaryPacketService.getFaultCodeCountsByTime())
//                .message(Constants.SUCCESS_MSG)
//                .status(HttpStatus.OK.value())
//                .build();
//    }

    @PostMapping("/")
    public ResponseDTO<?>postStationaryPacket(@Valid @RequestBody StationaryPacket stationaryPacket) {
        System.out.println("Running");
        return ResponseDTO.<Object>builder()
                .data(stationaryPacketService.postStationaryPacket(stationaryPacket))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PostMapping("/addStationaryPacket/")
    public ResponseDTO<?>addStationaryPacket(@Valid @RequestBody StationaryPacket stationaryPacket) {
        return ResponseDTO.<Object>builder()
                .data(stationaryPacketService.postStationaryPacket(stationaryPacket))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PutMapping("/")
    public ResponseDTO<?> updateStationaryPacket(@Valid @RequestBody StationaryPacket stationaryPacket) {
        stationaryPacketService.updateStationaryPacket(stationaryPacket);
        return ResponseDTO.<Object>builder()
                .data("Updated Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @DeleteMapping("/{id}")
    public ResponseDTO<?> deleteStationaryPacket(@PathVariable @Valid Long id){
        stationaryPacketService.deleteStationaryPacketById(id);
        return ResponseDTO.<Object>builder()
                .data("Deleted Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PostMapping("/import/")
    public ResponseDTO<?> importStationaryPackets(@Valid MultipartFile excelSheet) throws Exception {

        if (excelSheet == null){
            throw new Exception("Excel File Required");
        }

        int rowInserted = stationaryPacketService.importByExcelSheet(excelSheet);

        return ResponseDTO.<Object>builder()
                .data(rowInserted + " Rows Inserted")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

}
