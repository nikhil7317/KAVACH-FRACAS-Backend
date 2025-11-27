package com.railbit.tcasanalysis.entity.analysis.oembargraph;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BarGroupListContainer {
    String title;
    //Month calculated in the list by its index ex: 0=JAN,1=Feb
    List<BarGroup> barGroupList;
}
