package com.railbit.tcasanalysis.controller.divisioncontroller;

import com.railbit.tcasanalysis.DTO.ResponseDTO;
import com.railbit.tcasanalysis.entity.Division;
import com.railbit.tcasanalysis.entity.Station;
import com.railbit.tcasanalysis.service.DivisionService;
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
@RequestMapping("/tcasapi/division")
public class DivisionController {
    private final DivisionService divisionService;

    @GetMapping("/getAllDivisionByZone/{zoneId}")
    public ResponseDTO<List<Division>> getAllDivisionByZone(@PathVariable @Valid Integer zoneId){

        List<Division> divisionList = new ArrayList<>();
        if (zoneId <= 0) {
            divisionList = divisionService.getAllDivision();
        } else {
            divisionList = divisionService.getAllDivisionByZone(zoneId);
        }

        return ResponseDTO.<List<Division>>builder()
                .data(divisionList)
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/")
    public ResponseDTO<List<Division>> getAllDivisions(){
        return ResponseDTO.<List<Division>>builder()
                .data(divisionService.getAllDivision())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/{id}")
    public ResponseDTO<?> getDivisionById(@PathVariable @Valid Integer id){
        return ResponseDTO.<Division>builder()
                .data(divisionService.getDivisionById(id))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getDivisionByCode/{code}")
    public ResponseDTO<?> getDivisionByCode(@PathVariable @Valid String code){

        return ResponseDTO.<Division>builder()
                .data(divisionService.getDivisionByCode(code))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();

    }

    @PostMapping("/")
    public ResponseDTO<?>addDivision(@Valid @RequestBody Division division) {
        System.out.println("Running");
        return ResponseDTO.<Object>builder()
                .data(divisionService.postDivision(division))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PutMapping("/")
    public ResponseDTO<?> updateDivision(@Valid @RequestBody Division division) {
        divisionService.updateDivision(division);
        return ResponseDTO.<Object>builder()
                .data("Updated Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @DeleteMapping("/{id}")
    public ResponseDTO<?> deleteDivision(@PathVariable @Valid Integer id){
        divisionService.deleteDivisionById(id);
        return ResponseDTO.<Object>builder()
                .data("Deleted Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PostMapping("/import/")
    public ResponseDTO<?> importDivisions(@Valid MultipartFile excelSheet) throws Exception {

        if (excelSheet == null){
            throw new Exception("Excel File Required");
        }

        int rowInserted = divisionService.importByExcelSheet(excelSheet);

        return ResponseDTO.<Object>builder()
                .data(rowInserted + " Rows Inserted")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

}
