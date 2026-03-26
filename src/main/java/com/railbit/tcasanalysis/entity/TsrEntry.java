package com.railbit.tcasanalysis.entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tsr_entry")
@Setter
@Getter
public class TsrEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference("tsr-entry")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private TsrProfile profile;

    @Column(name = "entry_index")
    private Integer entryIndex;

    @Column(name = "tsr_id")
    private Integer tsrId;

    @Column(name = "distance")
    private Integer distance;

    @Column(name = "length")
    private Integer length;

    @Column(name = "tsr_class")
    private Integer tsrClass;

    @Column(name = "tsr_class_str")
    private String tsrClassStr;

    @Column(name = "universal_speed")
    private Integer universalSpeed;

    @Column(name = "universal_speed_kmph")
    private Integer universalSpeedKmph;

    @Column(name = "class_a_speed")
    private Integer classASpeed;

    @Column(name = "class_a_speed_kmph")
    private Integer classASpeedKmph;

    @Column(name = "class_b_speed")
    private Integer classBSpeed;

    @Column(name = "class_b_speed_kmph")
    private Integer classBSpeedKmph;

    @Column(name = "class_c_speed")
    private Integer classCSpeed;

    @Column(name = "class_c_speed_kmph")
    private Integer classCSpeedKmph;

    @Column(name = "whistle")
    private String whistle;
}
 