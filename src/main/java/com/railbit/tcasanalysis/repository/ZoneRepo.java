package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoneRepo extends JpaRepository<Zone,Integer> {
    Zone findByName(String name);
    Zone findByCode(String code);
}
