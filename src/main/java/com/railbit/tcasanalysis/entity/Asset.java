package com.railbit.tcasanalysis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.io.Serializable;

@Entity(name = "asset")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Asset implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Primary Key Id")
    private Long id;
    private String assetId;
    private String assetName;
    private String assetType;

    @ManyToOne
    private Station station;

    @ManyToOne
    private Division division;

    @ManyToOne
    private Firm firm;

    private String location;
    private String latitude;
    private String longitude;
    private String doc;
    private String codalLife;
    private String warrantyPeriod;

}
