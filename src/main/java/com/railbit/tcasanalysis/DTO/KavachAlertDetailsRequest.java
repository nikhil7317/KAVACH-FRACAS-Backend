package com.railbit.tcasanalysis.DTO;



import com.railbit.tcasanalysis.entity.KavachAlertDetails;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class KavachAlertDetailsRequest {
    private Long kavachAlertId;
    private Long createdUserId;
    private Long assignedToId;
    private LocalDateTime incidentCreatedAt;
    private String ticketNo;
    private String ticketStatus;
    private String ticketRemarks;
}