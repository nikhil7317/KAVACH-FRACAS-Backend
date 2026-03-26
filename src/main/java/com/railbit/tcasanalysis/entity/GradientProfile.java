package com.railbit.tcasanalysis.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "gradient_profile")
@Setter
@Getter
public class GradientProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference("subpkt-grad")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_packet_id", nullable = false, unique = true)
    private SubPacket subPacket;

    @Column(name = "grad_info_count")
    private Integer gradInfoCount;

    @JsonManagedReference("grad-entry")
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("entryIndex ASC")
    private List<GradientEntry> entries = new ArrayList<>();

    public void addEntry(GradientEntry entry) {
        entries.add(entry);
        entry.setProfile(this);
        entry.setEntryIndex(entries.size());
    }
}
