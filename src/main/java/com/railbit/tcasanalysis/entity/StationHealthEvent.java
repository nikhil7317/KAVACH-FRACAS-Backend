package com.railbit.tcasanalysis.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

 
/**
 * Individual event within Stationary KAVACH Health Message.
 * Event ID determines the field name and data size.
 */
@Entity
@Table(name = "station_health_event")
@Setter @Getter
public class StationHealthEvent {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @JsonBackReference("health-event")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "health_packet_id", nullable = false)
    private StationHealthPacket healthPacket;
 
    @Column(name = "event_index")
    private Integer eventIndex;
 
    @Column(name = "event_id")
    private Integer eventId;
 
    @Column(name = "event_name")
    private String eventName;
 
    @Column(name = "event_data_hex")
    private String eventDataHex;
 
    @Column(name = "event_value")
    private Integer eventValue;
 
    @Column(name = "event_value_str")
    private String eventValueStr;
 
    // For Loco Specific SoS (ID=43) and Train Exit (ID=44)
    @Column(name = "loco_id")
    private Integer locoId;
 
    @Column(name = "event_code")
    private Integer eventCode;
 
    @Column(name = "event_code_str")
    private String eventCodeStr;
 
    // For RFID events (ID=38,39,40)
    @Column(name = "last_rfid_tag")
    private Integer lastRfidTag;
}
 