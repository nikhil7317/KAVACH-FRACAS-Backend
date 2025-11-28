package com.railbit.tcasanalysis.cactiRepo;


import com.railbit.tcasanalysis.entity.cactiEntity.CactiHost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CactiHostRepository extends JpaRepository<CactiHost, Integer> {
}

