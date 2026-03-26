package com.railbit.tcasanalysis.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.railbit.tcasanalysis.entity.StationaryPacket;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "station_inner_packet")
@Setter
@Getter
public class StationInnerPacket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference("outer-inner")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stationary_packet_id", nullable = false)
    private StationaryPacket stationaryPacket;

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

    @Column(name = "source_stn_id")
    private Integer sourceStationId;

    @Column(name = "source_version_code")
    private Integer sourceVersionCode;

    @Column(name = "source_version_str")
    private String sourceVersionStr;

    @Column(name = "dest_loco_id")
    private Integer destLocoId;

    @Column(name = "ref_prof_id")
    private Integer refProfId;

    @Column(name = "ref_prof_str")
    private String refProfStr;

    @Column(name = "last_ref_rfid")
    private Integer lastRefRfid;

    @Column(name = "dist_pkt_start")
    private Integer distPktStart;

    @Column(name = "pkt_dir_code")
    private Integer pktDirCode;

    @Column(name = "pkt_dir_str")
    private String pktDirStr;

    @Column(name = "loco_mac_code")
    private String locoMacCode;

    @Column(name = "pkt_crc")
    private String pktCrc;

    @Column(name = "pkt_crc_valid")
    private Boolean pktCrcValid;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @JsonManagedReference("inner-subpkt")
    @OneToMany(mappedBy = "innerPacket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("subPktIndex ASC")
    private List<SubPacket> subPackets = new ArrayList<>();

    public void addSubPacket(SubPacket subPacket) {
        subPackets.add(subPacket);
        subPacket.setInnerPacket(this);
        subPacket.setSubPktIndex(subPackets.size());
    }

    @PrePersist
    protected void onCreate() { createdAt = new Date(); }
}
