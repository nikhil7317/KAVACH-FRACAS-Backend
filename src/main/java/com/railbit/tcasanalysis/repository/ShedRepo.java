package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.loco.LocoType;
import com.railbit.tcasanalysis.entity.loco.Shed;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShedRepo extends JpaRepository<Shed,Integer> {
    Shed findByName(String name);
}
