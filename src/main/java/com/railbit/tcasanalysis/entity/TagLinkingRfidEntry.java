package com.railbit.tcasanalysis.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "tag_linking_rfid_entry")
@Setter
@Getter
public class TagLinkingRfidEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference("taglink-rfid")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_linking_id", nullable = false)
    private TagLinkingInfo tagLinkingInfo;

    @Column(name = "entry_index")
    private Integer entryIndex;

    @Column(name = "dist_nxt_rfid")
    private Integer distNxtRfid;

    @Column(name = "nxt_rfid_tag_id")
    private Integer nxtRfidTagId;

    @Column(name = "dup_tag_dir")
    private String dupTagDir;

    @Column(name = "abs_loc_reset")
    private Integer absLocReset;

    @Column(name = "start_dist_to_loc_reset")
    private Integer startDistToLocReset;

    @Column(name = "adj_loco_dir")
    private String adjLocoDir;

    @Column(name = "abs_loc_correction")
    private Integer absLocCorrection;
}
