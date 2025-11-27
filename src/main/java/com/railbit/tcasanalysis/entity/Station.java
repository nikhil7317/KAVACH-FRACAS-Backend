package com.railbit.tcasanalysis.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.io.Serializable;
import java.util.List;

@Entity(name = "station")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Station  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Primary Key Id")
    private Integer id;
    @Comment("Station Name")
    private String name;
    @Comment("Station Code")
    @Column(unique = true)
    private String code;

    @ManyToOne
    private Division division;
    @ManyToOne
    @Comment("Associated Firm")
    private Firm firm;
    @ManyToOne
    private Tcas tcas;

    @Column(name = "tcas_subsys_id")
    private Integer tcassubsysid;

    private String nmsVersion;

}
