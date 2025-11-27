package com.railbit.tcasanalysis.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class NMSStationStatusDTO {
    private Integer stationId;
    private Integer divisionId;
    private Date atDate;
    private String stnCode;
    private String srcPort;
    private String srcIp;

    public NMSStationStatusDTO(Integer stationId, Integer divisionId, Date atDate, String stnCode, String srcPort, String srcIp) {
        this.stationId = stationId;
        this.divisionId = divisionId;
        this.atDate = atDate;
        this.stnCode = stnCode;
        this.srcPort = srcPort;
        this.srcIp = srcIp;
    }
}
