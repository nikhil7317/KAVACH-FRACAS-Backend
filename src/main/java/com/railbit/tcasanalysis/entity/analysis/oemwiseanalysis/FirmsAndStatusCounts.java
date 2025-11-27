package com.railbit.tcasanalysis.entity.analysis.oemwiseanalysis;

import com.google.gson.JsonArray;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FirmsAndStatusCounts {
    String firm;
    int open;
    int close;
    int total;
}
