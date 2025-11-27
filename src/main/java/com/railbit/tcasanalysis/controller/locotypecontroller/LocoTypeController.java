package com.railbit.tcasanalysis.controller.locotypecontroller;

import com.railbit.tcasanalysis.DTO.ResponseDTO;
import com.railbit.tcasanalysis.entity.loco.Loco;
import com.railbit.tcasanalysis.entity.loco.LocoType;
import com.railbit.tcasanalysis.service.LocoTypeService;
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
@RequestMapping("/tcasapi/locoType")
public class LocoTypeController {
    private final LocoTypeService locoTypeService;
    @GetMapping("/")
    public ResponseDTO<List<LocoType>> getAllLocoTypes(){
        return ResponseDTO.<List<LocoType>>builder()
                .data(locoTypeService.getAllLocoType())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @GetMapping("/{id}")
    public ResponseDTO<?> getLocoTypeById(@PathVariable @Valid Integer id){
        return ResponseDTO.<LocoType>builder()
                .data(locoTypeService.getLocoTypeById(id))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PostMapping("/")
    public ResponseDTO<?>addLocoType(@Valid @RequestBody LocoType locoType) {
        System.out.println("Running");
        return ResponseDTO.<Object>builder()
                .data(locoTypeService.postLocoType(locoType))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PutMapping("/")
    public ResponseDTO<?> updateLocoType(@Valid @RequestBody LocoType locoType) {
        locoTypeService.updateLocoType(locoType);
        return ResponseDTO.<Object>builder()
                .data("Updated Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @DeleteMapping("/{id}")
    public ResponseDTO<?> deleteLocoType(@PathVariable @Valid Integer id){
        locoTypeService.deleteLocoTypeById(id);
        return ResponseDTO.<Object>builder()
                .data("Deleted Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PostMapping("/import/")
    public ResponseDTO<?> importLocoTypes(@Valid MultipartFile excelSheet) throws Exception {

        if (excelSheet == null){
            throw new Exception("Excel File Required");
        }

        int rowInserted = locoTypeService.importByExcelSheet(excelSheet);

        return ResponseDTO.<Object>builder()
                .data(rowInserted + " Rows Inserted")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @GetMapping("/getLocoTypeNames/")
    public ResponseDTO<List<String>> getAllLocoTypeNames(){
        return ResponseDTO.<List<String>>builder()
                .data(locoTypeService.getAllLocoTypeNames())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

}
