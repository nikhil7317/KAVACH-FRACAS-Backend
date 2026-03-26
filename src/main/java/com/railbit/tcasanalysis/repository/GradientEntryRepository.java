package com.railbit.tcasanalysis.repository;


import com.railbit.tcasanalysis.entity.GradientEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradientEntryRepository extends JpaRepository<GradientEntry, Integer> {
}
