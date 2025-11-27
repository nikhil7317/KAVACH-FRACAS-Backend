package com.railbit.tcasanalysis.DTO.performanceReport;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OverallPerformanceReportDTO {

    private String month;
    private Integer totalTrips;
    private Integer emuTrips;
//    private Integer nonEmuTrips;
    private Integer undesirableBraking;
    private Integer desirableBraking;
    private Integer modeChange;

}
