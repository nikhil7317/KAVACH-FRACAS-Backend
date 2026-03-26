package com.railbit.tcasanalysis.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lc_gate_entry")
@Setter
@Getter
public class LCGateEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference("lcgate-entry")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private LCGateProfile profile;

    @Column(name = "entry_index")
    private Integer entryIndex;

    @Column(name = "distance")
    private Integer distance;

    @Column(name = "lc_id_numeric")
    private Integer lcIdNumeric;

    @Column(name = "lc_id_str")
    private String lcIdStr;

    @Column(name = "alpha_suffix")
    private String alphaSuffix;

    @Column(name = "manning_type")
    private String manningType;

    @Column(name = "lc_class")
    private String lcClass;

    @Column(name = "auto_whistling_enabled")
    private Boolean autoWhistlingEnabled;

    @Column(name = "auto_whistling_type")
    private String autoWhistlingType;
}
