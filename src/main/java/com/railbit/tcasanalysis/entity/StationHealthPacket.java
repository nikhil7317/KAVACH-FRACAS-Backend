package com.railbit.tcasanalysis.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "station_health_packet")
@Setter
@Getter
public class StationHealthPacket {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(name = "hex_data", columnDefinition = "TEXT", nullable = false)
    private String hexData;
 
    @Column(name = "src_ip")
    private String srcIp;
 
    @Column(name = "src_port")
    private String srcPort;
 
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
 
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "at_date")
    private Date atDate;
 
    @Column(name = "event_count")
    private Integer eventCount;
 
    @Column(name = "crc_hex")
    private String crcHex;
 
    @Column(name = "crc_valid")
    private Boolean crcValid;
 
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt;
 
    @JsonManagedReference("health-event")
    @OneToMany(mappedBy = "healthPacket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("eventIndex ASC")
    private List<StationHealthEvent> events = new ArrayList<>();
 
    public void addEvent(StationHealthEvent event) {
        events.add(event);
        event.setHealthPacket(this);
        event.setEventIndex(events.size());
    }
 
    @PrePersist
    protected void onCreate() { createdAt = new Date(); }
}
 