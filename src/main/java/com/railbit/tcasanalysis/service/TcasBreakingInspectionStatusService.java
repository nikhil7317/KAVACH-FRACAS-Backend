package com.railbit.tcasanalysis.service;

import com.railbit.tcasanalysis.entity.TcasBreakingInspection;
import com.railbit.tcasanalysis.entity.TcasBreakingInspectionStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TcasBreakingInspectionStatusService {
    TcasBreakingInspectionStatus addTcasBreakingInspectionStatus(TcasBreakingInspectionStatus tcasBreakingInspectionStatus,
                                                                 List<MultipartFile> fileList) throws Exception;
    TcasBreakingInspectionStatus getTcasBreakingInspectionStatusById(Long id);
    List<TcasBreakingInspectionStatus> getAllTcasBreakingInspectionStatusByInspection(Long id);
    List<TcasBreakingInspectionStatus> getAllTcasBreakingInspectionStatus();
    TcasBreakingInspectionStatus postTcasBreakingInspectionStatus(TcasBreakingInspectionStatus tcasBreakingInspectionStatus);
    TcasBreakingInspectionStatus addIncidentStatus(TcasBreakingInspectionStatus tcasBreakingInspectionStatus);
    void updateTcasBreakingInspectionStatus(TcasBreakingInspectionStatus tcasBreakingInspectionStatus);
    void deleteTcasBreakingInspectionStatusById(Long id);
    int importByExcelSheet(MultipartFile excelSheet) throws Exception;
    List<String> findRemarkTypesByRoleNameAndInspectionId(String roleName,Long inspectionId);
    TcasBreakingInspection findAndSetLastAssignedUserStatusInIncidentListInIncident(TcasBreakingInspection inspection);
}
