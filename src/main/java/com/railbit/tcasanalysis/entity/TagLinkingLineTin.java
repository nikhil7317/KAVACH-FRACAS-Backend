package com.railbit.tcasanalysis.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "tag_linking_line_tin")
@Setter
@Getter
public class TagLinkingLineTin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference("taglink-tin")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_linking_id", nullable = false)
    private TagLinkingInfo tagLinkingInfo;

    @Column(name = "entry_index")
    private Integer entryIndex;

    @Column(name = "line_tin")
    private Integer lineTin;
}
