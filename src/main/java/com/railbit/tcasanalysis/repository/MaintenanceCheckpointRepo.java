package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.MaintenanceCheckpoint;
import com.railbit.tcasanalysis.entity.MaintenanceUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaintenanceCheckpointRepo extends JpaRepository<MaintenanceCheckpoint,Long> {
}
