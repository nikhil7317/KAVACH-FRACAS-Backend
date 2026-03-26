package com.railbit.tcasanalysis.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tag_linking_info")
@Setter
@Getter
public class TagLinkingInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference("subpkt-taglink")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_packet_id", nullable = false, unique = true)
    private SubPacket subPacket;

    @Column(name = "dist_dup_tag")
    private Integer distDupTag;

    @Column(name = "route_rfid_count")
    private Integer routeRfidCount;

    @Column(name = "adj_line_count")
    private Integer adjLineCount;

    @JsonManagedReference("taglink-rfid")
    @OneToMany(mappedBy = "tagLinkingInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("entryIndex ASC")
    private List<TagLinkingRfidEntry> rfidEntries = new ArrayList<>();

    @JsonManagedReference("taglink-tin")
    @OneToMany(mappedBy = "tagLinkingInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("entryIndex ASC")
    private List<TagLinkingLineTin> lineTins = new ArrayList<>();

    public void addRfidEntry(TagLinkingRfidEntry entry) {
        rfidEntries.add(entry);
        entry.setTagLinkingInfo(this);
        entry.setEntryIndex(rfidEntries.size());
    }

    public void addLineTin(TagLinkingLineTin tin) {
        lineTins.add(tin);
        tin.setTagLinkingInfo(this);
        tin.setEntryIndex(lineTins.size());
    }
}
