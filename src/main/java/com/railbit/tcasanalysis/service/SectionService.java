package com.railbit.tcasanalysis.service;



import com.railbit.tcasanalysis.entity.Section;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SectionService {
    Section getSectionByName(String name);
    Section getSectionById(Integer id);
    List<Section> getAllSection();
    List<Section> getAllSectionByDivisionId(Integer divisionId);
    Section postSection(Section section);
    void updateSection(Section section);
    void deleteSectionById(Integer id);
    int importByExcelSheet(MultipartFile excelSheet) throws Exception;
}
