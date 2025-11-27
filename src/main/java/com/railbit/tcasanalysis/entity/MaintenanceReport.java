package com.railbit.tcasanalysis.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.railbit.tcasanalysis.entity.loco.Loco;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "maintenance_report")
@Getter
@Setter
public class MaintenanceReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "div_id")
    private Integer divId;

    @Column(name = "zone_id")
    private Integer zoneId;

    @ManyToOne
    @JoinColumn(name="stnId",referencedColumnName = "id")
    private Station stnId;

    @ManyToOne
    @JoinColumn(name="locoId",referencedColumnName = "id")
    private Loco locoId;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "frequency")
    private String frequency;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "maint_type")
    private String maintType;

    @Column(name = "briefdesc", columnDefinition = "TEXT")
    private String briefDesc;

    private String year;

    @Column(name = "file_url", columnDefinition = "TEXT")
    private String fileUrl;

    @Column(name = "file_name", columnDefinition = "TEXT")
    private String fileName;

    @Transient
    private String fileData;

    @OneToMany(mappedBy = "maintenanceReport", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<MaintenanceCheckpoint> checkpoints;

    @OneToMany(mappedBy = "maintenanceReport", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<MaintenanceUser> users;
}
