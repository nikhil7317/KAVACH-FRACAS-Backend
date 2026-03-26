package com.railbit.tcasanalysis.entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "turnout_entry")
@Setter
@Getter
public class TurnoutEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference("turnout-entry")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private TurnoutSpeedProfile profile;

    @Column(name = "entry_index")
    private Integer entryIndex;

    @Column(name = "to_speed_code")
    private Integer toSpeedCode;

    @Column(name = "to_speed_str")
    private String toSpeedStr;

    @Column(name = "diff_dist_to")
    private Integer diffDistTo;

    @Column(name = "to_speed_rel_dist")
    private Integer toSpeedRelDist;
}
