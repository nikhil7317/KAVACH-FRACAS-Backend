package com.railbit.tcasanalysis.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

/**
 * Real-time alert table — populated during packet decoding whenever
 * critical safety events are detected from Loco packets.
 *
 * Sources:
 *   1. EMERGENCY_STATUS (Onboard Regular + Access Request)
 *      - 001: Unusual Stoppage
 *      - 010: SoS
 *      - 011: Roll Back Detected
 *      - 100: Head On Collision
 *      - 101: Rear End Collision
 *      - 110: Parting SoS
 *
 *   2. Brake_Applied (Onboard Regular only)
 *      - 100: Emergency Brake by Kavach
 *
 *   3. TAG_LINK_INFO (Onboard Regular only)
 *      - 001: Duplicate Tag missing
 *      - 010: Main Tag missing
 *      - 011: Both Tag missing
 *      - 100: Tag position interchanged
 */
@Entity
@Table(name = "kavach_alert")
@Setter @Getter
public class KavachAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // When the event occurred (from packet date/time)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "event_time", nullable = false)
    private Date eventTime;

    // Source loco ID (from inner packet SOURCE_LOCO_ID)
    @Column(name = "loco_id")
    private Integer locoId;

    // Station ID (KAVACH_ID from outer, or APPROACHING_STN_ID from Access Request)
//    @Column(name = "station_id")
//    private Integer stationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "station_id",              // column in kavach_alert table
            referencedColumnName = "tcas_subsys_id", // column in station table
            insertable = false,
            updatable = false
    )
    private Station station;

    @Column(name = "station_id")
    private Integer stationId;

    // Alert category: EMERGENCY, BRAKE, TAG_LINK
    @Column(name = "alert_category", nullable = false)
    private String alertCategory;

    // Raw code from packet
    @Column(name = "alert_code", nullable = false)
    private Integer alertCode;

    // Human-readable message
    @Column(name = "alert_message", nullable = false)
    private String alertMessage;

    // Severity: CRITICAL, WARNING, INFO
    @Column(name = "severity", nullable = false)
    private String severity;

    // Source packet type: ONBOARD_REGULAR, ACCESS_REQUEST
    @Column(name = "source_pkt_type")
    private String sourcePktType;

    // FK to parent loco_packet
    @Column(name = "loco_packet_id")
    private Long locoPacketId;

    // Extra context: speed, mode, location etc.
    @Column(name = "train_speed")
    private Integer trainSpeed;

    @Column(name = "loco_mode")
    private String locoMode;

    @Column(name = "abs_loco_loc")
    private Integer absLocoLoc;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "longitude")
    private String longitude;

    // When this alert was saved to DB
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @Column(name = "last_rfid_tag")
    private Integer lastRfidTag;

    // Add these fields to KavachAlert.java
    @Transient
    private String ticketNo;

    @Transient
    private String ticketStatus;

    @PrePersist
    protected void onCreate() { createdAt = new Date(); }

    @Column(name = "is_notified", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isNotified = false;

    @Column(name = "is_popup_dialog_ack", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isPopupDialogAck = false;
}
