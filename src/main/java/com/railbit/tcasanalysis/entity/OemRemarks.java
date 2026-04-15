package com.railbit.tcasanalysis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "oem_remarks")
@Getter
@Setter
public class OemRemarks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "kavach_alert_details_id", nullable = false)
    private KavachAlertDetails kavachAlertDetails;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_user_id", nullable = false)
    private User createdUser;

    @Column(name = "ticket_remarks", nullable = false, columnDefinition = "TEXT")
    private String ticketRemarks;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}