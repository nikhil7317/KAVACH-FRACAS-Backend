package com.railbit.tcasanalysis.DTO;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class IncidentTrackDTO {
    private Long id;
    private LocalDateTime incidentCreatedAt;
    private String ticketRemarks;
    private String ticketStatus;
}