package com.railbit.tcasanalysis.service;


import com.railbit.tcasanalysis.entity.PossibleIssue;
import com.railbit.tcasanalysis.entity.loco.Shed;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PossibleIssueService {
    PossibleIssue getPossibleIssueById(Integer id);
    PossibleIssue getPossibleIssueByName(String name);
    PossibleIssue getPossibleIssueByNameAndIssueCategoryId(String name,Integer issueCategoryId);
    List<PossibleIssue> getAllPossibleIssues();
    List<PossibleIssue> getAllPossibleIssueByIssueCategory(Integer issueCategoryId);
    List<PossibleIssue> getAllPossibleIssuesByProjectType(Integer projectTypeId);
    int postPossibleIssue(PossibleIssue possibleIssue);
    void updatePossibleIssue(PossibleIssue possibleIssue);
    void deletePossibleIssueById(Integer id);
    int importByExcelSheet(MultipartFile excelSheet) throws Exception;
}
