package com.railbit.tcasanalysis.entity;



import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "severity_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Severity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
}