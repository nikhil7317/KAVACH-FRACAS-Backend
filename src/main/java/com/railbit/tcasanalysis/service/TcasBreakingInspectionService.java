package com.railbit.tcasanalysis.service;


import com.railbit.tcasanalysis.DTO.UserDTO;
import com.railbit.tcasanalysis.entity.*;
import com.railbit.tcasanalysis.entity.analysis.SingleIncidentAnalysisChartData;
import com.railbit.tcasanalysis.entity.analysis.YearlyReportExcelResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface TcasBreakingInspectionService {
    List<TcasBreakingInspection> getAllTcasBreakingInspection();
    List<TcasBreakingInspection> getAllTcasBreakingInspectionByLatestTripDate();
    List<TcasBreakingInspection> getAllIncidentsByLatestTripDate();
    List<TcasBreakingInspection> getAllTcasBreakingInspectionByDivisionId(int divisionID);
    TcasBreakingInspection getTcasBreakingInspectionByIncidentTag(String incidentTag);
    List<TcasBreakingInspection> getAllByMonth(int month);
    List<TcasBreakingInspection> getAllByUserId(Long userId);
    List<TcasBreakingInspection> getAllByAssignedToUser(Long userId);
    List<TcasBreakingInspection> getAllByFirmId(Integer firmId);
    Page<TcasBreakingInspection> getFilteredInspections(LocalDateTime fromDate, LocalDateTime toDate,
                                                        String status, Integer issueCategoryId,
                                                        Integer possibleIssueId, Integer possibleRootCauseId, Integer rootCauseSubCategoryId,
                                                        Integer stationId,
                                                        Integer zoneId,
                                                        Integer divisionId,
                                                        String withIssue,Integer assignFirmId,
                                                        Integer firmId, Integer locoId,Integer locoTypeId,String locoVersion,String condemned, String searchQuery, Pageable pageable);
    List<TcasBreakingInspection> downloadFilteredInspections(LocalDateTime fromDate, LocalDateTime toDate,
                                                        String status, Integer issueCategoryId,
                                                        Integer possibleIssueId, Integer possibleRootCauseId, Integer rootCauseSubCategoryId,
                                                        Integer stationId,
                                                        Integer zoneId,
                                                        Integer divisionId,
                                                        String withIssue,Integer assignFirmId,
                                                        Integer firmId, Integer locoId,Integer locoTypeId,String locoVersion,String condemned, String searchQuery);

    TcasBreakingInspection getTcasBreakingInspectionById(Long id);
    Long addIncident(TcasBreakingInspection tcasBreakingInspection) throws Exception;
    Long addTcasBreakingInspection(TcasBreakingInspection tcasBreakingInspection,List<MultipartFile> fileList) throws Exception;
    void updateTcasBreakingInspection(TcasBreakingInspection tcasBreakingInspection) throws Exception;
    int importByExcelSheet(MultipartFile excelSheet,Long userId) throws Exception;
    List<SingleIncidentAnalysisChartData> getCountsByIssueCategoryForMonthAndYear(int month, int year);

    void closeInspectionIssue(TcasBreakingInspectionStatus tcasBreakingInspectionStatus) throws Exception;

    YearlyReportExcelResponse getYearlyReportExcelResponse(int divisionId);

    void addInspectionStatusWithActions(TcasBreakingInspectionStatus tcasBreakingInspectionStatus,
                                        boolean preventiveAction, boolean correctiveAction) throws Exception;

    List<TcasBreakingInspection> getPendingInspections();
    List<TcasBreakingInspection> getClosedInspections();
    void deleteIncidentById(Long id);
}
