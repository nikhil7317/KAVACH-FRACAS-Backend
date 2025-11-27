package com.railbit.tcasanalysis.entity.analysis.yearlygraph;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YearlyGraphData {
    String title;
    //Month calculated in the list by its index ex: 0=JAN,1=Feb
    List<MonthlyGraphData> monthlyGraphDataList;
}
