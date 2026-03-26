package com.railbit.tcasanalysis.repository;


import com.railbit.tcasanalysis.entity.TagLinkingRfidEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagLinkingRfidEntryRepository extends JpaRepository<TagLinkingRfidEntry, Integer> {
}

