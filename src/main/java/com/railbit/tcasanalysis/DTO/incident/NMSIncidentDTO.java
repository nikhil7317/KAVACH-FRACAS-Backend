package com.railbit.tcasanalysis.DTO.incident;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.railbit.tcasanalysis.util.serializers.EpochToLocalDateTimeDeserializer;
import com.railbit.tcasanalysis.util.serializers.LocalDateTimeToEpochSerializer;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NMSIncidentDTO {

    private String incidentTicket;
    private String category;
    private Long locoId;
    private String locoNo;
    private String locoOem;
    private String locoType;
    private String locoVersion;
    private String zoneCode;
    private Integer divId;
    private String divisionCode;
    private Long stnId;
    private String stnCode;
    private String stnOem;
    private Date tripDate;
    private String incidentTime;
    private String briefDescription;

}
