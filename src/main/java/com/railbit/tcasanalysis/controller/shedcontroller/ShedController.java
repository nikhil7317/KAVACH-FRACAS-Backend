package com.railbit.tcasanalysis.controller.shedcontroller;

import com.railbit.tcasanalysis.DTO.ResponseDTO;
import com.railbit.tcasanalysis.entity.loco.Shed;
import com.railbit.tcasanalysis.service.ShedService;
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
@RequestMapping("/tcasapi/shed")
public class ShedController {
    private final ShedService shedService;
    @GetMapping("/")
    public ResponseDTO<List<Shed>> getAllSheds(){
        return ResponseDTO.<List<Shed>>builder()
                .data(shedService.getAllShed())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @GetMapping("/{id}")
    public ResponseDTO<?> getShedById(@PathVariable @Valid Integer id){
        return ResponseDTO.<Shed>builder()
                .data(shedService.getShedById(id))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PostMapping("/")
    public ResponseDTO<?>addShed(@Valid @RequestBody Shed shed) {
        System.out.println("Running");
        return ResponseDTO.<Object>builder()
                .data(shedService.postShed(shed))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PutMapping("/")
    public ResponseDTO<?> updateShed(@Valid @RequestBody Shed shed) {
        shedService.updateShed(shed);
        return ResponseDTO.<Object>builder()
                .data("Updated Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @DeleteMapping("/{id}")
    public ResponseDTO<?> deleteShed(@PathVariable @Valid Integer id){
        shedService.deleteShedById(id);
        return ResponseDTO.<Object>builder()
                .data("Deleted Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PostMapping("/import/")
    public ResponseDTO<?> importSheds(@Valid MultipartFile excelSheet) throws Exception {

        if (excelSheet == null){
            throw new Exception("Excel File Required");
        }
        int rowInserted = shedService.importByExcelSheet(excelSheet);
        return ResponseDTO.<Object>builder()
                .data(rowInserted + " Rows Inserted")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

}
