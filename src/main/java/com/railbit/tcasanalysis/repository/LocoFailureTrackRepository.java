package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.LocoFailureTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocoFailureTrackRepository extends JpaRepository<LocoFailureTrack, Long> {

    List<LocoFailureTrack> findByLocoFailureIdOrderByIncidentCreatedAtDesc(Long locoFailureId);
}