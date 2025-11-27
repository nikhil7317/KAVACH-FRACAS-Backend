package com.railbit.tcasanalysis.entity.analysis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PieChartData {

    String name;
    List<StringAndCount> chartDataList;

}
