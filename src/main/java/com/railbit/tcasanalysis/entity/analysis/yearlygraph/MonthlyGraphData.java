package com.railbit.tcasanalysis.entity.analysis.yearlygraph;

import com.railbit.tcasanalysis.entity.analysis.StringAndCount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyGraphData {
    List<StringAndCount> stringAndCountList;
}
