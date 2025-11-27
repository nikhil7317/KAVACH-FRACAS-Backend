package com.railbit.tcasanalysis.entity.analysis.yearlygraph;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FirmMonthStatusCount {
    String month;
    int open;
    int close;
    int total;
}
