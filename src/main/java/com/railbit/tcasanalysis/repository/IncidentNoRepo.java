package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.Firm;
import com.railbit.tcasanalysis.entity.IncidentNo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IncidentNoRepo extends JpaRepository<IncidentNo,Long> {

    @Query("SELECT COALESCE(MAX(t.incidentNo), 0) FROM IncidentNo t")
    int getLatestIncidentNo();
}
