package com.railbit.tcasanalysis.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;


import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "static_speed_profile")
@Setter
@Getter
public class StaticSpeedProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference("subpkt-ssp")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_packet_id", nullable = false, unique = true)
    private SubPacket subPacket;

    @Column(name = "speed_info_count")
    private Integer speedInfoCount;

    @JsonManagedReference("ssp-entry")
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("entryIndex ASC")
    private List<StaticSpeedEntry> entries = new ArrayList<>();

    public void addEntry(StaticSpeedEntry entry) {
        entries.add(entry);
        entry.setProfile(this);
        entry.setEntryIndex(entries.size());
    }
}
