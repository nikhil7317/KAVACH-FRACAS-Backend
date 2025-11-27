package com.railbit.tcasanalysis.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.io.Serializable;

@Entity(name = "zone")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Zone implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Primary Key Id")
    private Integer id;
    @Comment("ZONE Name")
    private String name;
    @Comment("Zone Code")
    private String code;
    private String zonalId;

}
