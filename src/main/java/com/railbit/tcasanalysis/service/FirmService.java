package com.railbit.tcasanalysis.service;


import com.railbit.tcasanalysis.entity.Firm;
import com.railbit.tcasanalysis.entity.Role;
import com.railbit.tcasanalysis.entity.loco.Loco;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FirmService {
    Firm getFirmByName(String name);
    Firm getFirmById(Integer id);
    List<Firm> getAllFirm();
    List<String> getAllFirmNames();
    int postFirm(Firm firm);
    void updateFirm(Firm firm);
    void deleteFirmById(Integer id);
    int importByExcelSheet(MultipartFile excelSheet) throws Exception;
}
