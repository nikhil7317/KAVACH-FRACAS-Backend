package com.railbit.tcasanalysis.locomodal;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
 
@Entity
@Table(name = "loco_packet")
@Setter @Getter
public class LocoPacket {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(name = "hex_data", columnDefinition = "TEXT", nullable = false)
    private String hexData;
 
    @Column(name = "src_ip")
    private String srcIp;
 
    @Column(name = "src_port")
    private String srcPort;
 
    @Column(name = "firm")
    private Integer firm;
 
    @Column(name = "pkt_type")
    private String pktType;
 
    @Column(name = "is_parsed")
    private Boolean isParsed;
 
    @Column(name = "msg_type")
    private String msgType;
 
    @Column(name = "msg_length")
    private Integer msgLength;
 
    @Column(name = "msg_sequence")
    private Integer msgSequence;
 
    @Column(name = "kavach_id")
    private Integer kavachId;
 
    @Column(name = "nms_system_id")
    private Integer nmsSystemId;
 
    @Column(name = "system_version")
    private Integer systemVersion;
 
    @Column(name = "system_version_str")
    private String systemVersionStr;
 
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "at_date")
    private Date atDate;
 
    @Column(name = "active_radio")
    private Integer activeRadio;
 
    @Column(name = "radio_status")
    private String radioStatus;
 
    // Loco-specific outer fields (G.4.2 Fields 13-14)
    @Column(name = "ma_section_count")
    private Integer maSectionCount;
 
    @Column(name = "route_id")
    private Integer routeId;
 
    @Column(name = "crc_hex")
    private String crcHex;
 
    @Column(name = "crc_valid")
    private Boolean crcValid;
 
    @Column(name = "loco_id")
    private Integer locoId;
 
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt;
 
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;
 
    @JsonManagedReference("loco-onboard")
    @OneToMany(mappedBy = "locoPacket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("id ASC")
    private List<OnboardRegularPacket> onboardRegularPackets = new ArrayList<>();
 
    @JsonManagedReference("loco-access")
    @OneToMany(mappedBy = "locoPacket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("id ASC")
    private List<AccessRequestPacket> accessRequestPackets = new ArrayList<>();
 
    public void addOnboardRegular(OnboardRegularPacket pkt) {
        onboardRegularPackets.add(pkt);
        pkt.setLocoPacket(this);
    }
 
    public void addAccessRequest(AccessRequestPacket pkt) {
        accessRequestPackets.add(pkt);
        pkt.setLocoPacket(this);
    }
 
    @PrePersist
    protected void onCreate() { createdAt = new Date(); updatedAt = new Date(); }
 
    @PreUpdate
    protected void onUpdate() { updatedAt = new Date(); }
}
 