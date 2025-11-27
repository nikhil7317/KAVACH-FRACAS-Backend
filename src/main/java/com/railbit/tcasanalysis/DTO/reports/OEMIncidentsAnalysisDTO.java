package com.railbit.tcasanalysis.DTO.reports;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OEMIncidentsAnalysisDTO {
    String locoKavachOem;
    String stationKavachOem;
    Long undesirableBraking;
    Long desirableBraking;
    Long modeChange;
    Long total;
}
