package com.railbit.tcasanalysis.controller.possiblerootcausecontroller;

import com.railbit.tcasanalysis.DTO.ResponseDTO;
import com.railbit.tcasanalysis.entity.PossibleIssue;
import com.railbit.tcasanalysis.entity.PossibleRootCause;
import com.railbit.tcasanalysis.service.PossibleRootCauseService;
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
@RequestMapping("/tcasapi/possibleRootCause")
public class PossibleRootCauseController {
    private final PossibleRootCauseService possibleRootCauseService;
    @GetMapping("/")
    public ResponseDTO<List<PossibleRootCause>> getAllPossibleRootCauses(){
        return ResponseDTO.<List<PossibleRootCause>>builder()
                .data(possibleRootCauseService.getAllPossibleRootCauses())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @GetMapping("/getAllPossibleRootCausesByProjectType/{projectTypeId}")
    public ResponseDTO<List<PossibleRootCause>> getAllPossibleRootCausesByProjectType(@PathVariable @Valid Integer projectTypeId){
        return ResponseDTO.<List<PossibleRootCause>>builder()
                .data(possibleRootCauseService.getAllPossibleRootCausesByProjectType(projectTypeId))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @GetMapping("/getPossibleRootCauseByName/{name}")
    public ResponseDTO<?> getPossibleRootCauseByName(@PathVariable @Valid String name){
        return ResponseDTO.<PossibleRootCause>builder()
                .data(possibleRootCauseService.getPossibleRootCauseByName(name))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @GetMapping("/{id}")
    public ResponseDTO<?> getPossibleRootCauseById(@PathVariable @Valid Integer id){
        return ResponseDTO.<PossibleRootCause>builder()
                .data(possibleRootCauseService.getPossibleRootCauseById(id))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PostMapping("/")
    public ResponseDTO<?>addPossibleRootCause(@Valid @RequestBody PossibleRootCause possibleRootCause) {
        System.out.println("Running");
        return ResponseDTO.<Object>builder()
                .data(possibleRootCauseService.postPossibleRootCause(possibleRootCause))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PutMapping("/")
    public ResponseDTO<?> updatePossibleRootCause(@Valid @RequestBody PossibleRootCause possibleRootCause) {
        possibleRootCauseService.updatePossibleRootCause(possibleRootCause);
        return ResponseDTO.<Object>builder()
                .data("Updated Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @DeleteMapping("/{id}")
    public ResponseDTO<?> deletePossibleRootCause(@PathVariable @Valid Integer id){
        possibleRootCauseService.deletePossibleRootCauseById(id);
        return ResponseDTO.<Object>builder()
                .data("Deleted Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PostMapping("/import/")
    public ResponseDTO<?> importPossibleRootCauses(@Valid MultipartFile excelSheet) throws Exception {

        if (excelSheet == null){
            throw new Exception("Excel File Required");
        }

        int rowInserted = possibleRootCauseService.importByExcelSheet(excelSheet);

        return ResponseDTO.<Object>builder()
                .data(rowInserted + " Rows Inserted")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }



}
