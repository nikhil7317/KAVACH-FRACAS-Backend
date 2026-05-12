package com.railbit.tcasanalysis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "loco_failure")
@Getter
@Setter
public class LocoFailure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loco_id", nullable = false)
    private Integer locoId;

    @Column(name = "incident_created_at", nullable = false)
    private LocalDateTime incidentCreatedAt;

    @Column(name = "ticket_no", unique = true, nullable = false)
    private String ticketNo;

    @Column(name = "ticket_status", nullable = false)
    private String ticketStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_user_id")
    private User createdUser;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_to_id")
    private User assignedTo;

    @Column(name = "severity", nullable = false)
    private String severity;

    @Column(name = "is_loco_failure_notified_app", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isLocoFailureNotifiedApp = false;

    @Column(name = "is_loco_failure_notified_web", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isLocoFailureNotifiedWeb = false;

    @PrePersist
    protected void onCreate() {
        this.incidentCreatedAt = LocalDateTime.now();
        if (this.ticketStatus == null) {
            this.ticketStatus = "OPEN";
        }
    }
}