package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.LocoMovementData;
import com.railbit.tcasanalysis.entity.nmspackets.NmsIncident;
import com.railbit.tcasanalysis.entity.nmspackets.NmsIncidentDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NmsIncidentRepo extends JpaRepository<NmsIncident,Integer> {
    Optional<NmsIncident> findByNmsIncidentId(String nmsId);

//    @Query(value = "SELECT * FROM nms_incident WHERE loco_id = :locoId AND trip_date LIKE %:tripDate% LIMIT 1", nativeQuery = true)
//    NmsIncident findTripByLocoIdAndTripMonth(@Param("locoId") Long locoId, @Param("tripDate") String tripDate);

    @Query(value = "SELECT * FROM nms_incident WHERE loco_id = :locoId AND trip_date LIKE %:tripDate% ORDER BY id DESC LIMIT 1", nativeQuery = true)
    NmsIncident findLocoLastRecord(@Param("locoId") Long locoId, @Param("tripDate") String tripDate);

    @Query(value = "SELECT COUNT(*) FROM nms_incident WHERE div_id = :divId AND trip_date LIKE %:tripDate%", nativeQuery = true)
    Integer countLocoIncidentRecords(@Param("divId") Integer divId, @Param("tripDate") String tripDate);


    @Query(value = "SELECT lmd.* FROM nms_incident lmd " +
            "where lmd.trip_date = :fromDate " +
            "ORDER BY lmd.incident_time ASC",
            nativeQuery = true)
    List<NmsIncident> getFilteredLocoMovementData(
            @Param("fromDate") String fromDate
    );
}
