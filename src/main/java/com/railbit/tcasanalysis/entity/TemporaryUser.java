package com.railbit.tcasanalysis.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.railbit.tcasanalysis.entity.loco.Shed;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity(name = "temporaryuser")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemporaryUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String contact;
    private String password;
    private String email;
    private Integer adminId;
    @Column(columnDefinition = "BOOLEAN default 1")
    private Boolean status=true;
    @Column(columnDefinition = "BOOLEAN default 1")
    private Boolean readPermission=true;
    @Column(columnDefinition = "BOOLEAN default 0")
    private Boolean writePermission=false;
    @Column(columnDefinition = "BOOLEAN default 0")
    private Boolean openPermission=false;
    @Column(columnDefinition = "BOOLEAN default 0")
    private Boolean closePermission=false;
    @ManyToOne
    private Role role;
    @ManyToOne
    private Shed shed;
    @ManyToOne
    private Firm firm;
    @ManyToOne
    private Division division;
    @ManyToOne
    private Designation designation;
    private String fullDesignation;
    private String otp;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiresAt;

}
