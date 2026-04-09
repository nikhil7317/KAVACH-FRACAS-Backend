package com.railbit.tcasanalysis.entity;



import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "category_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "severity_id", nullable = false)
    private Severity severity;
}