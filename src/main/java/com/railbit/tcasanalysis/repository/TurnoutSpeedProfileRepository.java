package com.railbit.tcasanalysis.repository;


import com.railbit.tcasanalysis.entity.TurnoutSpeedProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TurnoutSpeedProfileRepository extends JpaRepository<TurnoutSpeedProfile, Integer> {
}