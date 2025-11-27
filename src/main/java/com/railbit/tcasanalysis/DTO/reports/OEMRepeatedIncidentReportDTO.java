package com.railbit.tcasanalysis.DTO.reports;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OEMRepeatedIncidentReportDTO {

    String zoneCode;
    String divisionCode;
    String ltcasOem;
    String stcasOem;
    String issueCategory;
    String rootCause;
    Long cases;

}
