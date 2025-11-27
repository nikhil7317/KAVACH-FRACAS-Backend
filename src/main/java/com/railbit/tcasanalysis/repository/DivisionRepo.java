package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.Division;
import com.railbit.tcasanalysis.entity.Station;
import com.railbit.tcasanalysis.entity.loco.Shed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DivisionRepo extends JpaRepository<Division,Integer> {
    Division findByName(String name);
    Division findByCode(String code);
    List<Division> findByZoneId(Integer zoneId);
}
