package com.railbit.tcasanalysis.controller.stationcontroller;

import com.railbit.tcasanalysis.DTO.ResponseDTO;
import com.railbit.tcasanalysis.entity.Role;
import com.railbit.tcasanalysis.entity.Station;
import com.railbit.tcasanalysis.service.StationService;
import com.railbit.tcasanalysis.util.Constants;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
@RequestMapping("/tcasapi/station")
public class StationController {
    private final StationService stationService;
    @GetMapping("/")
    public ResponseDTO<List<Station>> getAllStations(){
        return ResponseDTO.<List<Station>>builder()
                .data(stationService.getAllStations())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getAllStationsBySection/{sectionId}")
    public ResponseDTO<List<Station>> getAllStationsBySection(@PathVariable @Valid Integer sectionId){

        List<Station> stationList = new ArrayList<>();
        stationList = stationService.getAllStations();
//        if (sectionId <= 0) {
//            stationList = stationService.getAllStations();
//        } else {
//            stationList = stationService.getAllStationsBySection(sectionId);
//        }

        return ResponseDTO.<List<Station>>builder()
                .data(stationList)
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getAllStationsByZone/{zoneId}")
    public ResponseDTO<List<Station>> getAllStationsByZone(@PathVariable @Valid Integer zoneId){

        List<Station> stationList = new ArrayList<>();
        if (zoneId <= 0) {
            stationList = stationService.getAllStations();
        } else {
            stationList = stationService.getAllStationsByZone(zoneId);
        }

        return ResponseDTO.<List<Station>>builder()
                .data(stationList)
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getAllStationsByDivision/{divisionId}")
    public ResponseDTO<List<Station>> getAllStationsByDivision(@PathVariable @Valid Integer divisionId){

        List<Station> stationList = new ArrayList<>();
        if (divisionId <= 0) {
            stationList = stationService.getAllStations();
        } else {
            stationList = stationService.getAllStationsByDivision(divisionId);
        }

        return ResponseDTO.<List<Station>>builder()
                .data(stationList)
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @GetMapping("/{id}")
    public ResponseDTO<?> getStationById(@PathVariable @Valid Integer id){
        return ResponseDTO.<Station>builder()
                .data(stationService.getStationById(id))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @GetMapping("/getStationByCode/{code}")
    public ResponseDTO<?> getStationByCode(@PathVariable @Valid String code){
        return ResponseDTO.<Station>builder()
                .data(stationService.getStationByCode(code))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @PostMapping("/")
    public ResponseDTO<?>addStation(@Valid @RequestBody Station station) {
        System.out.println("Running");
        return ResponseDTO.<Object>builder()
                .data(stationService.postStation(station))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @PutMapping("/")
    public ResponseDTO<?> updateRole(@Valid @RequestBody Station station) {
        stationService.updateStation(station);
        return ResponseDTO.<Object>builder()
                .data("Updated Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @DeleteMapping("/{id}")
    public ResponseDTO<?> deleteStation(@PathVariable @Valid Integer id){
        stationService.deleteStationById(id);
        return ResponseDTO.<Object>builder()
                .data("Deleted Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PostMapping("/import/")
    public ResponseDTO<?> importStations(@Valid MultipartFile excelSheet) throws Exception {

        if (excelSheet == null){
            throw new Exception("Excel File Required");
        }

        int rowInserted = stationService.importByExcelSheet(excelSheet);

        return ResponseDTO.<Object>builder()
                .data(rowInserted + " Rows Inserted")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }



}
