package com.railbit.tcasanalysis.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity(name = "assignedincident")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignedIncident implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Primary Key Id")
    private Long id;

    @ManyToOne
    private TcasBreakingInspection tcasBreakingInspection;
    @ManyToOne
    private User assignedFromUser;
    @ManyToOne
    private User assignedToUser;

    @Column(columnDefinition = "BOOLEAN default 0")
    @Comment("0 for pending, 1 for assigned")
    private Boolean status=false;

    @CreationTimestamp
    @JsonFormat
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdDateTime = LocalDateTime.now();

    @Transient
    private String incidentTag;  // This field will not be persisted to the database

    @Transient
    private String remark;  // This field will not be persisted to the database

    @Transient
    private Integer remarkType;  // This field will not be persisted to the database
}
