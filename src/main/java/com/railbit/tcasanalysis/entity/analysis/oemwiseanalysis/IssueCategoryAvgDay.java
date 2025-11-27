package com.railbit.tcasanalysis.entity.analysis.oemwiseanalysis;

import com.railbit.tcasanalysis.entity.Firm;
import com.railbit.tcasanalysis.entity.IssueCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueCategoryAvgDay {
    IssueCategory issueCategory;
    int avgDays;
}
