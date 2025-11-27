package com.railbit.tcasanalysis.service;



import com.railbit.tcasanalysis.entity.Designation;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DesignationService {
    Designation getDesignationByName(String name);
    Designation getDesignationById(Integer id);
    List<Designation> getAllDesignation();
    Designation postDesignation(Designation designation);
    void updateDesignation(Designation designation);
    void deleteDesignationById(Integer id);
    int importByExcelSheet(MultipartFile excelSheet) throws Exception;
}
