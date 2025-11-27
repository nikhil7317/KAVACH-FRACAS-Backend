package com.railbit.tcasanalysis.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "incident_ticket_firm")
public class IncidentTicketFirm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_ticket_id")
    private IncidentTicket incidentTicket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "firm_id")
    private Firm firm;

    // getters & setters
}

