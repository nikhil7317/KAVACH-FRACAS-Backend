package com.railbit.tcasanalysis.controller.possibleissuecontroller;

import com.railbit.tcasanalysis.DTO.ResponseDTO;
import com.railbit.tcasanalysis.entity.PossibleIssue;
import com.railbit.tcasanalysis.entity.loco.LocoType;
import com.railbit.tcasanalysis.service.PossibleIssueService;
import com.railbit.tcasanalysis.util.Constants;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
// Sub Category
@RestController
@AllArgsConstructor
@CrossOrigin("*")
@RequestMapping("/tcasapi/possibleIssue")
public class PossibleIssueController {
    private final PossibleIssueService possibleIssueService;
    @GetMapping("/")
    public ResponseDTO<List<PossibleIssue>> getAllPossibleIssues(){
        return ResponseDTO.<List<PossibleIssue>>builder()
                .data(possibleIssueService.getAllPossibleIssues())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @GetMapping("/getAllByIssueCategory/{issueCategoryId}")
    public ResponseDTO<List<PossibleIssue>> getAllPossibleIssueByIssueCategory(@PathVariable @Valid Integer issueCategoryId){
        return ResponseDTO.<List<PossibleIssue>>builder()
                .data(possibleIssueService.getAllPossibleIssueByIssueCategory(issueCategoryId))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @GetMapping("/getAllPossibleIssuesByProjectType/{projectTypeId}")
    public ResponseDTO<List<PossibleIssue>> getAllPossibleIssuesByProjectType(@PathVariable @Valid Integer projectTypeId){
        return ResponseDTO.<List<PossibleIssue>>builder()
                .data(possibleIssueService.getAllPossibleIssuesByProjectType(projectTypeId))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @GetMapping("/getPossibleIssueByName/{name}")
    public ResponseDTO<?> getPossibleIssueByName(@PathVariable @Valid String name){
        return ResponseDTO.<PossibleIssue>builder()
                .data(possibleIssueService.getPossibleIssueByName(name))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @GetMapping("/getPossibleIssueByNameAndCategoryId")
    public ResponseDTO<?> getPossibleIssueByNameAndCategoryId(
            @RequestParam(required = false) String name,
            @RequestParam @Valid Integer categoryId){
        return ResponseDTO.<PossibleIssue>builder()
                .data(possibleIssueService.getPossibleIssueByNameAndIssueCategoryId(name, categoryId)) //
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @GetMapping("/{id}")
    public ResponseDTO<?> getPossibleIssueById(@PathVariable @Valid Integer id){
        return ResponseDTO.<PossibleIssue>builder()
                .data(possibleIssueService.getPossibleIssueById(id))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PostMapping("/")
    public ResponseDTO<?>addPossibleIssue(@Valid @RequestBody PossibleIssue possibleIssue) {
        System.out.println("Running");
        return ResponseDTO.<Object>builder()
                .data(possibleIssueService.postPossibleIssue(possibleIssue))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PutMapping("/")
    public ResponseDTO<?> updatePossibleIssue(@Valid @RequestBody PossibleIssue possibleIssue) {
        possibleIssueService.updatePossibleIssue(possibleIssue);
        return ResponseDTO.<Object>builder()
                .data("Updated Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @DeleteMapping("/{id}")
    public ResponseDTO<?> deletePossibleIssue(@PathVariable @Valid Integer id){
        possibleIssueService.deletePossibleIssueById(id);
        return ResponseDTO.<Object>builder()
                .data("Deleted Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PostMapping("/import/")
    public ResponseDTO<?> importPossibleIssues(@Valid MultipartFile excelSheet) throws Exception {

        if (excelSheet == null){
            throw new Exception("Excel File Required");
        }

        int rowInserted = possibleIssueService.importByExcelSheet(excelSheet);

        return ResponseDTO.<Object>builder()
                .data(rowInserted + " Rows Inserted")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }



}
