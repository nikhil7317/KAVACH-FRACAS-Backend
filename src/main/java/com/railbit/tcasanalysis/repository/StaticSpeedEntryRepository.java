package com.railbit.tcasanalysis.repository;


import com.railbit.tcasanalysis.entity.StaticSpeedEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaticSpeedEntryRepository extends JpaRepository<StaticSpeedEntry, Integer> {
}
