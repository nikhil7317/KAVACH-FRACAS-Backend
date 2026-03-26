package com.railbit.tcasanalysis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.io.Serializable;

@Entity(name = "report_master")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportMaster implements Serializable {

    @Id
    @Comment("Primary Key Id")
    private Integer id;

    @Column(name = "message_type", nullable = false)
    private String messageType;

    @Column(name = "group_type")
    private String groupType;
}