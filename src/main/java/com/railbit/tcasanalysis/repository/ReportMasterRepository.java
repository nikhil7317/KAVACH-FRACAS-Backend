package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.ReportMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportMasterRepository extends JpaRepository<ReportMaster, Integer> {
}