package com.railbit.tcasanalysis.entity.analysis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StringAndCount {
    private String name;
    private int count;
    private String colorCode;
}
