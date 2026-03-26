package com.railbit.tcasanalysis.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "track_condition_entry")
@Setter
@Getter
public class TrackConditionEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference("trackcond-entry")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private TrackConditionProfile profile;

    @Column(name = "entry_index")
    private Integer entryIndex;

    @Column(name = "track_cond_type_code")
    private Integer trackCondTypeCode;

    @Column(name = "track_cond_type_str")
    private String trackCondTypeStr;

    @Column(name = "start_distance")
    private Integer startDistance;

    @Column(name = "length")
    private Integer length;
}
 