package com.railbit.tcasanalysis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity(name = "tag")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Primary Key Id")
    private Long id;
    private String tagNo;
    private String tagType;
    private Double latitude;
    private Double longitude;
    private String roadNo;
    private String section;
    @ManyToOne
    private Station station;

}
