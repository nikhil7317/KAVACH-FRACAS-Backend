package com.railbit.tcasanalysis.entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tsr_profile")
@Setter
@Getter
public class TsrProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference("subpkt-tsr")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_packet_id", nullable = false, unique = true)
    private SubPacket subPacket;

    @Column(name = "tsr_status_code")
    private Integer tsrStatusCode;

    @Column(name = "tsr_status_str")
    private String tsrStatusStr;

    @Column(name = "tsr_info_count")
    private Integer tsrInfoCount;

    @JsonManagedReference("tsr-entry")
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("entryIndex ASC")
    private List<TsrEntry> entries = new ArrayList<>();

    public void addEntry(TsrEntry entry) {
        entries.add(entry);
        entry.setProfile(this);
        entry.setEntryIndex(entries.size());
    }
}
 