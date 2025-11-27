package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.MaintenanceUser;
import com.railbit.tcasanalysis.entity.nmspackets.NmsIncidentDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NmsIncidentDateRepo extends JpaRepository<NmsIncidentDate,Integer> {

    @Query(value = "SELECT * FROM nms_incident_date ORDER BY id ASC LIMIT 1", nativeQuery = true)
    NmsIncidentDate findFirstRecord();
}
