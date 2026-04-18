package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.KavachAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

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
    @Query(value = """
        SELECT ka.*, kad.ticket_no, kad.ticket_status
        FROM kavach_alert ka
        LEFT JOIN kavach_alert_details kad ON kad.kavach_alert_id = ka.id
        WHERE ka.event_time BETWEEN :from AND :to
          AND (:locoId IS NULL OR ka.loco_id = :locoId)
          AND (:stnId IS NULL OR ka.station_id = :stnId)
          AND (:severity IS NULL OR ka.severity = :severity)
          AND (:category IS NULL OR ka.alert_category = :category)
          AND NOT EXISTS (
              SELECT 1 FROM alert_message_config amc
              WHERE amc.enabled = false
                AND amc.alert_category = ka.alert_category
                AND amc.alert_message  = ka.alert_message
          )
        ORDER BY ka.event_time DESC
        """, nativeQuery = true)
    List<Object[]> findByFiltersWithDetails(
            @Param("from") Date from,
            @Param("to") Date to,
            @Param("locoId") Integer locoId,
            @Param("stnId") Integer stnId,
            @Param("severity") String severity,
            @Param("category") String category,
            Pageable pageable);

    @Query("SELECT DISTINCT k.alertCode, k.alertMessage FROM KavachAlert k WHERE k.alertCategory = :category")
    List<Object[]> findDistinctAlertCodeAndMessage(@Param("category") String category);

    @Query("SELECT DISTINCT a.alertCategory FROM KavachAlert a ORDER BY a.alertCategory")
    List<String> findDistinctAlertCategories();


    @Query(value = "SELECT a.alert_message, s.name AS station_name, a.event_time " +
            "FROM kavach_alert a " +
            "LEFT JOIN station s ON a.station_id = s.tcas_subsys_id " +
            "WHERE DATE(a.event_time) = CURRENT_DATE " +
            "ORDER BY a.event_time DESC " +
            "LIMIT 10",
            nativeQuery = true)
    List<Object[]> findTop10TodayAlerts();
}
