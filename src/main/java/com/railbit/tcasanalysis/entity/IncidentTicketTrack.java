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

@Entity(name = "incidenttickettrack")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncidentTicketTrack implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Primary Key Id")
    private Long id;

    @ManyToOne
    @Comment("Inspection Id")
    private IncidentTicket incidentTicket;

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
    }

}
