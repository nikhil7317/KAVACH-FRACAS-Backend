package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.IncidentTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IncidentTrackRepository extends JpaRepository<IncidentTrack, Long> {
    List<IncidentTrack> findByKavachAlertDetailsIdOrderByIncidentCreatedAtDesc(Long kavachAlertDetailsId);
}