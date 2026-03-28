package com.railbit.tcasanalysis.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;


/**
 * Individual fault entry within KAVACH Fault Message.
 * Repeated F times (max 10) per packet.
 */
@Entity
@Table(name = "kavach_fault_entry")
@Setter @Getter
public class KavachFaultEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference("fault-entry")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fault_packet_id", nullable = false)
    private KavachFaultPacket faultPacket;

    @Column(name = "entry_index")
    private Integer entryIndex;

    // Field 12: Module ID (1 byte, firm specific)
    @Column(name = "module_id")
    private Integer moduleId;

    // Field 13: Fault Code Type (1 byte)
    // 1 = Fault Code, 2 = Recovery Code
    @Column(name = "fault_code_type")
    private Integer faultCodeType;

    @Column(name = "fault_code_type_str")
    private String faultCodeTypeStr;

    // Field 14: Fault Code (2 bytes, firm specific)
    @Column(name = "fault_code")
    private Integer faultCode;

    @Column(name = "fault_code_hex")
    private String faultCodeHex;
}
