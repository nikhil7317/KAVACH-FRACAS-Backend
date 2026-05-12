package com.railbit.tcasanalysis.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class LocoFailureListDTO {

    private Long id;
    private Integer locoId;
    private LocalDateTime incidentCreatedAt;
    private String ticketNo;
    private String ticketStatus;
    private String severity;
    private Boolean isLocoFailureNotifiedApp;
    private Boolean isLocoFailureNotifiedWeb;
    private UserSummaryDTO createdUser;
    private UserSummaryDTO assignedTo;

    @Getter @Setter
    public static class UserSummaryDTO {
        private Long id;
        private String name;
        private DesignationSummaryDTO designation;
    }

    @Getter @Setter
    public static class DesignationSummaryDTO {
        private Long id;
        private String name;
        private String title;
    }
}