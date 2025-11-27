package com.railbit.tcasanalysis.entity.nmspackets.stationarypackets;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.railbit.tcasanalysis.entity.Station;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "stationary_packet")
public class StationaryPacket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("at_date")
    private Date atDate;

    @Lob
    @Column(columnDefinition = "TEXT",unique = true)
    @JsonProperty("hex_data")
    private String hexData;

    @JsonProperty("pkt_type")
    private String pktType;

    @JsonProperty("tbl_crc")
    private String tblCrc;

    private int firm;

    private String srcIp;
    private String srcPort;
    private String stnCode;

    @Column(columnDefinition = "BOOLEAN default 0")
    @JsonProperty("is_parsed")
    private Boolean isParsed=false;

    @Transient
    private Station station;

}
