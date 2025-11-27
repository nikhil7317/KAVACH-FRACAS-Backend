package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.Designation;
import com.railbit.tcasanalysis.entity.loco.Shed;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DesignationRepo extends JpaRepository<Designation,Integer> {
    Designation findByName(String name);
}
