package com.railbit.tcasanalysis.entity.analysis;

import com.railbit.tcasanalysis.entity.IssueCategory;
import com.railbit.tcasanalysis.entity.Tcas;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TcasObjAndCount {
    private Tcas tcas;
    private int count;
    private String colorCode;
}
