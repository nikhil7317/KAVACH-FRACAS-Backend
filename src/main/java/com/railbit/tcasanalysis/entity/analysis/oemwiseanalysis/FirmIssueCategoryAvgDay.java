package com.railbit.tcasanalysis.entity.analysis.oemwiseanalysis;

import com.railbit.tcasanalysis.entity.Firm;
import com.railbit.tcasanalysis.entity.IssueCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FirmIssueCategoryAvgDay {
    Firm firm;
    List<IssueCategoryAvgDay> issueCategoryAvgDayList;
}
