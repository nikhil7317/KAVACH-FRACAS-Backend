package com.railbit.tcasanalysis.DTO.reports;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LocoRepeatedIncidentReportDTO {

    String zoneCode;
    String divisionCode;
    String locoId;
    Integer id;
    String ltcasOem;
    String issueCategory;
    String rootCause;
    String subCategory;
    Long cases;

}
