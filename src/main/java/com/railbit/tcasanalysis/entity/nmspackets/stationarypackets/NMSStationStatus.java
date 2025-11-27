package com.railbit.tcasanalysis.entity.nmspackets.stationarypackets;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.railbit.tcasanalysis.entity.Division;
import com.railbit.tcasanalysis.entity.Station;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import java.util.Date;

@Data
@Entity
@Table(name = "nms_station_status")
public class NMSStationStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("at_date")
    @Column(nullable = true)
    private Date atDate;

    @ManyToOne
    @JoinColumn(name = "stn_id", referencedColumnName = "id")
    private Station station;

    @ManyToOne
    @JoinColumn(name = "div_id", referencedColumnName = "id")
    private Division division;

    private String srcIp;
    private String srcPort;
    private String stnCode;
    private String status;

    public NMSStationStatus(Date atDate, String stnCode, String srcIp, String srcPort, Station station, Division division,String status) {
        this.atDate = atDate;
        this.stnCode = stnCode;
        this.srcIp = srcIp;
        this.srcPort = srcPort;
        this.station = station;
        this.division = division;
        this.status = status;
    }


    // Default constructor
    public NMSStationStatus() {
    }
}