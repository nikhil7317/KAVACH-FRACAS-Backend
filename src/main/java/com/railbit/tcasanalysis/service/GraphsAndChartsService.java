package com.railbit.tcasanalysis.service;



import com.google.gson.JsonArray;
import com.railbit.tcasanalysis.DTO.MonthWiseData;
import com.railbit.tcasanalysis.entity.analysis.oemwiseanalysis.FirmAndAvgDay;
import com.railbit.tcasanalysis.entity.analysis.SingleIncidentAnalysisChartData;
import com.railbit.tcasanalysis.entity.analysis.StringAndCount;
import com.railbit.tcasanalysis.entity.analysis.oembargraph.BarGroupListContainer;
import com.railbit.tcasanalysis.entity.analysis.oemwiseanalysis.FirmIssueCategoryAvgDay;
import com.railbit.tcasanalysis.entity.analysis.oemwiseanalysis.FirmsAndStatusCounts;
import com.railbit.tcasanalysis.entity.analysis.yearlygraph.FirmMonthStatusCount;
import com.railbit.tcasanalysis.entity.analysis.yearlygraph.YearlyGraphData;
import org.springframework.boot.configurationprocessor.json.JSONArray;

import java.time.LocalDateTime;
import java.util.List;

public interface GraphsAndChartsService {
    List<MonthWiseData> getIssueWiseYearlyGraphData(Long zoneId, Long divisionId);
    List<SingleIncidentAnalysisChartData> getCountsByFirmAndDate(LocalDateTime fromDate, LocalDateTime toDate, int firmId);
    List<BarGroupListContainer> getFirmWiseBarGraphData(LocalDateTime fromDate, LocalDateTime toDate);
    List<SingleIncidentAnalysisChartData> getIncidentsOverViewDatewise(Long zoneId, Long divisionId, LocalDateTime fromDate, LocalDateTime toDate);
    List<StringAndCount> getDashboardCounts();
    List<FirmAndAvgDay> getAvgDaysTakenToCloseByFirm();
    List<FirmIssueCategoryAvgDay> getAvgDaysTakenToCloseByFirmAndIssueCategory();
    List<FirmAndAvgDay> getAvgDaysTakenToCloseByFirmAndModeChange();
    List<FirmAndAvgDay> getAvgDaysTakenToCloseByFirmAndUndesirableBreaking();
    List<FirmsAndStatusCounts> getCountsByFirmsAndStatus();
    List<FirmMonthStatusCount> getMonthlyInspectionCountsByFirmGroupByStatus(int year,int firmId);
    List<FirmsAndStatusCounts> getCountsByFirmAndIssueCategory(int issueCategoryId);
}
