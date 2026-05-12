package com.railbit.tcasanalysis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "loco_failure_track")
@Getter
@Setter
public class LocoFailureTrack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loco_failure_id", nullable = false)
    private LocoFailure locoFailure;

    @Column(name = "ticket_remarks", columnDefinition = "TEXT")
    private String ticketRemarks;

    @Column(name = "ticket_status", nullable = false)
    private String ticketStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_user_id")
    private User createdUser;

    @Column(name = "incident_created_at", nullable = false)
    private LocalDateTime incidentCreatedAt;

    @PrePersist
    protected void onCreate() {
        this.incidentCreatedAt = LocalDateTime.now();
    }
}