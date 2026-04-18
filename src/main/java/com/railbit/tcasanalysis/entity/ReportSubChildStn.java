package com.railbit.tcasanalysis.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity(name = "report_sub_child_stn")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportSubChildStn implements Serializable {

    @Id
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "sub_packetType_code")
    private Integer sub_packetType_code;
    

    @ManyToOne
    @JoinColumn(name = "report_master_child_id", nullable = false)
    private ReportMasterChild reportMasterChild;
}