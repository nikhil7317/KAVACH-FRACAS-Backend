package com.railbit.tcasanalysis.locomodal;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
 
/**
 * C.4.3 Onboard to Station Regular Packet (PKT_TYPE=1010)
 * Flat structure — no sub-packets. Total 230 bits.
 * PKT_LENGTH is 7 bits (not 10 like station).
 */
@Entity
@Table(name = "onboard_regular_packet")
@Setter @Getter
public class OnboardRegularPacket {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @JsonBackReference("loco-onboard")
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
 
    @Column(name = "l_doubt_over")
    private Integer lDoubtOver;
 
    @Column(name = "l_doubt_under")
    private Integer lDoubtUnder;
 
    @Column(name = "train_int")
    private Integer trainInt;
 
    @Column(name = "train_int_str")
    private String trainIntStr;
 
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
 
    @Column(name = "last_rfid_tag")
    private Integer lastRfidTag;
 
    @Column(name = "tag_dup")
    private Integer tagDup;
 
    @Column(name = "tag_dup_str")
    private String tagDupStr;
 
    @Column(name = "tag_link_info")
    private Integer tagLinkInfo;
 
    @Column(name = "tag_link_info_str")
    private String tagLinkInfoStr;
 
    @Column(name = "tin")
    private Integer tin;
 
    @Column(name = "brake_applied")
    private Integer brakeApplied;
 
    @Column(name = "brake_applied_str")
    private String brakeAppliedStr;
 
    @Column(name = "new_ma_reply")
    private Integer newMaReply;
 
    @Column(name = "new_ma_reply_str")
    private String newMaReplyStr;
 
    @Column(name = "last_ref_profile_num")
    private Integer lastRefProfileNum;
 
    @Column(name = "sig_ov")
    private Integer sigOv;
 
    @Column(name = "sig_ov_str")
    private String sigOvStr;
 
    @Column(name = "info_ack")
    private Integer infoAck;
 
    @Column(name = "info_ack_str")
    private String infoAckStr;
 
    @Column(name = "spare")
    private Integer spare;
 
    @Column(name = "loco_health_status")
    private Integer locoHealthStatus;
 
    @Column(name = "mac_code")
    private String macCode;
 
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
 