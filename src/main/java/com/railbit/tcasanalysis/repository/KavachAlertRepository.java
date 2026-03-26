package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.KavachAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface KavachAlertRepository extends JpaRepository<KavachAlert, Long> {

    // Date range (required)
    @Query("SELECT a FROM KavachAlert a " +
           "WHERE a.eventTime BETWEEN :from AND :to " +
           "ORDER BY a.eventTime DESC")
    List<KavachAlert> findByDateRange(
            @Param("from") Date from, @Param("to") Date to);

    // Date + locoId
    @Query("SELECT a FROM KavachAlert a " +
           "WHERE a.eventTime BETWEEN :from AND :to " +
           "AND a.locoId = :locoId " +
           "ORDER BY a.eventTime DESC")
    List<KavachAlert> findByDateRangeAndLocoId(
            @Param("from") Date from, @Param("to") Date to,
            @Param("locoId") Integer locoId);

    // Date + stationId
    @Query("SELECT a FROM KavachAlert a " +
           "WHERE a.eventTime BETWEEN :from AND :to " +
           "AND a.stationId = :stnId " +
           "ORDER BY a.eventTime DESC")
    List<KavachAlert> findByDateRangeAndStationId(
            @Param("from") Date from, @Param("to") Date to,
            @Param("stnId") Integer stnId);

    // Date + severity
    @Query("SELECT a FROM KavachAlert a " +
           "WHERE a.eventTime BETWEEN :from AND :to " +
           "AND a.severity = :severity " +
           "ORDER BY a.eventTime DESC")
    List<KavachAlert> findByDateRangeAndSeverity(
            @Param("from") Date from, @Param("to") Date to,
            @Param("severity") String severity);

    // Date + category
    @Query("SELECT a FROM KavachAlert a " +
           "WHERE a.eventTime BETWEEN :from AND :to " +
           "AND a.alertCategory = :category " +
           "ORDER BY a.eventTime DESC")
    List<KavachAlert> findByDateRangeAndCategory(
            @Param("from") Date from, @Param("to") Date to,
            @Param("category") String category);

    // All filters
    @Query("SELECT a FROM KavachAlert a " +
           "WHERE a.eventTime BETWEEN :from AND :to " +
           "AND (:locoId IS NULL OR a.locoId = :locoId) " +
           "AND (:stnId IS NULL OR a.stationId = :stnId) " +
           "AND (:severity IS NULL OR a.severity = :severity) " +
           "AND (:category IS NULL OR a.alertCategory = :category) " +
           "ORDER BY a.eventTime DESC")
    List<KavachAlert> findByFilters(
            @Param("from") Date from, @Param("to") Date to,
            @Param("locoId") Integer locoId,
            @Param("stnId") Integer stnId,
            @Param("severity") String severity,
            @Param("category") String category);
}
