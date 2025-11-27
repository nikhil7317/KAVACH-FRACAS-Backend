package com.railbit.tcasanalysis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.io.Serializable;

@Entity(name = "rootcausesubcategory")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RootCauseSubCategory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Primary Key Id")
    private Integer id;
    @Comment("Category Name")
    private String name;

    @ManyToOne
    private PossibleRootCause possibleRootCause;

}
