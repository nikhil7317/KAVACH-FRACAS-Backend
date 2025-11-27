package com.railbit.tcasanalysis.controller.issuecategorycontroller;

import com.railbit.tcasanalysis.DTO.ResponseDTO;
import com.railbit.tcasanalysis.entity.IssueCategory;
import com.railbit.tcasanalysis.service.IssueCategoryService;
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
@RequestMapping("/tcasapi/issueCategory")
public class IssueCategoryController {
    private final IssueCategoryService issueCategoryService;
    @GetMapping("/")
    public ResponseDTO<List<IssueCategory>> getAllIssueCategories(){
        return ResponseDTO.<List<IssueCategory>>builder()
                .data(issueCategoryService.getAllIssueCategory())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @GetMapping("/getIssueCategoryByName/{name}")
    public ResponseDTO<?> getIssueCategoryByName(@PathVariable @Valid String name){
        return ResponseDTO.<IssueCategory>builder()
                .data(issueCategoryService.getIssueCategoryByName(name))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @GetMapping("/{id}")
    public ResponseDTO<?> getIssueCategoryById(@PathVariable @Valid Integer id){
        return ResponseDTO.<IssueCategory>builder()
                .data(issueCategoryService.getIssueCategoryById(id))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PostMapping("/")
    public ResponseDTO<?>addIssueCategory(@Valid @RequestBody IssueCategory issueCategory) {
        System.out.println("Running");
        return ResponseDTO.<Object>builder()
                .data(issueCategoryService.postIssueCategory(issueCategory))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PutMapping("/")
    public ResponseDTO<?> updateIssueCategory(@Valid @RequestBody IssueCategory issueCategory) {
        issueCategoryService.updateIssueCategory(issueCategory);
        return ResponseDTO.<Object>builder()
                .data("Updated Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @DeleteMapping("/{id}")
    public ResponseDTO<?> deleteIssueCategory(@PathVariable @Valid Integer id){
        issueCategoryService.deleteIssueCategoryById(id);
        return ResponseDTO.<Object>builder()
                .data("Deleted Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PostMapping("/import/")
    public ResponseDTO<?> importIssueCategories(@Valid MultipartFile excelSheet) throws Exception {

        if (excelSheet == null){
            throw new Exception("Excel File Required");
        }

        int rowInserted = issueCategoryService.importByExcelSheet(excelSheet);

        return ResponseDTO.<Object>builder()
                .data(rowInserted + " Rows Inserted")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

}
