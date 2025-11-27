package com.railbit.tcasanalysis.DTO.reports;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CausesWiseRepeatedIncidentsReportDTO {

    String issueCategory;
    String possibleRootCause;
    String rootCauseSubCategory;
    Long count;
    String station;
    String locoId;

}
