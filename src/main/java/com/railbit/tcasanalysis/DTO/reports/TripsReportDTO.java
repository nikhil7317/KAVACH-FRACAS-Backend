package com.railbit.tcasanalysis.DTO.reports;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TripsReportDTO {
    String zone;
    String division;
    Integer tripNo;
    String tripDate;
    String locoNo;
    String locoType;
    String withIssue;
    Long totalIncidents;
}
