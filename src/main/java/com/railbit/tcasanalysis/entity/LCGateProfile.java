package com.railbit.tcasanalysis.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lc_gate_profile")
@Setter
@Getter
public class LCGateProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference("subpkt-lcgate")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_packet_id", nullable = false, unique = true)
    private SubPacket subPacket;

    @Column(name = "lc_info_count")
    private Integer lcInfoCount;

    @JsonManagedReference("lcgate-entry")
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("entryIndex ASC")
    private List<LCGateEntry> entries = new ArrayList<>();

    public void addEntry(LCGateEntry entry) {
        entries.add(entry);
        entry.setProfile(this);
        entry.setEntryIndex(entries.size());
    }
}
