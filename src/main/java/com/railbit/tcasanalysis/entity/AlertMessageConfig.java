package com.railbit.tcasanalysis.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

/**
 * Configuration table that maps each alert_category to its known alert_messages.
 * When 'enabled' is false, all dashboard queries will EXCLUDE rows
 * in kavach_alert that match both the category AND the message.
 */
@Entity
@Table(
        name = "alert_message_config",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_category_message",
                columnNames = {"alert_category", "alert_message"}
        )
)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertMessageConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Matches KavachAlert.alertCategory (e.g. "BRAKE", "MODE_CHANGE").
     * Not a FK — kept as plain string to stay loosely coupled.
     */
    @Column(name = "alert_category", nullable = false)
    private String alertCategory;

    /**
     * Matches KavachAlert.alertMessage (e.g. "Emergency Brake by Kavach").
     */
    @Column(name = "alert_message", nullable = false)
    private String alertMessage;

    /**
     * When false → this (category, message) pair is excluded from all
     * dashboard / analysis queries.
     */
    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private Boolean enabled = true;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
        if (enabled == null) enabled = true;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
}