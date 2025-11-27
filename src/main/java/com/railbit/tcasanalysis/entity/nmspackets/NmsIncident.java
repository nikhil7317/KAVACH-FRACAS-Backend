package com.railbit.tcasanalysis.entity.nmspackets;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Entity
@Table(name = "nms_incident")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class NmsIncident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "incident_ticket", length = 250)
    private String incidentTicket;

    @Column(name = "div_id")
    private Integer divId;

    @Column(name = "loco_id")
    private Long locoId;

    @Column(name = "stn_id")
    private Long stnId;

    @Column(name = "trip_date")
    private Date tripDate;

    @Column(name = "incident_time")
    private String incidentTime;

    @Column(name = "trip_no")
    private Integer tripNo;

    @Column(name = "issue_category", length = 250)
    private String issueCategory;

    @Column(name = "loco_dir")
    private String locoDir;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "nms_id", length = 250, unique = true)
    private String nmsIncidentId;


    public NmsIncident() {

    }
}
