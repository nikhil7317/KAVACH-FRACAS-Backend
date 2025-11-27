package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.Station;
import com.railbit.tcasanalysis.entity.nmspackets.FaultCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FaultCodeRepo extends JpaRepository<FaultCode,Integer> {

    FaultCode findByCode(Integer id);
}
