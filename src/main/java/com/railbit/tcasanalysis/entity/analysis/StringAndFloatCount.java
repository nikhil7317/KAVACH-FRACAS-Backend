package com.railbit.tcasanalysis.entity.analysis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StringAndFloatCount {
    private String name;
    private String count;
    private String colorCode;
}
