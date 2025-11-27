package com.railbit.tcasanalysis.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "maintenance_checkpoint")
@Setter
@Getter
public class MaintenanceCheckpoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "maint_pk", referencedColumnName = "id", nullable = true)
    @JsonBackReference
    private MaintenanceReport maintenanceReport;

    @Column(name = "name", length = 350)
    private String name;

    @Column(name = "remakrs", columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "file_url", columnDefinition = "TEXT")
    private String fileUrl;

    @Column(name = "file_name", columnDefinition = "TEXT")
    private String fileName;

    @Transient
    @JsonIgnore
    private String fileData;
}