package com.railbit.tcasanalysis.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "maintenance_user")
@Setter
@Getter
public class MaintenanceUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "maint_pk", referencedColumnName = "id", nullable = true)
    @JsonBackReference
    private MaintenanceReport maintenanceReport;

    @Column(name = "name", length = 250)
    private String name;

    @Column(name = "designation", length = 250)
    private String designation;

    @Column(name = "user_type", length = 250)
    private String userType;
}
