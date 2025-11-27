package com.railbit.tcasanalysis.entity.analysis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BarGraphDataSet {
    private String name;
    private double value;
    private String label;
    private String colorCode;
}
