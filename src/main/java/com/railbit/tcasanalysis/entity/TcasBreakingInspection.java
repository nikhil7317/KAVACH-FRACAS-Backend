package com.railbit.tcasanalysis.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.railbit.tcasanalysis.entity.loco.Loco;
import com.railbit.tcasanalysis.util.serializers.EpochToLocalDateDeserializer;
import com.railbit.tcasanalysis.util.serializers.EpochToLocalDateTimeDeserializer;
import com.railbit.tcasanalysis.util.serializers.LocalDateEpochSerializer;
import com.railbit.tcasanalysis.util.serializers.LocalDateTimeToEpochSerializer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity(name = "tcasbreakinginspection")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TcasBreakingInspection implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "INTEGER COMMENT '0-Low, 1-Medium, 2-High'")
    private Integer criticalityLevel;

    private String incidentTag;

    @JsonSerialize(using = LocalDateEpochSerializer.class)
    @JsonDeserialize(using = EpochToLocalDateDeserializer.class)
    private LocalDate tripDate;

    @JsonSerialize(using = LocalDateTimeToEpochSerializer.class)
    @JsonDeserialize(using = EpochToLocalDateTimeDeserializer.class)
    private LocalDateTime incidentDateTime;

    private Integer tripNo;

    @ManyToOne
    private Loco loco;
    private String trainNo;
    @ManyToOne
    private Station tcasStationFrom;
    @ManyToOne
    private Station tcasStationTo;
    @ManyToOne
    private Station faultyStation;
    @ManyToOne
    private Division division;
    private String locoTcasPowerStatus;
    @ManyToOne
    Tcas tcas;
    @JsonSerialize(using = LocalDateTimeToEpochSerializer.class)
    @JsonDeserialize(using = EpochToLocalDateTimeDeserializer.class)
    private LocalDateTime tripStartTime;
    @JsonSerialize(using = LocalDateTimeToEpochSerializer.class)
    @JsonDeserialize(using = EpochToLocalDateTimeDeserializer.class)
    private LocalDateTime tripEndTime; 
    private double totalKms;
    private String signalCorrespondence;
    @ManyToOne
    private IssueCategory issueCategory;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String briefDescription;
    @ManyToOne
    private PossibleIssue possibleIssue;
    @ManyToOne
    private PossibleRootCause possibleRootCause;
    @ManyToOne
    private RootCauseSubCategory rootCauseSubCategory;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String rootCauseDescription;
    private String status;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String remark;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String oemRemark;

    @CreationTimestamp
    @JsonSerialize(using = LocalDateTimeToEpochSerializer.class)
    @JsonDeserialize(using = EpochToLocalDateTimeDeserializer.class)
    private LocalDateTime createdDateTime=LocalDateTime.now();

    @ManyToOne
    private User user;

    //ManyToOne relationship pointing back to IncidentTicket
    @ManyToOne
    @JoinColumn(name = "incident_ticket_id")
    @JsonIgnoreProperties("inspections") // This prevents back reference to inspections to avoid the loop
    private IncidentTicket incidentTicket;

    @Transient
    private boolean assignStatus;  // This field will not be persisted to the database

    @Transient
    private String assignedRole;  // This field will not be persisted to the database

    @Transient
    private Long assignedToUserId;  // This field will not be persisted to the database

    @Transient
    private String remarkType;  // This field will not be persisted to the database

}
