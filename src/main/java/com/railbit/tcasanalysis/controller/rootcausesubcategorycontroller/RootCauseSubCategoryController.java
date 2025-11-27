package com.railbit.tcasanalysis.controller.rootcausesubcategorycontroller;

import com.railbit.tcasanalysis.DTO.ResponseDTO;
import com.railbit.tcasanalysis.entity.PossibleIssue;
import com.railbit.tcasanalysis.entity.RootCauseSubCategory;
import com.railbit.tcasanalysis.service.RootCauseSubCategoryService;
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
@RequestMapping("/tcasapi/rootCauseSubCategory")
public class RootCauseSubCategoryController {
    private final RootCauseSubCategoryService rootCauseSubCategoryService;
    @GetMapping("/")
    public ResponseDTO<List<RootCauseSubCategory>> getAllRootCauseSubCategories(){
        return ResponseDTO.<List<RootCauseSubCategory>>builder()
                .data(rootCauseSubCategoryService.getAllRootCauseSubCategorys())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @GetMapping("/getRootCauseSubCategoryByName/{name}")
    public ResponseDTO<?> getRootCauseSubCategoryByName(@PathVariable @Valid String name) {
        List<RootCauseSubCategory> subCategories = rootCauseSubCategoryService.getRootCauseSubCategoryByName(name);

        return ResponseDTO.<List<RootCauseSubCategory>>builder()
                .data(subCategories)
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @GetMapping("/getRootCauseSubCategoryByNameAndCategoryId")
    public ResponseDTO<?> getRootCauseSubCategoryByNameAndCategoryId(
            @RequestParam(required = false) String name,
            @RequestParam @Valid Integer categoryId) {
        return ResponseDTO.<RootCauseSubCategory>builder()
                .data(rootCauseSubCategoryService.findByNameAndPossibleRootCauseId(name, categoryId)) //
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @GetMapping("/getAllByRootCause/{rootCauseId}")
    public ResponseDTO<List<RootCauseSubCategory>> getAllPossibleIssueByIssueCategory(@PathVariable @Valid Integer rootCauseId){
        return ResponseDTO.<List<RootCauseSubCategory>>builder()
                .data(rootCauseSubCategoryService.getAllByRootCause(rootCauseId))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @GetMapping("/{id}")
    public ResponseDTO<?> getRootCauseSubCategoryById(@PathVariable @Valid Integer id){
        return ResponseDTO.<RootCauseSubCategory>builder()
                .data(rootCauseSubCategoryService.getRootCauseSubCategoryById(id))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PostMapping("/")
    public ResponseDTO<?>addRootCauseSubCategory(@Valid @RequestBody RootCauseSubCategory rootCauseSubCategory) {
        System.out.println("Running");
        return ResponseDTO.<Object>builder()
                .data(rootCauseSubCategoryService.postRootCauseSubCategory(rootCauseSubCategory))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PutMapping("/")
    public ResponseDTO<?> updateRootCauseSubCategory(@Valid @RequestBody RootCauseSubCategory rootCauseSubCategory) {
        rootCauseSubCategoryService.updateRootCauseSubCategory(rootCauseSubCategory);
        return ResponseDTO.<Object>builder()
                .data("Updated Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @DeleteMapping("/{id}")
    public ResponseDTO<?> deleteRootCauseSubCategory(@PathVariable @Valid Integer id){
        rootCauseSubCategoryService.deleteRootCauseSubCategoryById(id);
        return ResponseDTO.<Object>builder()
                .data("Deleted Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PostMapping("/import/")
    public ResponseDTO<?> importRootCauseSubCategorys(@Valid MultipartFile excelSheet) throws Exception {

        if (excelSheet == null){
            throw new Exception("Excel File Required");
        }

        int rowInserted = rootCauseSubCategoryService.importByExcelSheet(excelSheet);

        return ResponseDTO.<Object>builder()
                .data(rowInserted + " Rows Inserted")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }



}
