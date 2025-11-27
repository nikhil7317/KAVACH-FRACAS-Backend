package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.Firm;
import com.railbit.tcasanalysis.entity.loco.LocoType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocoTypeRepo extends JpaRepository<LocoType,Integer> {
    LocoType findByName(String name);
}
