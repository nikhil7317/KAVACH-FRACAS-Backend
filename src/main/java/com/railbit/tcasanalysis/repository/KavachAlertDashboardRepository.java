package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.KavachAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface KavachAlertDashboardRepository extends JpaRepository<KavachAlert, Long> {

    // ── Last alert date ────────────────────────────────────────────────────────

    @Query(value = """
            SELECT MAX(ka.event_time)
            FROM kavach_alert ka
            WHERE NOT EXISTS (
                SELECT 1 FROM alert_message_config amc
                WHERE amc.enabled = false
                  AND amc.alert_category = ka.alert_category
                  AND amc.alert_message  = ka.alert_message
            )
            """, nativeQuery = true)
    Date findLastAlertDate();

    // ── Total count ────────────────────────────────────────────────────────────

    @Query(value = """
    SELECT COUNT(*)
    FROM kavach_alert ka
    INNER JOIN station s 
        ON ka.station_id = s.tcas_subsys_id

    WHERE ka.event_time >= :fromDate 
      AND ka.event_time < :toDate

      AND (:divisionId IS NULL OR s.division_id = :divisionId)

      AND NOT EXISTS (
          SELECT 1 
          FROM alert_message_config amc
          WHERE amc.enabled = false
            AND amc.alert_category = ka.alert_category
            AND amc.alert_message  = ka.alert_message
      )
    """, nativeQuery = true)
    Long countAlertsInRange(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("divisionId") Integer divisionId
    );

    // ── Loco-wise ─────────────────────────────────────────────────────────────

    @Query(value = """
        SELECT ka.loco_id, COUNT(*) AS cnt
        FROM kavach_alert ka
        INNER JOIN station s 
            ON ka.station_id = s.tcas_subsys_id
        INNER JOIN division d 
            ON s.division_id = d.id
        WHERE ka.event_time >= :fromDate 
          AND ka.event_time < :toDate
          AND ka.loco_id IS NOT NULL
          AND (:divisionId IS NULL OR d.id = :divisionId)
          AND NOT EXISTS (
              SELECT 1 
              FROM alert_message_config amc
              WHERE amc.enabled = false
                AND amc.alert_category = ka.alert_category
                AND amc.alert_message  = ka.alert_message
          )
        GROUP BY ka.loco_id
        ORDER BY cnt DESC
        """, nativeQuery = true)
    List<Object[]> countByLocoId(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("divisionId") Integer divisionId
    );

    // ── Category-wise ─────────────────────────────────────────────────────────

    @Query(value = """
    SELECT ka.alert_category, COUNT(*) AS cnt
    FROM kavach_alert ka

    INNER JOIN station s
        ON ka.station_id = s.tcas_subsys_id

    WHERE ka.event_time >= :fromDate
      AND ka.event_time < :toDate

      AND (:divisionId IS NULL OR s.division_id = :divisionId)

      AND NOT EXISTS (
          SELECT 1
          FROM alert_message_config amc
          WHERE amc.enabled = false
            AND amc.alert_category = ka.alert_category
            AND amc.alert_message = ka.alert_message
      )

    GROUP BY ka.alert_category
    ORDER BY cnt DESC
    """, nativeQuery = true)
    List<Object[]> countByAlertCategory(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("divisionId") Integer divisionId
    );

    // ── Station-wise ──────────────────────────────────────────────────────────

    @Query(value = """
        SELECT s.code AS station_name, COUNT(*) AS cnt
        FROM kavach_alert ka
        INNER JOIN station s ON ka.station_id = s.tcas_subsys_id
        WHERE ka.event_time >= :fromDate 
          AND ka.event_time < :toDate
          AND ka.station_id IS NOT NULL 
          AND ka.station_id <> 0
          
          AND (:divisionId IS NULL OR s.division_id = :divisionId)

          AND NOT EXISTS (
              SELECT 1 
              FROM alert_message_config amc
              WHERE amc.enabled = false
                AND amc.alert_category = ka.alert_category
                AND amc.alert_message  = ka.alert_message
          )
        GROUP BY s.code
        ORDER BY cnt DESC
    """, nativeQuery = true)
    List<Object[]> countByStationId(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("divisionId") Integer divisionId
    );
    // ── Ticket counts ─────────────────────────────────────────────────────────

    @Query(value = """
    SELECT COUNT(*)
    FROM kavach_alert ka
    INNER JOIN kavach_alert_details kad 
        ON ka.id = kad.kavach_alert_id
    INNER JOIN station s 
        ON ka.station_id = s.tcas_subsys_id

    WHERE ka.event_time >= :fromDate 
      AND ka.event_time < :toDate

      AND kad.ticket_no IS NOT NULL 
      AND kad.ticket_no <> ''

      AND (:divisionId IS NULL OR s.division_id = :divisionId)

      AND NOT EXISTS (
          SELECT 1 
          FROM alert_message_config amc
          WHERE amc.enabled = false
            AND amc.alert_category = ka.alert_category
            AND amc.alert_message  = ka.alert_message
      )
    """, nativeQuery = true)
    Long countAlertsWithTicketInRange(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("divisionId") Integer divisionId
    );

    @Query(value = """
    SELECT COUNT(DISTINCT kad.ticket_no)
    FROM kavach_alert_details kad
    INNER JOIN kavach_alert ka 
        ON ka.id = kad.kavach_alert_id
    INNER JOIN station s 
        ON ka.station_id = s.tcas_subsys_id

    WHERE ka.event_time >= :fromDate 
      AND ka.event_time < :toDate

      AND UPPER(kad.ticket_status) = 'OPEN'

      AND kad.ticket_no IS NOT NULL 
      AND kad.ticket_no <> ''

      AND (:divisionId IS NULL OR s.division_id = :divisionId)

      AND NOT EXISTS (
          SELECT 1 
          FROM alert_message_config amc
          WHERE amc.enabled = false
            AND amc.alert_category = ka.alert_category
            AND amc.alert_message  = ka.alert_message
      )
    """, nativeQuery = true)
    Long countOpenTicketsInRange(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("divisionId") Integer divisionId
    );

    @Query(value = """
    SELECT COUNT(DISTINCT kad.ticket_no)
    FROM kavach_alert_details kad
    INNER JOIN kavach_alert ka 
        ON ka.id = kad.kavach_alert_id
    INNER JOIN station s 
        ON ka.station_id = s.tcas_subsys_id

    WHERE ka.event_time >= :fromDate 
      AND ka.event_time < :toDate

      AND UPPER(kad.ticket_status) IN ('CLOSED', 'CLOSE')

      AND kad.ticket_no IS NOT NULL 
      AND kad.ticket_no <> ''

      AND (:divisionId IS NULL OR s.division_id = :divisionId)

      AND NOT EXISTS (
          SELECT 1 
          FROM alert_message_config amc
          WHERE amc.enabled = false
            AND amc.alert_category = ka.alert_category
            AND amc.alert_message  = ka.alert_message
      )
    """, nativeQuery = true)
    Long countClosedTicketsInRange(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("divisionId") Integer divisionId
    );

    // ── Critical alerts count ─────────────────────────────────────────────────

    @Query(value = """
    SELECT COUNT(*)
    FROM kavach_alert ka
    INNER JOIN station s 
        ON ka.station_id = s.tcas_subsys_id
    WHERE ka.event_time >= :fromDate 
      AND ka.event_time < :toDate
      AND UPPER(ka.severity) = 'CRITICAL'

      AND (:divisionId IS NULL OR s.division_id = :divisionId)

      AND NOT EXISTS (
          SELECT 1 
          FROM alert_message_config amc
          WHERE amc.enabled = false
            AND amc.alert_category = ka.alert_category
            AND amc.alert_message  = ka.alert_message
      )
    """, nativeQuery = true)
    Long countCriticalAlertsInRange(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("divisionId") Integer divisionId
    );

    // ── Monthly category-wise (12 months) ─────────────────────────────────────

    @Query(value = """
    SELECT YEAR(ka.event_time) AS yr,
           MONTH(ka.event_time) AS mo,
           ka.alert_category,
           COUNT(*) AS cnt

    FROM kavach_alert ka

    INNER JOIN station s
        ON ka.station_id = s.tcas_subsys_id

    WHERE ka.event_time >= :fromDate

      AND (:divisionId IS NULL OR s.division_id = :divisionId)

      AND NOT EXISTS (
          SELECT 1
          FROM alert_message_config amc
          WHERE amc.enabled = false
            AND amc.alert_category = ka.alert_category
            AND amc.alert_message = ka.alert_message
      )

    GROUP BY YEAR(ka.event_time),
             MONTH(ka.event_time),
             ka.alert_category

    ORDER BY YEAR(ka.event_time),
             MONTH(ka.event_time),
             ka.alert_category
    """, nativeQuery = true)
    List<Object[]> countByCategoryMonthly(
            @Param("fromDate") Date fromDate,
            @Param("divisionId") Integer divisionId
    );

    // ── Loco-wise severity breakdown ──────────────────────────────────────────────

    @Query(value = """
        SELECT ka.loco_id, ka.severity, COUNT(*) AS cnt
        FROM kavach_alert ka
        WHERE ka.event_time >= :fromDate AND ka.event_time < :toDate
          AND ka.loco_id IS NOT NULL
          AND NOT EXISTS (
              SELECT 1 FROM alert_message_config amc
              WHERE amc.enabled = false
                AND amc.alert_category = ka.alert_category
                AND amc.alert_message  = ka.alert_message
          )
        GROUP BY ka.loco_id, ka.severity
        ORDER BY ka.loco_id, ka.severity
        """, nativeQuery = true)
    List<Object[]> countByLocoIdAndSeverity(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

// ── Category-wise severity breakdown ─────────────────────────────────────────

    @Query(value = """
    SELECT ka.alert_category, ka.severity, COUNT(*) AS cnt
    FROM kavach_alert ka

    INNER JOIN station s
        ON ka.station_id = s.tcas_subsys_id

    WHERE ka.event_time >= :fromDate
      AND ka.event_time < :toDate

      AND (:divisionId IS NULL OR s.division_id = :divisionId)

      AND NOT EXISTS (
          SELECT 1
          FROM alert_message_config amc
          WHERE amc.enabled = false
            AND amc.alert_category = ka.alert_category
            AND amc.alert_message = ka.alert_message
      )

    GROUP BY ka.alert_category, ka.severity
    ORDER BY ka.alert_category, ka.severity
    """, nativeQuery = true)
    List<Object[]> countByAlertCategoryAndSeverity(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("divisionId") Integer divisionId
    );

// ── Station-wise severity breakdown ──────────────────────────────────────────

    @Query(value = """
        SELECT s.code AS station_name, ka.severity, COUNT(*) AS cnt
        FROM kavach_alert ka
        INNER JOIN station s ON ka.station_id = s.tcas_subsys_id
        WHERE ka.event_time >= :fromDate AND ka.event_time < :toDate
          AND ka.station_id IS NOT NULL AND ka.station_id <> 0
          AND NOT EXISTS (
              SELECT 1 FROM alert_message_config amc
              WHERE amc.enabled = false
                AND amc.alert_category = ka.alert_category
                AND amc.alert_message  = ka.alert_message
          )
        GROUP BY s.code, ka.severity
        ORDER BY s.code, ka.severity
        """, nativeQuery = true)
    List<Object[]> countByStationIdAndSeverity(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

// ── Monthly category-wise severity breakdown (12 months) ─────────────────────

    @Query(value = """
    SELECT YEAR(ka.event_time) AS yr,
           MONTH(ka.event_time) AS mo,
           ka.alert_category,
           ka.severity,
           COUNT(*) AS cnt

    FROM kavach_alert ka

    INNER JOIN station s
        ON ka.station_id = s.tcas_subsys_id

    WHERE ka.event_time >= :fromDate

      AND (:divisionId IS NULL OR s.division_id = :divisionId)

      AND NOT EXISTS (
          SELECT 1
          FROM alert_message_config amc
          WHERE amc.enabled = false
            AND amc.alert_category = ka.alert_category
            AND amc.alert_message = ka.alert_message
      )

    GROUP BY YEAR(ka.event_time),
             MONTH(ka.event_time),
             ka.alert_category,
             ka.severity

    ORDER BY YEAR(ka.event_time),
             MONTH(ka.event_time),
             ka.alert_category,
             ka.severity
    """, nativeQuery = true)
    List<Object[]> countByCategoryAndSeverityMonthly(
            @Param("fromDate") Date fromDate,
            @Param("divisionId") Integer divisionId
    );
}