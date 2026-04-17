package com.railbit.tcasanalysis.DTO;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutoTicketConfigDTO {
    private Long id;
    private List<Integer> selectedCategories;
    private boolean autoTicketEnabled;
    private boolean autoEmailEnabled;
    private Long railwayUserId;      // Replaces userType + assignedToUserId
    private Long oemUserId;          // New field for OEM user
    private Long createdByUserId;
}