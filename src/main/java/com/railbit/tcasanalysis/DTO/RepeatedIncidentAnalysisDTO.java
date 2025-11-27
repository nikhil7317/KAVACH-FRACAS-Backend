package com.railbit.tcasanalysis.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RepeatedIncidentAnalysisDTO {
    private Long uniqueOpenTickets;
    private Long uniqueClosedTickets;
    private Long totalUniqueTickets;
    private Long totalTickets;

}