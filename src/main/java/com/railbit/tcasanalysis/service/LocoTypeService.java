package com.railbit.tcasanalysis.service;


import com.railbit.tcasanalysis.entity.loco.LocoType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface LocoTypeService {
    LocoType getLocoTypeByName(String name);
    LocoType getLocoTypeById(Integer id);
    List<LocoType> getAllLocoType();
    List<String> getAllLocoTypeNames();
    int postLocoType(LocoType locoType);
    void updateLocoType(LocoType locoType);
    void deleteLocoTypeById(Integer id);
    int importByExcelSheet(MultipartFile excelSheet) throws Exception;
}
