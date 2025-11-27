package com.railbit.tcasanalysis.DTO.reports;

import com.railbit.tcasanalysis.DTO.MonthWiseData;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AvgIncidentClosingTimeReportDTO {

    List<MonthWiseData> monthWiseDataList;

}
