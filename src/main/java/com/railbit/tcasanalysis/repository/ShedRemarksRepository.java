package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.ShedRemarks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShedRemarksRepository extends JpaRepository<ShedRemarks, Long> {

    boolean existsByLocoFailureId(Long locoFailureId);

    List<ShedRemarks> findByLocoFailureIdOrderByCreatedAtDesc(Long locoFailureId);
}