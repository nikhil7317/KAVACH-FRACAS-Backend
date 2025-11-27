package com.railbit.tcasanalysis.controller.tagcontroller;

import com.railbit.tcasanalysis.DTO.ResponseDTO;
import com.railbit.tcasanalysis.entity.Tag;
import com.railbit.tcasanalysis.service.TagService;
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
@RequestMapping("/tcasapi/tag")
public class TagController {

    private final TagService TagService;

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

}
