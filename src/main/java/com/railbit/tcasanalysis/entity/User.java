package com.railbit.tcasanalysis.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.railbit.tcasanalysis.authority.Authority;
import com.railbit.tcasanalysis.entity.loco.Shed;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "user")
@Data
@AllArgsConstructor
@NoArgsConstructor
//Customer table to store his details.
public class User implements UserDetails, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long adminId;
    private String name;
    @Column(unique = true)
    private String contact;
    @Column(unique = true)
    private String email;
    private String password;
    @Column(columnDefinition = "BOOLEAN default 1")
    private Boolean status=true;
    @Column(columnDefinition = "BOOLEAN default 1")
    private Boolean readPermission=true;
    @Column(columnDefinition = "BOOLEAN default 1")
    private Boolean writePermission=false;
    @Column(columnDefinition = "BOOLEAN default 1")
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
    private Zone zone;
    @ManyToOne
    private Designation designation;
    @CreationTimestamp
    @JsonFormat
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdDateTime=LocalDateTime.now();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<Authority> set =new HashSet<>();
            set.add(new Authority(role.getName()));
        return set;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.status;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.status;
    }
}
