package com.railbit.tcasanalysis.entity.analysis;

import java.util.List;

import com.railbit.tcasanalysis.entity.Tcas;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SingleIncidentAnalysisChartData {

    private String chartTitle;
    private int total;
    List<PieChartData> chartDataList;

}
