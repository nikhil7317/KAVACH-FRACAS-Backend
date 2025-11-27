package com.railbit.tcasanalysis.entity.nmspackets;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertsCount {
    private int spareCount;
    private int headOnCollisionCount;
    private int rearEndCollisionCount;
    private int sosLocoCount;
    private int sosStationCount;
    private int overrideModeCount;
    private int tripModeCount;
}