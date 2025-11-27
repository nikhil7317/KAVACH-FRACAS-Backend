package com.railbit.tcasanalysis.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "tcasbreakinginspectionstatus")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TcasBreakingInspectionStatus implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Primary Key Id")
    private Long id;

    @ManyToOne
    @Comment("Inspection Id")
    private TcasBreakingInspection tcasBreakingInspection;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String status;

    @ManyToOne
    @Comment("From User Id")
    private User user;

    @ManyToOne
    @Comment("Project Type Id")
    private ProjectType projectType;

    @ManyToOne
    @Comment("Assigned Incident Id")
    private AssignedIncident assignedIncident;

    @ManyToOne
    @Comment("Remark Type Id")
    private RemarkType remarkType;

    @CreationTimestamp
    @JsonFormat
    private LocalDateTime createdDateTime=LocalDateTime.now();

    private String images;
    @Transient
    private List<String> imageList;

    @PrePersist
    protected void onPrePersist() {
        if (this.remarkType == null) {
            this.remarkType = new RemarkType(1, null); // or fetch the RemarkType entity from the database
        }
        if (this.projectType == null) {
            this.projectType = new ProjectType(1,null); // or fetch the ProjectType entity from the database
        }
    }

}
