package com.railbit.tcasanalysis.controller.zonecontroller;

import com.railbit.tcasanalysis.DTO.ResponseDTO;
import com.railbit.tcasanalysis.entity.Zone;
import com.railbit.tcasanalysis.service.ZoneService;
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
@RequestMapping("/tcasapi/zone")
public class ZoneController {

    private final ZoneService zoneService;

    @GetMapping("/")
    public ResponseDTO<List<Zone>> getAllZones(){
        return ResponseDTO.<List<Zone>>builder()
                .data(zoneService.getAllZones())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())  
                .build();
    }

    @GetMapping("/{id}")
    public ResponseDTO<?> getZoneById(@PathVariable @Valid Integer id){
        return ResponseDTO.<Zone>builder()
                .data(zoneService.getZoneById(id))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @GetMapping("/getZoneByCode/{code}")
    public ResponseDTO<?> getZoneByCode(@PathVariable @Valid String code){
        return ResponseDTO.<Zone>builder()
                .data(zoneService.getZoneByCode(code))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PostMapping("/")
    public ResponseDTO<?>addZone(@Valid @RequestBody Zone zone) {
        System.out.println("Running");
        return ResponseDTO.<Object>builder()
                .data(zoneService.postZone(zone))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PutMapping("/")
    public ResponseDTO<?> updateZone(@Valid @RequestBody Zone zone) {
        zoneService.updateZone(zone);
        return ResponseDTO.<Object>builder()
                .data("Updated Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @DeleteMapping("/{id}")
    public ResponseDTO<?> deleteZone(@PathVariable @Valid Integer id){
        zoneService.deleteZoneById(id);
        return ResponseDTO.<Object>builder()
                .data("Deleted Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PostMapping("/import/")
    public ResponseDTO<?> importZones(@Valid MultipartFile excelSheet) throws Exception {

        if (excelSheet == null){
            throw new Exception("Excel File Required");
        }

        int rowInserted = zoneService.importByExcelSheet(excelSheet);

        return ResponseDTO.<Object>builder()
                .data(rowInserted + " Rows Inserted")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

}
