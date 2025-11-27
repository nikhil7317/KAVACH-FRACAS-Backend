package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.LocoPktRetrieverConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LocoPktRetrieverConfigRepo extends JpaRepository<LocoPktRetrieverConfig,Integer> {

    @Query(value = "SELECT * FROM locopktretrieverconfig LIMIT 1", nativeQuery = true)
    LocoPktRetrieverConfig findAnySingleRecord();
}

