package com.railbit.tcasanalysis.controller.sectioncontroller;

import com.railbit.tcasanalysis.DTO.ResponseDTO;
import com.railbit.tcasanalysis.entity.Section;
import com.railbit.tcasanalysis.entity.Station;
import com.railbit.tcasanalysis.service.SectionService;
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
@RequestMapping("/tcasapi/section")
public class SectionController {
    private final SectionService sectionService;
    @GetMapping("/")
    public ResponseDTO<List<Section>> getAllSections(){
        return ResponseDTO.<List<Section>>builder()
                .data(sectionService.getAllSection())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @GetMapping("/{id}")
    public ResponseDTO<?> getSectionById(@PathVariable @Valid Integer id){
        return ResponseDTO.<Section>builder()
                .data(sectionService.getSectionById(id))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @GetMapping("/getAllSectionsByDivision/{divisionId}")
    public ResponseDTO<List<Section>> getAllSectionsByDivision(@PathVariable @Valid Integer divisionId){

        List<Section> sectionList = new ArrayList<>();
        if (divisionId <= 0) {
            sectionList = sectionService.getAllSection();
        } else {
            sectionList = sectionService.getAllSectionByDivisionId(divisionId);
        }

        return ResponseDTO.<List<Section>>builder()
                .data(sectionList)
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PostMapping("/")
    public ResponseDTO<?>addSection(@Valid @RequestBody Section section) {
        System.out.println("Running");
        return ResponseDTO.<Object>builder()
                .data(sectionService.postSection(section))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PutMapping("/")
    public ResponseDTO<?> updateSection(@Valid @RequestBody Section section) {
        sectionService.updateSection(section);
        return ResponseDTO.<Object>builder()
                .data("Updated Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @DeleteMapping("/{id}")
    public ResponseDTO<?> deleteSection(@PathVariable @Valid Integer id){
        sectionService.deleteSectionById(id);
        return ResponseDTO.<Object>builder()
                .data("Deleted Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PostMapping("/import/")
    public ResponseDTO<?> importSections(@Valid MultipartFile excelSheet) throws Exception {

        if (excelSheet == null){
            throw new Exception("Excel File Required");
        }

        int rowInserted = sectionService.importByExcelSheet(excelSheet);

        return ResponseDTO.<Object>builder()
                .data(rowInserted + " Rows Inserted")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

}
