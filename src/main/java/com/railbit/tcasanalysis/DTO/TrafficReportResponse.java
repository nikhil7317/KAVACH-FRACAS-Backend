package com.railbit.tcasanalysis.DTO;


import lombok.Data;
import java.util.List;

@Data
public class TrafficReportResponse {

    private CactiHostDetails cactiHost;       // complete host information
    private Long fromTimestamp;
    private Long toTimestamp;
    private Integer pollingIntervalSeconds;
    private List<ReportData> reports;
}

