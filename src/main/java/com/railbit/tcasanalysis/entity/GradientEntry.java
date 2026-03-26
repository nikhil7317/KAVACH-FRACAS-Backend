package com.railbit.tcasanalysis.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "gradient_entry")
@Setter
@Getter
public class GradientEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference("grad-entry")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private GradientProfile profile;

    @Column(name = "entry_index")
    private Integer entryIndex;

    @Column(name = "distance")
    private Integer distance;

    @Column(name = "direction")
    private Integer direction;

    @Column(name = "direction_str")
    private String directionStr;

    @Column(name = "gradient_value")
    private Integer gradientValue;

    @Column(name = "gradient_str")
    private String gradientStr;
}
