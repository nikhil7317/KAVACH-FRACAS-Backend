package com.railbit.tcasanalysis.entity.cmsabn;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.railbit.tcasanalysis.entity.*;
import com.railbit.tcasanalysis.entity.loco.Loco;
import com.railbit.tcasanalysis.util.serializers.EpochToLocalDateTimeDeserializer;
import com.railbit.tcasanalysis.util.serializers.LocalDateTimeToEpochSerializer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Entity(name = "cmsabn")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CMSAbn implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Primary Key Id")
    private Long id;
    private String abnId;
    private String abnType;
    private String subHead;

    @ManyToOne
    private Division division;

    @ManyToOne
    private Station fromStation;

    @ManyToOne
    private Station toStation;

    @ManyToOne
    private Station faultyStation;

    @JsonSerialize(using = LocalDateTimeToEpochSerializer.class)
    @JsonDeserialize(using = EpochToLocalDateTimeDeserializer.class)
    private LocalDateTime abnDateTime;

    private String fromKm;
    private String toKm;

    @ManyToOne
    private Loco loco;

    private String train;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String initialClosingRemark;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String finalClosingRemark;

    @JsonSerialize(using = LocalDateTimeToEpochSerializer.class)
    @JsonDeserialize(using = EpochToLocalDateTimeDeserializer.class)
    private LocalDateTime initialClosingDateTime;

    @JsonSerialize(using = LocalDateTimeToEpochSerializer.class)
    @JsonDeserialize(using = EpochToLocalDateTimeDeserializer.class)
    private LocalDateTime finalClosingDateTime;

    private String ticketNo;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String oemRemark;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String analysis;

    @ManyToOne
    private Tcas tcas;

    private String assignTo;

    // Many-to-Many relationship with Firm
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "cmsabn_ticket_firm",
            joinColumns = @JoinColumn(name = "cmsabn_ticket_id"),
            inverseJoinColumns = @JoinColumn(name = "firm_id")
    )
    private Set<Firm> assignedFirms;

    private String ltcasStcas;

    @ManyToOne
    private PossibleRootCause possibleRootCause;

    private String status;

    @JsonSerialize(using = LocalDateTimeToEpochSerializer.class)
    @JsonDeserialize(using = EpochToLocalDateTimeDeserializer.class)
    private LocalDateTime closureDateTime;

    private String cliId;
    private String cliName;
    private String cliDesig;
    private String filledBy;
    @JsonSerialize(using = LocalDateTimeToEpochSerializer.class)
    @JsonDeserialize(using = EpochToLocalDateTimeDeserializer.class)
    private LocalDateTime smstoLp;
    private String frwdByLoc;
    private String frwdByUser;
    private String frwdByAuth;
    @JsonSerialize(using = LocalDateTimeToEpochSerializer.class)
    @JsonDeserialize(using = EpochToLocalDateTimeDeserializer.class)
    private LocalDateTime frwdDateTime;
    private String remStts;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String fwrdRemarks;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String appRemarks;
    @JsonSerialize(using = LocalDateTimeToEpochSerializer.class)
    @JsonDeserialize(using = EpochToLocalDateTimeDeserializer.class)
    private LocalDateTime appRmrkDateTime;
    @JsonSerialize(using = LocalDateTimeToEpochSerializer.class)
    @JsonDeserialize(using = EpochToLocalDateTimeDeserializer.class)
    private LocalDateTime rprtDateTime;

    @CreationTimestamp
    @JsonSerialize(using = LocalDateTimeToEpochSerializer.class)
    @JsonDeserialize(using = EpochToLocalDateTimeDeserializer.class)
    private LocalDateTime createdDateTime=LocalDateTime.now();

    @UpdateTimestamp
    @JsonSerialize(using = LocalDateTimeToEpochSerializer.class)
    @JsonDeserialize(using = EpochToLocalDateTimeDeserializer.class)
    private LocalDateTime updatedDateTime;
}
