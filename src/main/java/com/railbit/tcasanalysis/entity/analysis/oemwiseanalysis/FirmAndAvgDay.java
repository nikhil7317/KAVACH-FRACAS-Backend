package com.railbit.tcasanalysis.entity.analysis.oemwiseanalysis;

import com.railbit.tcasanalysis.entity.Firm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FirmAndAvgDay {
    Firm firm;
    int avgDays;
}
