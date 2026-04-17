package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.OemRemarks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OemRemarksRepository extends JpaRepository<OemRemarks, Long> {

    // Check if OEM has submitted remarks for a specific ticket
    boolean existsByKavachAlertDetailsId(Long kavachAlertDetailsId);

    // Get all remarks for a ticket (for display)
    List<OemRemarks> findByKavachAlertDetailsIdOrderByCreatedAtDesc(Long kavachAlertDetailsId);
}