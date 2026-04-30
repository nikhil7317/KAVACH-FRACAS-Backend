package com.railbit.tcasanalysis.controller.tagcontroller;

import com.railbit.tcasanalysis.DTO.ResponseDTO;
import com.railbit.tcasanalysis.entity.Tag;
import com.railbit.tcasanalysis.service.MissingTagReportService;
import com.railbit.tcasanalysis.service.TagService;
import com.railbit.tcasanalysis.util.Constants;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
@RequestMapping("/tcasapi/tag")
public class TagController {

    private final TagService TagService;

    @Autowired
    private MissingTagReportService missingTagReportService;

    @GetMapping("/")
    public ResponseDTO<List<Tag>> getAllTags(){
        return ResponseDTO.<List<Tag>>builder()
                .data(TagService.getAllTags())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())  
                .build();
    }

    @GetMapping("/{id}")
    public ResponseDTO<?> getTagById(@PathVariable @Valid Long id){
        return ResponseDTO.<Tag>builder()
                .data(TagService.getTagById(id))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @PostMapping("/")
    public ResponseDTO<?>addTag(@Valid @RequestBody Tag Tag) {
        System.out.println("Running");
        return ResponseDTO.<Object>builder()
                .data(TagService.postTag(Tag))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @PutMapping("/")
    public ResponseDTO<?> updateTag(@Valid @RequestBody Tag Tag) {
        TagService.updateTag(Tag);
        return ResponseDTO.<Object>builder()
                .data("Updated Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseDTO<?> deleteTag(@PathVariable @Valid Long id){
        TagService.deleteTagById(id);
        return ResponseDTO.<Object>builder()
                .data("Deleted Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @PostMapping("/import/")
    public ResponseDTO<?> importTags(@Valid MultipartFile excelSheet) throws Exception {

        if (excelSheet == null){
            throw new Exception("Excel File Required");
        }

        int rowInserted = TagService.importByExcelSheet(excelSheet);

        return ResponseDTO.<Object>builder()
                .data(rowInserted + " Rows Inserted")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }



    @GetMapping("/missing-tags")
    public ResponseEntity<?> getMissingTagReport(

            @RequestParam("fromDate")
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date fromDate,

            @RequestParam("toDate")
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date toDate,

            @RequestParam(value = "locoId", required = false) Integer locoId,
            @RequestParam(value = "alertCategory", required = false) String alertCategory,
            @RequestParam(value = "severity", required = false) String severity,

            @RequestParam(value = "stnId", required = false) Integer stnId // ✅ CHANGED
    ) {

        Map<String, Object> report =
                missingTagReportService.getMissingTagReport(
                        fromDate, toDate, locoId, alertCategory, severity, stnId);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "filters", Map.of(
                        "fromDate", fromDate,
                        "toDate", toDate,
                        "locoId", locoId != null ? locoId : "all",
                        "alertCategory", alertCategory != null ? alertCategory : "all",
                        "severity", severity != null ? severity : "all",
                        "stnId", stnId != null ? stnId : "all"   // ✅ UPDATED
                ),
                "report", report
        ));
    }

    @GetMapping("/alertCategory")
    public ResponseEntity<List<Map<String, Object>>> getDistinctAlertMessages() {

        List<Map<String, Object>> data =
                missingTagReportService.getDistinctAlertMessagesWithId("TAG_LINK");

        return ResponseEntity.ok(data);
    }

}
