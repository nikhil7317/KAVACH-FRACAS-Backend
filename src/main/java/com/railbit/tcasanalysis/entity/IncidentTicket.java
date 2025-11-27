package com.railbit.tcasanalysis.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity(name = "incidentticket")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncidentTicket implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Primary Key Id")
    private Long id;

    @ManyToOne
    private Division division;

    @Column(unique = true, nullable = false)
    private String ticketNo;

    @JsonSerialize(using = LocalDateEpochSerializer.class)
    @JsonDeserialize(using = EpochToLocalDateDeserializer.class)
    private LocalDate tripDate;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String remark;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String oemRemark;

    private String assignTo;

    @ManyToOne
    private User user;

    @JsonSerialize(using = LocalDateEpochSerializer.class)
    @JsonDeserialize(using = EpochToLocalDateDeserializer.class)
    private LocalDate targetDate;

    @JsonSerialize(using = LocalDateTimeToEpochSerializer.class)
    @JsonDeserialize(using = EpochToLocalDateTimeDeserializer.class)
    private LocalDateTime closureDateTime;

    // One-to-Many relationship with Firm
    @OneToMany(mappedBy = "incidentTicket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JsonIgnoreProperties("incidentTicket") // Ignore the back reference
    @JsonIgnore
    private List<TcasBreakingInspection> inspections;

    @OneToMany(mappedBy = "incidentTicket", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<IncidentTicketFirm> incidentTicketFirms;

    // Many-to-Many relationship with Firm
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "incident_ticket_firm",
            joinColumns = @JoinColumn(name = "incident_ticket_id"),
            inverseJoinColumns = @JoinColumn(name = "firm_id")
    )
//    @JsonIgnore@Fetch(FetchMode.LAZY) // Lazy load the assignedFirms
    @JsonIgnoreProperties("incidentTickets") // Ignore the back reference
    private Set<Firm> assignedFirms;

    @Column(columnDefinition = "BOOLEAN default 0")
    @Comment("0 = close , 1 = open")
    private Boolean status=false;

    @CreationTimestamp
    @JsonSerialize(using = LocalDateTimeToEpochSerializer.class)
    @JsonDeserialize(using = EpochToLocalDateTimeDeserializer.class)
    private LocalDateTime createdDateTime=LocalDateTime.now();

    @Transient
    private int incidentCount;

}
