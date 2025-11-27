package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.Designation;
import com.railbit.tcasanalysis.entity.Tcas;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TcasRepo extends JpaRepository<Tcas,Integer> {
    Tcas findByName(String name);
}
