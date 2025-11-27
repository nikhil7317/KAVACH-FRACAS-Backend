package com.railbit.tcasanalysis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.io.Serializable;

@Entity(name = "possibleissue")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PossibleIssue implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Primary Key Id")
    private Integer id;
    @Comment("Issue Name")
    private String name;
    @ManyToOne
    @Comment("Issue Category Id")
    private IssueCategory issueCategory;
    @ManyToOne
    @Comment("Project Type Id")
    private ProjectType projectType;

}
