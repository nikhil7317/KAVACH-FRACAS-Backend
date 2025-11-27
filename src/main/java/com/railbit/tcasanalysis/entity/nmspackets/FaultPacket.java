package com.railbit.tcasanalysis.entity.nmspackets;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.railbit.tcasanalysis.entity.Station;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "faultpacket")
public class FaultPacket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonProperty("date")
    private LocalDate date;

    @JsonProperty("total_fault_code")
    private int totalFaultCode;

    @JsonProperty("tcas_subsys_type")
    private int tcasSubsysType;

    private long crc;

    @Column(unique = true)
    @JsonProperty("hex_value")
    private String hexValue;

    @JsonProperty("start_frame")
    private int startFrame;

    @JsonProperty("msg_type")
    private int msgType;

    @JsonFormat
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    @JsonProperty("time")
    private LocalTime time;

    @JsonProperty("msg_length")
    @Column(columnDefinition = "INTEGER DEFAULT 0")
    private int msgLength;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="tcasSubsysId",referencedColumnName = "id")
    @JsonIgnore
    private Station tcasSubsysId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="faultCode",referencedColumnName = "id")
    @JsonIgnore
    private FaultCode faultCode;

    private String zoneId;
    private String divId;
    private String kv;
    private String nmsId;
    private String oemId;

}
