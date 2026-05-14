package com.railbit.tcasanalysis.DTO;

import com.railbit.tcasanalysis.entity.LocoFailure;
import com.railbit.tcasanalysis.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ShedRemarksRequest {

    private LocoFailure locoFailure;
    private User createdUser;
    private LocalDateTime incidentCreatedAt;
    private String ticketRemarks;
}