package com.railbit.tcasanalysis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "incident_track")
@Getter
@Setter
public class IncidentTrack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "kavach_alert_details_id", nullable = false)
    private KavachAlertDetails kavachAlertDetails;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_user_id")
    private User createdUser;

    @Column(name = "incident_created_at")
    private LocalDateTime incidentCreatedAt;

    @Column(name = "ticket_status")
    private String ticketStatus;

    @Column(name = "ticket_remarks")
    private String ticketRemarks;
}