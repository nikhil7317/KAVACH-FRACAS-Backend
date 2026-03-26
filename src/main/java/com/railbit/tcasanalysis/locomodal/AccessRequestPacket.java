package com.railbit.tcasanalysis.locomodal;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.Date;
 
/**
 * C.4.6 Access Request Packet (PKT_TYPE=1101)
 * Flat structure — no sub-packets, no MAC_CODE. Total 232 bits.
 * PKT_LENGTH is 7 bits.
 */
@Entity
@Table(name = "access_request_packet")
@Setter @Getter
public class AccessRequestPacket {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @JsonBackReference("loco-access")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loco_packet_id", nullable = false)
    private LocoPacket locoPacket;
 
    @Column(name = "raw_hex", columnDefinition = "TEXT")
    private String rawHex;
 
    @Column(name = "pkt_type_code")
    private Integer pktTypeCode;
 
    @Column(name = "pkt_type_str")
    private String pktTypeStr;
 
    @Column(name = "pkt_length")
    private Integer pktLength;
 
    @Column(name = "frame_num")
    private Integer frameNum;
 
    @Column(name = "frame_time")
    private String frameTime;
 
    @Column(name = "source_loco_id")
    private Integer sourceLocoId;
 
    @Column(name = "source_loco_version")
    private Integer sourceLocoVersion;
 
    @Column(name = "source_loco_version_str")
    private String sourceLocoVersionStr;
 
    @Column(name = "abs_loco_loc")
    private Integer absLocoLoc;
 
    @Column(name = "train_length")
    private Integer trainLength;
 
    @Column(name = "train_speed")
    private Integer trainSpeed;
 
    @Column(name = "movement_dir")
    private Integer movementDir;
 
    @Column(name = "movement_dir_str")
    private String movementDirStr;
 
    @Column(name = "emergency_status")
    private Integer emergencyStatus;
 
    @Column(name = "emergency_status_str")
    private String emergencyStatusStr;
 
    @Column(name = "loco_mode")
    private Integer locoMode;
 
    @Column(name = "loco_mode_str")
    private String locoModeStr;
 
    @Column(name = "approaching_stn_id")
    private Integer approachingStnId;
 
    @Column(name = "last_rfid_tag")
    private Integer lastRfidTag;
 
    @Column(name = "tin")
    private Integer tin;
 
    @Column(name = "longitude_raw")
    private Integer longitudeRaw;
 
    @Column(name = "longitude_deg")
    private String longitudeDeg;
 
    @Column(name = "latitude_raw")
    private Integer latitudeRaw;
 
    @Column(name = "latitude_deg")
    private String latitudeDeg;
 
    @Column(name = "loco_rnd_num")
    private Integer locoRndNum;
 
    @Column(name = "pkt_crc")
    private String pktCrc;
 
    @Column(name = "pkt_crc_valid")
    private Boolean pktCrcValid;
 
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt;
 
    @PrePersist
    protected void onCreate() { createdAt = new Date(); }
}
 