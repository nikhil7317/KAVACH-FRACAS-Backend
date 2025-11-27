package com.railbit.tcasanalysis.controller.tcascontroller;

import com.railbit.tcasanalysis.DTO.ResponseDTO;
import com.railbit.tcasanalysis.entity.Tcas;
import com.railbit.tcasanalysis.service.TcasService;
import com.railbit.tcasanalysis.service.TcasService;
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
@RequestMapping("/tcasapi/tcas")
public class TcasController {
    private final TcasService tcasService;
    @GetMapping("/")
    public ResponseDTO<List<Tcas>> getAllTcas(){
        return ResponseDTO.<List<Tcas>>builder()
                .data(tcasService.getAllTcas())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/{id}")
    public ResponseDTO<?> getTcasById(@PathVariable @Valid Integer id){
        return ResponseDTO.<Tcas>builder()
                .data(tcasService.getTcasById(id))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PostMapping("/")
    public ResponseDTO<?>addTcas(@Valid @RequestBody Tcas tcas) {
        System.out.println("Running");
        return ResponseDTO.<Object>builder()
                .data(tcasService.postTcas(tcas))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PutMapping("/")
    public ResponseDTO<?> updateTcas(@Valid @RequestBody Tcas tcas) {
        tcasService.updateTcas(tcas);
        return ResponseDTO.<Object>builder()
                .data("Updated Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @DeleteMapping("/{id}")
    public ResponseDTO<?> deleteTcas(@PathVariable @Valid Integer id){
        tcasService.deleteTcasById(id);
        return ResponseDTO.<Object>builder()
                .data("Deleted Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PostMapping("/import/")
    public ResponseDTO<?> importTcas(@Valid MultipartFile excelSheet) throws Exception {

        if (excelSheet == null){
            throw new Exception("Excel File Required");
        }

        int rowInserted = tcasService.importByExcelSheet(excelSheet);

        return ResponseDTO.<Object>builder()
                .data(rowInserted + " Rows Inserted")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

}
