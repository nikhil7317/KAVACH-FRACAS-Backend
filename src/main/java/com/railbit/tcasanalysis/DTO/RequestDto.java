package com.railbit.tcasanalysis.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestDto {

    private String fromDate;
    private String toDate;
    private String zoneId;
    private String divisionId;
    private String stationId;
    private String token;
    private ContentDto content;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ContentDto {
        private String issueCategory;
        private String possibleRootCause;
        private String rootCauseSubCategory;
        private int count;
        private String station;
        private String locoNumber;
    }
}
