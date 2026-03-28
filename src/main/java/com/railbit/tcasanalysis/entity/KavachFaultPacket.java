package com.railbit.tcasanalysis.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * G.4.9 KAVACH Fault Message to NMS Server (MSG_TYPE=0x19)
 *
 * SOF can be 0xAAAA (E1/Network) or 0xBBBB (GPRS).
 * KAVACH Subsystem ID is 3 bytes (unlike 2 bytes in other packets).
 */
@Entity
@Table(name = "kavach_fault_packet")
@Setter @Getter
public class KavachFaultPacket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hex_data", columnDefinition = "TEXT", nullable = false)
    private String hexData;

    @Column(name = "src_ip")
    private String srcIp;

    @Column(name = "src_port")
    private String srcPort;

    @Column(name = "sof")
    private String sof;

    @Column(name = "msg_type")
    private String msgType;

    @Column(name = "msg_length")
    private Integer msgLength;

    @Column(name = "msg_sequence")
    private Integer msgSequence;

    // 3 bytes — different from station/loco (2 bytes)
    @Column(name = "kavach_subsystem_id")
    private Integer kavachSubsystemId;

    @Column(name = "nms_system_id")
    private Integer nmsSystemId;

    @Column(name = "system_version")
    private Integer systemVersion;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "at_date")
    private Date atDate;

    // 0x11=Stationary, 0x22=Onboard, 0x33=TSRMS
    @Column(name = "subsystem_type")
    private Integer subsystemType;

    @Column(name = "subsystem_type_str")
    private String subsystemTypeStr;

    @Column(name = "total_fault_codes")
    private Integer totalFaultCodes;

    @Column(name = "crc_hex")
    private String crcHex;

    @Column(name = "crc_valid")
    private Boolean crcValid;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @JsonManagedReference("fault-entry")
    @OneToMany(mappedBy = "faultPacket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("entryIndex ASC")
    private List<KavachFaultEntry> faultEntries = new ArrayList<>();

    public void addFaultEntry(KavachFaultEntry entry) {
        faultEntries.add(entry);
        entry.setFaultPacket(this);
        entry.setEntryIndex(faultEntries.size());
    }

    @PrePersist
    protected void onCreate() { createdAt = new Date(); }
}
