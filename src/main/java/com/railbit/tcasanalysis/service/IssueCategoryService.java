package com.railbit.tcasanalysis.service;



import com.railbit.tcasanalysis.entity.IssueCategory;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IssueCategoryService {
    IssueCategory getIssueCategoryByName(String name);
    IssueCategory getIssueCategoryById(Integer id);
    List<IssueCategory> getAllIssueCategory();
    int postIssueCategory(IssueCategory issueCategory);
    void updateIssueCategory(IssueCategory issueCategory);
    void deleteIssueCategoryById(Integer id);
    int importByExcelSheet(MultipartFile excelSheet) throws Exception;
}
