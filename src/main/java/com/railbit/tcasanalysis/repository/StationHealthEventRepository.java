package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.StationHealthEvent;
import com.railbit.tcasanalysis.entity.StationHealthPacket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationHealthEventRepository extends JpaRepository<StationHealthEvent, Long> {
}
