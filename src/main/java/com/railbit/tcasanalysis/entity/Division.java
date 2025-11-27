package com.railbit.tcasanalysis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.io.Serializable;

@Entity(name = "division")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class  Division implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Primary Key Id")
    private Integer id;
    @Comment("Division Name")
    private String name;
    @Comment("Division Code")
    private String code;
    @Comment("Incident Count According to Division")
    private Long incidentCount;
    private String divisionalId;
    @ManyToOne
    private Zone zone;
}
