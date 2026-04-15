package com.railbit.tcasanalysis.DTO;

import com.railbit.tcasanalysis.entity.KavachAlert;
import com.railbit.tcasanalysis.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class KavachAlertDetailsResponseDTO {

    private Long id;

    // Full kavachAlert object so frontend can show all alert details in the view
    private KavachAlert kavachAlert;

    // Who created/assigned this ticket
    private User createdUser;

    // Who the ticket is currently assigned to (with full designation)
    private User assignedTo;

    private String ticketNo;

    // Current ticket status
    private String ticketStatus;

    private LocalDateTime incidentCreatedAt;

    // ✅ Whether OEM has submitted remarks in the oem_remarks table.
    // Frontend uses this directly to decide Close/Re-Assign eligibility.
    // No need to scan incidentTracks[] for this check anymore.
    private boolean oemRemarksSubmitted;

    // All incident track history (newest first)
    private List<IncidentTrackDTO> incidentTracks;
}