package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.KavachDashboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KavachDashboardRepository extends JpaRepository<KavachDashboard, Long> {
    // Additional custom queries can be added here if necessary
}
