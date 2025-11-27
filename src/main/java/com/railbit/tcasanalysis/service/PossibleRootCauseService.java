package com.railbit.tcasanalysis.service;


import com.railbit.tcasanalysis.entity.PossibleIssue;
import com.railbit.tcasanalysis.entity.PossibleRootCause;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PossibleRootCauseService {
    PossibleRootCause getPossibleRootCauseById(Integer id);
    PossibleRootCause getPossibleRootCauseByName(String name);
    List<PossibleRootCause> getAllPossibleRootCauses();
    List<PossibleRootCause> getAllPossibleRootCausesByProjectType(Integer projectTypeId);
    int postPossibleRootCause(PossibleRootCause possibleRootCause);
    void updatePossibleRootCause(PossibleRootCause possibleRootCause);
    void deletePossibleRootCauseById(Integer id);
    int importByExcelSheet(MultipartFile excelSheet) throws Exception;
}
