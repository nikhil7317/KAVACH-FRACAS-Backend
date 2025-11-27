package com.railbit.tcasanalysis.DTO.reports;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RootCausesWiseIncidentsReportDTO {

    String zoneCode;
    String divisionCode;
    String possibleRootCause;
    String issueCategory;
    String rootCauseSubCategory;
    Long count;

}
