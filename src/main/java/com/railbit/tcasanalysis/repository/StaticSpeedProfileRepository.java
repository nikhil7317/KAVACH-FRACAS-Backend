package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.StaticSpeedProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaticSpeedProfileRepository extends JpaRepository<StaticSpeedProfile, Integer> {
}