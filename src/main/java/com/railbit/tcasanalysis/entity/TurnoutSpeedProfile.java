package com.railbit.tcasanalysis.entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "turnout_speed_profile")
@Setter
@Getter
public class TurnoutSpeedProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference("subpkt-turnout")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_packet_id", nullable = false, unique = true)
    private SubPacket subPacket;

    @Column(name = "turnout_count")
    private Integer turnoutCount;

    @JsonManagedReference("turnout-entry")
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("entryIndex ASC")
    private List<TurnoutEntry> entries = new ArrayList<>();

    public void addEntry(TurnoutEntry entry) {
        entries.add(entry);
        entry.setProfile(this);
        entry.setEntryIndex(entries.size());
    }
}
