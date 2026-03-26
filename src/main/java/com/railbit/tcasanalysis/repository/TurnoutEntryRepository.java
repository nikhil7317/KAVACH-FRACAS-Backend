package com.railbit.tcasanalysis.repository;


import com.railbit.tcasanalysis.entity.TurnoutEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TurnoutEntryRepository  extends JpaRepository<TurnoutEntry, Integer> {
}
