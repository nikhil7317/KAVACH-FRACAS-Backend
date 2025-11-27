package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.MaintenanceReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MaintenanceReportRepo extends JpaRepository<MaintenanceReport,Long>, JpaSpecificationExecutor<MaintenanceReport> {
}
