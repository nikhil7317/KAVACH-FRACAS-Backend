package com.railbit.tcasanalysis.service;


import com.railbit.tcasanalysis.entity.RootCauseSubCategory;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RootCauseSubCategoryService {
    RootCauseSubCategory getRootCauseSubCategoryById(Integer id);
    List<RootCauseSubCategory> getRootCauseSubCategoryByName(String name);
    RootCauseSubCategory findByNameAndPossibleRootCauseId(String name, Integer rootCauseId);
    List<RootCauseSubCategory> getAllRootCauseSubCategorys();
    List<RootCauseSubCategory> getAllByRootCause(Integer rootCauseId);
    int postRootCauseSubCategory(RootCauseSubCategory rootCauseSubCategory);
    void updateRootCauseSubCategory(RootCauseSubCategory rootCauseSubCategory);
    void deleteRootCauseSubCategoryById(Integer id);
    int importByExcelSheet(MultipartFile excelSheet) throws Exception;
}
