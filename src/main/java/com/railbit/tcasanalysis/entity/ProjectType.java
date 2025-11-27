package com.railbit.tcasanalysis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.io.Serializable;

@Entity(name = "projecttype")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectType implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Primary Key Id")
    private Integer id;
    @Comment("Project Name")
    private String name;
    public ProjectType(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
