package com.railbit.tcasanalysis.controller.firmcontroller;

import com.railbit.tcasanalysis.DTO.ResponseDTO;
import com.railbit.tcasanalysis.entity.Division;
import com.railbit.tcasanalysis.entity.Firm;
import com.railbit.tcasanalysis.service.FirmService;
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
@RequestMapping("/tcasapi/firm")
public class FirmController {
    private final FirmService firmService;
    @GetMapping("/")
    public ResponseDTO<List<Firm>> getAllFirms(){
        return ResponseDTO.<List<Firm>>builder()
                .data(firmService.getAllFirm())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/{id}")
    public ResponseDTO<?> getTcasBreakingInspectionById(@PathVariable @Valid Integer id){
        return ResponseDTO.<Firm>builder()
                .data(firmService.getFirmById(id))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PostMapping("/")
    public ResponseDTO<?>addTcasBreakingInspection(@Valid @RequestBody Firm firm) {
        System.out.println("Running");
        return ResponseDTO.<Object>builder()
                .data(firmService.postFirm(firm))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PutMapping("/")
    public ResponseDTO<?> updateFirm(@Valid @RequestBody Firm firm) {
        firmService.updateFirm(firm);
        return ResponseDTO.<Object>builder()
                .data("Updated Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @DeleteMapping("/{id}")
    public ResponseDTO<?> deleteUser(@PathVariable @Valid Integer id){
        firmService.deleteFirmById(id);
        return ResponseDTO.<Object>builder()
                .data("Deleted Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PostMapping("/import/")
    public ResponseDTO<?> importFirms(@Valid MultipartFile excelSheet) throws Exception {

        if (excelSheet == null){
            throw new Exception("Excel File Required");
        }

        int rowInserted = firmService.importByExcelSheet(excelSheet);

        return ResponseDTO.<Object>builder()
                .data(rowInserted + " Rows Inserted")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @GetMapping("/getFirmNames/")
    public ResponseDTO<List<String>> getAllFirmNos(){
        return ResponseDTO.<List<String>>builder()
                .data(firmService.getAllFirmNames())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

}
