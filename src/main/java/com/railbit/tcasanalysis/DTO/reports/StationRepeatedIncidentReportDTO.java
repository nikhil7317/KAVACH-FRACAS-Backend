package com.railbit.tcasanalysis.DTO.reports;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StationRepeatedIncidentReportDTO {

    String zoneCode;
    String divisionCode;
    Integer divisionId;
    String stnCode;
    Integer stnId;
    String stcasOem;
//    String locoNo;
//    String ltcasOem;
    String issueCategory;
    String rootCause;
    String subCategory;
    Long cases;

}
