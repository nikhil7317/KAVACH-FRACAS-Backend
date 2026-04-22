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
            WHERE ka.event_time >= :fromDate AND ka.event_time < :toDate
              AND NOT EXISTS (
                  SELECT 1 FROM alert_message_config amc
                  WHERE amc.enabled = false
                    AND amc.alert_category = ka.alert_category
                    AND amc.alert_message  = ka.alert_message
              )
            """, nativeQuery = true)
    Long countAlertsInRange(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

    // ── Loco-wise ─────────────────────────────────────────────────────────────

    @Query(value = """
            SELECT ka.loco_id, COUNT(*) AS cnt
            FROM kavach_alert ka
            WHERE ka.event_time >= :fromDate AND ka.event_time < :toDate
              AND ka.loco_id IS NOT NULL
              AND NOT EXISTS (
                  SELECT 1 FROM alert_message_config amc
                  WHERE amc.enabled = false
                    AND amc.alert_category = ka.alert_category
                    AND amc.alert_message  = ka.alert_message
              )
            GROUP BY ka.loco_id
            ORDER BY cnt DESC
            """, nativeQuery = true)
    List<Object[]> countByLocoId(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

    // ── Category-wise ─────────────────────────────────────────────────────────

    @Query(value = """
            SELECT ka.alert_category, COUNT(*) AS cnt
            FROM kavach_alert ka
            WHERE ka.event_time >= :fromDate AND ka.event_time < :toDate
              AND NOT EXISTS (
                  SELECT 1 FROM alert_message_config amc
                  WHERE amc.enabled = false
                    AND amc.alert_category = ka.alert_category
                    AND amc.alert_message  = ka.alert_message
              )
            GROUP BY ka.alert_category
            ORDER BY cnt DESC
            """, nativeQuery = true)
    List<Object[]> countByAlertCategory(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

    // ── Station-wise ──────────────────────────────────────────────────────────

    @Query(value = """
            SELECT s.code AS station_name, COUNT(*) AS cnt
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
            GROUP BY s.code
            ORDER BY cnt DESC
        """, nativeQuery = true)
    List<Object[]> countByStationId(@Param("fromDate") Date fromDate,
                                    @Param("toDate") Date toDate);

    // ── Ticket counts ─────────────────────────────────────────────────────────

    @Query(value = """
            SELECT COUNT(*)
            FROM kavach_alert ka
            INNER JOIN kavach_alert_details kad ON ka.id = kad.kavach_alert_id
            WHERE ka.event_time >= :fromDate AND ka.event_time < :toDate
              AND kad.ticket_no IS NOT NULL AND kad.ticket_no <> ''
              AND NOT EXISTS (
                  SELECT 1 FROM alert_message_config amc
                  WHERE amc.enabled = false
                    AND amc.alert_category = ka.alert_category
                    AND amc.alert_message  = ka.alert_message
              )
            """, nativeQuery = true)
    Long countAlertsWithTicketInRange(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

    @Query(value = """
        SELECT COUNT(DISTINCT kad.ticket_no)
        FROM incident_track it
        INNER JOIN kavach_alert_details kad ON it.kavach_alert_details_id = kad.id
        INNER JOIN kavach_alert ka ON ka.id = kad.kavach_alert_id
        WHERE it.incident_created_at >= :fromDate 
          AND it.incident_created_at < :toDate
          AND UPPER(it.ticket_status) = 'OPEN'
          AND kad.ticket_no IS NOT NULL AND kad.ticket_no <> ''
          AND NOT EXISTS (
              SELECT 1 FROM alert_message_config amc
              WHERE amc.enabled = false
                AND amc.alert_category = ka.alert_category
                AND amc.alert_message  = ka.alert_message
          )
        """, nativeQuery = true)
    Long countOpenTicketsInRange(@Param("fromDate") Date fromDate,
                                 @Param("toDate") Date toDate);

    @Query(value = """
        SELECT COUNT(DISTINCT kad.ticket_no)
        FROM incident_track it
        INNER JOIN kavach_alert_details kad ON it.kavach_alert_details_id = kad.id
        INNER JOIN kavach_alert ka ON ka.id = kad.kavach_alert_id
        WHERE it.incident_created_at >= :fromDate 
          AND it.incident_created_at < :toDate
          AND UPPER(it.ticket_status) IN ('CLOSED', 'CLOSE')
          AND kad.ticket_no IS NOT NULL AND kad.ticket_no <> ''
          AND NOT EXISTS (
              SELECT 1 FROM alert_message_config amc
              WHERE amc.enabled = false
                AND amc.alert_category = ka.alert_category
                AND amc.alert_message  = ka.alert_message
          )
        """, nativeQuery = true)
    Long countClosedTicketsInRange(@Param("fromDate") Date fromDate,
                                   @Param("toDate") Date toDate);

    // ── Critical alerts count ─────────────────────────────────────────────────

    @Query(value = """
        SELECT COUNT(*)
        FROM kavach_alert ka
        WHERE ka.event_time >= :fromDate AND ka.event_time < :toDate
          AND UPPER(ka.severity) = 'CRITICAL'
          AND NOT EXISTS (
              SELECT 1 FROM alert_message_config amc
              WHERE amc.enabled = false
                AND amc.alert_category = ka.alert_category
                AND amc.alert_message  = ka.alert_message
          )
        """, nativeQuery = true)
    Long countCriticalAlertsInRange(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

    // ── Monthly category-wise (12 months) ─────────────────────────────────────

    @Query(value = """
            SELECT YEAR(ka.event_time)  AS yr,
                   MONTH(ka.event_time) AS mo,
                   ka.alert_category,
                   COUNT(*)             AS cnt
            FROM kavach_alert ka
            WHERE ka.event_time >= :fromDate
              AND NOT EXISTS (
                  SELECT 1 FROM alert_message_config amc
                  WHERE amc.enabled = false
                    AND amc.alert_category = ka.alert_category
                    AND amc.alert_message  = ka.alert_message
              )
            GROUP BY YEAR(ka.event_time), MONTH(ka.event_time), ka.alert_category
            ORDER BY YEAR(ka.event_time), MONTH(ka.event_time), ka.alert_category
            """, nativeQuery = true)
    List<Object[]> countByCategoryMonthly(@Param("fromDate") Date fromDate);
}