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
 * automatically creates a KavachAlertDetails ticket assigned to railwayUserId or oemUserId.
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
     * FK to the Railway user who should receive auto-assigned tickets.
     * Can be null if only OEM user is set.
     */
    @Column(name = "railway_user_id")
    private Long railwayUserId;

    /**
     * FK to the OEM user who should receive auto-assigned tickets.
     * Can be null if only Railway user is set.
     */
    @Column(name = "oem_user_id")
    private Long oemUserId;

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

    /**
     * Returns the first available user ID for assignment.
     * Priority: Railway user first, then OEM user.
     * Returns null if neither is set.
     */
    @Transient
    public Long getAssignedUserId() {
        if (railwayUserId != null) return railwayUserId;
        return oemUserId;
    }

    /**
     * Returns true if at least one user (Railway or OEM) is configured.
     */
    @Transient
    public boolean hasAssignedUser() {
        return railwayUserId != null || oemUserId != null;
    }
}