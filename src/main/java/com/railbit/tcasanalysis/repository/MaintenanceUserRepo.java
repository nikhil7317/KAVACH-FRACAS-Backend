package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.MaintenanceReport;
import com.railbit.tcasanalysis.entity.MaintenanceUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaintenanceUserRepo extends JpaRepository<MaintenanceUser,Long> {
}
