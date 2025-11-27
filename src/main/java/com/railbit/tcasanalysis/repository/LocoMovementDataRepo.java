package com.railbit.tcasanalysis.repository;

//import com.railbit.tcasanalysis.entity.IncidentNo;
import com.railbit.tcasanalysis.entity.LocoMovementData;
import com.railbit.tcasanalysis.entity.LocoMovementSummary;
import com.railbit.tcasanalysis.entity.loco.Loco;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface LocoMovementDataRepo extends JpaRepository<LocoMovementData, Long> {

    // Find by locoID and date range (optional) and order by date descending
    List<LocoMovementData> findByLocoIDAndDateBetweenOrderByIdDesc(Loco locoID, Date startDate, Date endDate, Pageable pageable);

    // Find by locoID only and order by date descending
    List<LocoMovementData> findByLocoIDOrderByIdDesc(Loco locoID, Pageable pageable);

    // Find by date range only and order by date descending
    List<LocoMovementData> findByDateBetweenOrderByIdDesc(Date startDate, Date endDate, Pageable pageable);

    List<LocoMovementData> findByLocoIDAndDateBetweenOrderByIdAsc(Loco locoID, Date startDate, Date endDate);
    // Retrieve all records ordered by date descending
    List<LocoMovementData> findAllByOrderByIdDesc(Pageable pageable);

    @Query(value = "SELECT DISTINCT lmd.locoID " +
            "FROM locomovementdata lmd " +
            "WHERE lmd.date = :fromDate " +
            "AND lmd.time BETWEEN :fromTime AND :toTime AND lmd.locoID IS NOT NULL AND lmd.stnCode IS NOT NULL ",
            nativeQuery = true)
    List<String> getDistinctLocoIds(@Param("fromDate") String fromDate,
                                    @Param("fromTime") String fromTime,
                                    @Param("toTime") String toTime);

    @Query(value = "SELECT lmd.* FROM locomovementdata lmd " +
            "JOIN loco l2 ON lmd.locoID = l2.id " +
            "WHERE lmd.locoID = :locoNo " +
            "AND lmd.date = :fromDate " +
            "AND lmd.time BETWEEN :fromTime AND :toTime " +
            "AND lmd.locoID IS NOT NULL AND lmd.stnCode IS NOT NULL " +
            "ORDER BY lmd.time ASC",
            nativeQuery = true)
    List<LocoMovementData> getFilteredLocoMovementData(
            @Param("locoNo") String locoNo,
            @Param("fromDate") String fromDate,
            @Param("fromTime") String fromTime,
            @Param("toTime") String toTime
    );

    @Query(value = "SELECT lmd.* FROM locomovementdata lmd " +
            "JOIN loco l2 ON lmd.locoID = l2.id " +
            "WHERE lmd.locoID = :locoNo " +
            "AND lmd.date = :fromDate " +
            "AND lmd.time BETWEEN :fromTime AND :toTime " +
            "AND lmd.locoID IS NOT NULL AND lmd.stnCode IS NOT NULL " +
            "AND lmd.locoMode != :locoMode " +
            "ORDER BY lmd.time ASC LIMIT 1",
            nativeQuery = true)
    LocoMovementData getFilteredLocoMovementDataWithLocoMode(
            @Param("locoNo") String locoNo,
            @Param("fromDate") String fromDate,
            @Param("fromTime") String fromTime,
            @Param("toTime") String toTime,
            @Param("locoMode") String locoMode
    );

    @Query("SELECT e FROM locomovementsummary e WHERE e.reportFromTime >= :startDate AND e.reportFromTime <= :endDate")
    List<LocoMovementSummary> findByReportFromTimeBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT COUNT(l) FROM locomovementdata l WHERE l.emrStatus = :status AND l.date BETWEEN :fromDate AND :toDate")
    int countByEmrStatusAndDate(@Param("status") String status, @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    @Query("SELECT COUNT(l) FROM locomovementdata l WHERE l.emrGenSOS = :status AND l.date BETWEEN :fromDate AND :toDate")
    int countByEmrGenSOSAndDate(@Param("status") String status, @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    @Query("SELECT COUNT(l) FROM locomovementdata l WHERE l.locoMode = :mode AND l.date BETWEEN :fromDate AND :toDate")
    int countByLocoModeAndDate(@Param("mode") String mode, @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    @Query("SELECT l FROM locomovementdata l WHERE l.emrStatus = :status AND l.date BETWEEN :fromDate AND :toDate")
    List<LocoMovementData> locoMovementDataListByEmrStatusAndDate(@Param("status") String status, @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    @Query("SELECT l FROM locomovementdata l WHERE l.emrGenSOS = :status AND l.date BETWEEN :fromDate AND :toDate")
    List<LocoMovementData> locoMovementDataListByEmrGenSOSAndDate(@Param("status") String status, @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    @Query("SELECT l FROM locomovementdata l WHERE l.locoMode = :mode AND l.date BETWEEN :fromDate AND :toDate")
    List<LocoMovementData> locoMovementDataListByLocoModeAndDate(@Param("mode") String mode, @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    // Make sure this is a native query
    @Query(value = """
    SELECT 
        SUM(CASE WHEN emrStatus = 'spare' THEN 1 ELSE 0 END) AS spareCount,
        SUM(CASE WHEN emrStatus = 'Head On Collision' THEN 1 ELSE 0 END) AS headOnCollisionCount,
        SUM(CASE WHEN emrStatus = 'Rear End Collision' THEN 1 ELSE 0 END) AS rearEndCollisionCount,
        SUM(CASE WHEN emrStatus = 'Sos' THEN 1 ELSE 0 END) AS sosLocoCount,
        SUM(CASE WHEN emrGenSOS = 'Sos' THEN 1 ELSE 0 END) AS sosStationCount,
        SUM(CASE WHEN locoMode = 'override' THEN 1 ELSE 0 END) AS overrideModeCount,
        SUM(CASE WHEN locoMode = 'trip' THEN 1 ELSE 0 END) AS tripModeCount
    FROM locomovementdata
    WHERE date BETWEEN :fromDate AND :toDate
""", nativeQuery = true)
    List<Object[]> getAllAlertCounts(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);




}