package com.railbit.tcasanalysis.DTO;

import lombok.Data;

@Data
public class ReportData {
    private String dataSourceName;
    private Double currentValue;
    private Double minValue;
    private Double maxValue;
    private Double avgValue;
    private String unit;
}

