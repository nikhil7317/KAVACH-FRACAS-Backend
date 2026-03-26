package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.TsrEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TsrEntryRepository extends JpaRepository<TsrEntry, Integer> {
}
