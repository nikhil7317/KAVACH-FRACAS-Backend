package com.railbit.tcasanalysis.DTO;

import com.railbit.tcasanalysis.entity.Designation;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class LocoFailureResponseDTO {

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

    private List<LocoFailureTrackDTO> locoFailureTracks;

    @Getter
    @Setter
    public static class UserSummaryDTO {
        private Long id;
        private String name;
        private DesignationSummaryDTO designation;
    }

    @Getter
    @Setter
    public static class DesignationSummaryDTO {
        private Long id;
        private String name;
        private String title;
    }

    @Getter
    @Setter
    public static class LocoFailureTrackDTO {
        private Long id;
        private String ticketRemarks;
        private String ticketStatus;
        private UserSummaryDTO createdUser;
        private LocalDateTime incidentCreatedAt;
    }
}