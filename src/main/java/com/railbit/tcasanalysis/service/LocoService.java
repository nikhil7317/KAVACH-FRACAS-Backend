package com.railbit.tcasanalysis.service;



import com.railbit.tcasanalysis.entity.loco.Loco;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface LocoService {
    Loco getLocoById(Integer id);
    Loco findByLocoNo(String locoNo);
    List<Loco> getAllLoco();
    List<String> getAllLocoNos();
    List<String> getDistinctVersions();
    Page<Loco> getLocos(String searchTerm, Integer locoType, Integer firm, Integer shed,
                        String month, String version, Pageable pageable);

    List<Loco> getAllLocos();
    int postLoco(Loco Loco);
    void updateLoco(Loco Loco);
    void deleteLocoById(Integer id);
    int importByExcelSheet(MultipartFile excelSheet) throws Exception;
}
