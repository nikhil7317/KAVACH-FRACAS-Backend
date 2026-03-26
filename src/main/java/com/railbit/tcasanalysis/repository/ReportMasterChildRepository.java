package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.ReportMasterChild;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportMasterChildRepository extends JpaRepository<ReportMasterChild, Integer> {

    List<ReportMasterChild> findByReportMasterId(Integer reportMasterId);
}