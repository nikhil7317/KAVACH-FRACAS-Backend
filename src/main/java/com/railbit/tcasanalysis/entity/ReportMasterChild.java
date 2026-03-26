package com.railbit.tcasanalysis.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity(name = "report_master_child")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportMasterChild implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "child_name", nullable = false)
    private String childName;

    @Column(name = "pktTypeCode")
    private Integer pktTypeCode;

    @ManyToOne
    @JoinColumn(name = "report_master_id", nullable = false)
    private ReportMaster reportMaster;
}