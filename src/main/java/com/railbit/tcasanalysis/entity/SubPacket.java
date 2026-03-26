package com.railbit.tcasanalysis.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "sub_packet")
@Setter
@Getter
public class SubPacket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference("inner-subpkt")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inner_packet_id", nullable = false)
    private StationInnerPacket innerPacket;

    @Column(name = "sub_pkt_index")
    private Integer subPktIndex;

    @Column(name = "sub_pkt_type_code")
    private Integer subPktTypeCode;

    @Column(name = "sub_pkt_type_str")
    private String subPktTypeStr;

    @Column(name = "sub_pkt_length")
    private Integer subPktLength;

    @Column(name = "frame_offset")
    private Integer frameOffset;

    @Column(name = "dest_loco_sos_code")
    private Integer destLocoSosCode;

    @Column(name = "dest_loco_sos_str")
    private String destLocoSosStr;

    @Column(name = "raw_data", columnDefinition = "TEXT")
    private String rawData;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    // Type 0: Movement Authority
    @JsonManagedReference("subpkt-ma")
    @OneToOne(mappedBy = "subPacket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private MovementAuthorityData movementAuthorityData;

    // Type 1: Static Speed Profile
    @JsonManagedReference("subpkt-ssp")
    @OneToOne(mappedBy = "subPacket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private StaticSpeedProfile staticSpeedProfile;

    // Type 2: Gradient Profile
    @JsonManagedReference("subpkt-grad")
    @OneToOne(mappedBy = "subPacket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private GradientProfile gradientProfile;

    // Type 3: LC Gate Profile
    @JsonManagedReference("subpkt-lcgate")
    @OneToOne(mappedBy = "subPacket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private LCGateProfile lcGateProfile;

    // Type 4: Turnout Speed Profile
    @JsonManagedReference("subpkt-turnout")
    @OneToOne(mappedBy = "subPacket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private TurnoutSpeedProfile turnoutSpeedProfile;

    // Type 5: Tag Linking Information
    @JsonManagedReference("subpkt-taglink")
    @OneToOne(mappedBy = "subPacket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private TagLinkingInfo tagLinkingInfo;

    // Type 6: Track Condition Data
    @JsonManagedReference("subpkt-trackcond")
    @OneToOne(mappedBy = "subPacket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private TrackConditionProfile trackConditionProfile;

    // Type 7: Temp Speed Restrictions
    @JsonManagedReference("subpkt-tsr")
    @OneToOne(mappedBy = "subPacket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private TsrProfile tsrProfile;

    @PrePersist
    protected void onCreate() { createdAt = new Date(); }
}
