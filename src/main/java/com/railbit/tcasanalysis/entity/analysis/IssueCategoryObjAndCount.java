package com.railbit.tcasanalysis.entity.analysis;

import com.railbit.tcasanalysis.entity.IssueCategory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueCategoryObjAndCount {
    private IssueCategory issueCategory;
    private int count;
    private String colorCode;
}
