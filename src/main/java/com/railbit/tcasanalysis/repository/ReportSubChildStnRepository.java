package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.ReportSubChildStn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportSubChildStnRepository extends JpaRepository<ReportSubChildStn, Integer> {

    List<ReportSubChildStn> findByReportMasterChildId(Integer reportMasterChildId);
}