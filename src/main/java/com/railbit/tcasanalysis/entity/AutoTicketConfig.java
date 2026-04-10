package com.railbit.tcasanalysis.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

/**
 * Stores the auto-ticket configuration set by admin.
 * One active config row at a time (isActive = true).
 *
 * When a KavachAlert arrives whose alertCategory is in selectedCategories,
 * the alert processing service reads this config and — if autoTicketEnabled —
 * automatically creates a KavachAlertDetails ticket assigned to assignedToUserId.
 */
@Entity
@Table(name = "auto_ticket_config")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AutoTicketConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Comma-separated alert category names.
     * e.g.  "BRAKE,RFID_ISSUE,LOCO_SOS"
     * Stored as plain text so no extra join table is needed.
     */
    @Column(name = "selected_categories", nullable = false, length = 1000)
    private String selectedCategories;

    /** When true, tickets are created automatically on matching alerts. */
    @Column(name = "auto_ticket_enabled", nullable = false)
    private boolean autoTicketEnabled = false;

    /** When true, an email is sent on auto-ticket creation. */
    @Column(name = "auto_email_enabled", nullable = false)
    private boolean autoEmailEnabled = false;

    /**
     * "RAILWAY" or "OEM" — mirrors the USER_TYPE_OPTIONS on the frontend.
     */
    @Column(name = "user_type", length = 20)
    private String userType;

    /** FK to the user who should receive auto-assigned tickets. */
    @Column(name = "assigned_to_user_id")
    private Long assignedToUserId;

    /** Only one config row should be active at a time. */
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    /** Who created / last saved this config. */
    @Column(name = "created_by_user_id")
    private Long createdByUserId;

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
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }

    // ── Convenience helpers ───────────────────────────────────────────────────

    /** Returns the selectedCategories string as a List<String>. */
    @Transient
    public List<String> getCategoryList() {
        if (selectedCategories == null || selectedCategories.isBlank()) return List.of();
        return List.of(selectedCategories.split(","));
    }

    /** Returns true if the given alertCategory matches this config. */
    @Transient
    public boolean matchesCategory(String alertCategory) {
        if (alertCategory == null) return false;
        return getCategoryList().stream()
                .anyMatch(c -> c.trim().equalsIgnoreCase(alertCategory.trim()));
    }
}