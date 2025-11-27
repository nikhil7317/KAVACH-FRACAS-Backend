package com.railbit.tcasanalysis.service;



import com.railbit.tcasanalysis.entity.Division;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DivisionService {
    List<Division> getAllDivisionByZone(Integer zoneId);
    Division getDivisionByName(String name);
    Division getDivisionByCode(String code);
    Division getDivisionById(Integer id);
    List<Division> getAllDivision();
    int postDivision(Division division);
    void updateDivision(Division division);
    void deleteDivisionById(Integer id);
    int importByExcelSheet(MultipartFile excelSheet) throws Exception;
}
