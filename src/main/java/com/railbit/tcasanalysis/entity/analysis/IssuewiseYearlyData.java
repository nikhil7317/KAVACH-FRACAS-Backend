package com.railbit.tcasanalysis.entity.analysis;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssuewiseYearlyData {

    String issueCategory;
    String issueCategoryPercent;
    List<TcaswiseYearlyData> tcaswiseYearlyDataList;

}
