package com.railbit.tcasanalysis.service;



import com.railbit.tcasanalysis.entity.Tcas;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TcasService {
    Tcas getTcasByName(String name);
    Tcas getTcasById(Integer id);
    List<Tcas> getAllTcas();
    int postTcas(Tcas tcas);
    void updateTcas(Tcas tcas);
    void deleteTcasById(Integer id);
    int importByExcelSheet(MultipartFile excelSheet) throws Exception;
}
