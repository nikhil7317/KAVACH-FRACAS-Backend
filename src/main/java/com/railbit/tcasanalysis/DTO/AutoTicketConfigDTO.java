package com.railbit.tcasanalysis.DTO;


import lombok.*;

import java.util.List;

/**
 * Used for both the GET response (to seed the frontend form)
 * and the POST/PUT request body (to save the config).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutoTicketConfigDTO {

    private Long id;

    /** Multi-select categories from frontend, e.g. ["BRAKE","RFID_ISSUE"] */
    private List<Integer> selectedCategories;

    private boolean autoTicketEnabled;
    private boolean autoEmailEnabled;

    /** "RAILWAY" or "OEM" */
    private String userType;

    private Long assignedToUserId;

    private Long createdByUserId;
}
