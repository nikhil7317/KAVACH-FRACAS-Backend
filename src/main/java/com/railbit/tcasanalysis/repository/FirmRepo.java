package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.Firm;
import com.railbit.tcasanalysis.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FirmRepo extends JpaRepository<Firm,Integer> {
    Firm findByName(String name);
}
