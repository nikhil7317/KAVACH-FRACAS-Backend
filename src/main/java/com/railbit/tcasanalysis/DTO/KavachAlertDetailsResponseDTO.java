package com.railbit.tcasanalysis.DTO;

import com.railbit.tcasanalysis.entity.KavachAlert;
import com.railbit.tcasanalysis.entity.User;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class KavachAlertDetailsResponseDTO {
    private Long id;
    private KavachAlert kavachAlert;           // Full entity - no DTO needed as per your requirement
    private User createdUser;                   // Full entity - contains designation
    private String ticketNo;                    // Just the ticket number string
    private List<IncidentTrackDTO> incidentTracks;  // Array of incident track history
}