package com.railbit.tcasanalysis.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "static_speed_entry")
@Setter
@Getter
public class StaticSpeedEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference("ssp-entry")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private StaticSpeedProfile profile;

    @Column(name = "entry_index")
    private Integer entryIndex;

    @Column(name = "distance")
    private Integer distance;

    @Column(name = "speed_class")
    private Integer speedClass;

    @Column(name = "speed_class_str")
    private String speedClassStr;

    @Column(name = "universal_speed")
    private Integer universalSpeed;

    @Column(name = "universal_speed_kmph")
    private Integer universalSpeedKmph;

    @Column(name = "category_a_speed")
    private Integer categoryASpeed;

    @Column(name = "category_a_speed_kmph")
    private Integer categoryASpeedKmph;

    @Column(name = "category_b_speed")
    private Integer categoryBSpeed;

    @Column(name = "category_b_speed_kmph")
    private Integer categoryBSpeedKmph;

    @Column(name = "category_c_speed")
    private Integer categoryCSpeed;

    @Column(name = "category_c_speed_kmph")
    private Integer categoryCSpeedKmph;
}
