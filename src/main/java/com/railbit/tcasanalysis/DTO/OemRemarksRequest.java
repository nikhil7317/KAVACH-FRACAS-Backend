package com.railbit.tcasanalysis.DTO;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class OemRemarksRequest {

    private KavachAlertDetailsRef kavachAlertDetails;
    private CreatedUserRef createdUser;
    private LocalDateTime incidentCreatedAt;
    private String ticketRemarks;

    @Getter
    @Setter
    public static class KavachAlertDetailsRef {
        private Long id;
    }

    @Getter
    @Setter
    public static class CreatedUserRef {
        private Long id;
    }
}