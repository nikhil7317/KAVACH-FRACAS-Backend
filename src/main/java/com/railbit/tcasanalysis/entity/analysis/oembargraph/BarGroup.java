package com.railbit.tcasanalysis.entity.analysis.oembargraph;

import com.railbit.tcasanalysis.entity.analysis.StringAndCount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BarGroup {
    String title;
    List<StringAndCount> barDataList;
}
