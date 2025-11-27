package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.LocoMovementSummary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocoMovementSummaryRepo extends JpaRepository<LocoMovementSummary,Long> {
}
