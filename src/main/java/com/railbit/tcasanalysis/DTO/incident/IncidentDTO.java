package com.railbit.tcasanalysis.DTO.incident;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.railbit.tcasanalysis.entity.*;
import com.railbit.tcasanalysis.entity.loco.Loco;
import com.railbit.tcasanalysis.util.serializers.EpochToLocalDateDeserializer;
import com.railbit.tcasanalysis.util.serializers.LocalDateEpochSerializer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@ToString
public class IncidentDTO {
    private Long id;

    private Division division;
    private String tripNo;
    @JsonSerialize(using = LocalDateEpochSerializer.class)
    @JsonDeserialize(using = EpochToLocalDateDeserializer.class)
    private LocalDate tripDate;
    private String trainNo;
    private String locoNo;
    private String locoDir;
    private Station faultyStation;
    private String briefDescription;
    private Tcas tcas;
    private String signalCorrespondence;
   // private IssueCategory issueCategory;
    private String issueCategory;
    private PossibleIssue possibleIssue;
    private PossibleRootCause possibleRootCause;
    private RootCauseSubCategory rootCauseSubCategory;
    private String rootCauseDescription;
    private Integer criticalityLevel;
    private String remark;
    private String incidentTicket;
    private String station;
    private Date date;
    private String time;
    private String absLocation;
    private String toSpeed;
    private String brakeMode;
    private String locoMode;
    private String locoSpeed;
    private String MA;
    private String toDistance;
    private String locoSOS;
    private String issue;

    private Station stationDao;
    private Loco loco;
}
