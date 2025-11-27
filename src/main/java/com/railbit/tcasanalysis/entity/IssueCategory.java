package com.railbit.tcasanalysis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.io.Serializable;

@Entity(name = "issuecategory")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssueCategory implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Primary Key Id")
    private Integer id;
    @Comment("Issue Name")
    private String name;
    @ManyToOne
    @Comment("Project Type Id")
    private ProjectType projectType = new ProjectType(1,"");

}
