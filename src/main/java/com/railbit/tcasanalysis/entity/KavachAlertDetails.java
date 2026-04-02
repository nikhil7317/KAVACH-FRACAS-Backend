package com.railbit.tcasanalysis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "kavach_alert_details")
@Getter
@Setter
public class KavachAlertDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "kavach_alert_id", referencedColumnName = "id", nullable = false, unique = true)
    private KavachAlert kavachAlert;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_user_id")
    private User createdUser;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_to_id")
    private User assignedTo;

    @Column(name = "incident_created_at")
    private LocalDateTime incidentCreatedAt;

    @Column(name = "ticket_no", unique = true)
    private String ticketNo;

    @Column(name = "ticket_status")
    private String ticketStatus;
}