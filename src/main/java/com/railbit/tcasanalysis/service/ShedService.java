package com.railbit.tcasanalysis.service;



import com.railbit.tcasanalysis.entity.loco.Shed;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ShedService {
    Shed getShedByName(String name);
    Shed getShedById(Integer id);
    List<Shed> getAllShed();
    int postShed(Shed shed);
    void updateShed(Shed shed);
    void deleteShedById(Integer id);
    int importByExcelSheet(MultipartFile excelSheet) throws Exception;
}
