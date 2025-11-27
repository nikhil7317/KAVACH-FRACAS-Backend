package com.railbit.tcasanalysis.DTO;

import com.railbit.tcasanalysis.entity.analysis.BarGraphDataSet;
import com.railbit.tcasanalysis.entity.analysis.StringAndCount;
import com.railbit.tcasanalysis.entity.analysis.StringAndFloatCount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthWiseData {
    String month;
    List<BarGraphDataSet> barGraphDataSetList;
}
