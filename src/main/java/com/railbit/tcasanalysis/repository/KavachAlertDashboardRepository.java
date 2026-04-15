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

    @Query("SELECT MAX(k.eventTime) FROM KavachAlert k")
    Date findLastAlertDate();

    // ── Total alert count in range ─────────────────────────────────────────────

    @Query("""
            SELECT COUNT(k)
            FROM KavachAlert k
            WHERE k.eventTime >= :fromDate AND k.eventTime < :toDate
            """)
    Long countAlertsInRange(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

    // ── Loco-wise count ────────────────────────────────────────────────────────

    @Query("""
            SELECT k.locoId, COUNT(k)
            FROM KavachAlert k
            WHERE k.eventTime >= :fromDate AND k.eventTime < :toDate
              AND k.locoId IS NOT NULL
            GROUP BY k.locoId
            ORDER BY COUNT(k) DESC
            """)
    List<Object[]> countByLocoId(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

    // ── Category-wise count ────────────────────────────────────────────────────

    @Query("""
            SELECT k.alertCategory, COUNT(k)
            FROM KavachAlert k
            WHERE k.eventTime >= :fromDate AND k.eventTime < :toDate
            GROUP BY k.alertCategory
            ORDER BY COUNT(k) DESC
            """)
    List<Object[]> countByAlertCategory(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

    // ── Station-wise count ─────────────────────────────────────────────────────

    @Query("""
            SELECT k.stationId, COUNT(k)
            FROM KavachAlert k
            WHERE k.eventTime >= :fromDate AND k.eventTime < :toDate
              AND k.stationId IS NOT NULL AND k.stationId <> 0
            GROUP BY k.stationId
            ORDER BY COUNT(k) DESC
            """)
    List<Object[]> countByStationId(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

    // ── Ticket counts ─────────────────────────────────────────────────────────
    // FIXED: Using JOIN with kavach_alert_details table where ticket data actually exists

    @Query(value = """
            SELECT COUNT(*)
            FROM kavach_alert ka
            INNER JOIN kavach_alert_details kad ON ka.id = kad.kavach_alert_id
            WHERE ka.event_time >= :fromDate AND ka.event_time < :toDate
              AND kad.ticket_no IS NOT NULL AND kad.ticket_no <> ''
            """, nativeQuery = true)
    Long countAlertsWithTicketInRange(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

    @Query(value = """
            SELECT COUNT(DISTINCT kad.ticket_no)
            FROM kavach_alert ka
            INNER JOIN kavach_alert_details kad ON ka.id = kad.kavach_alert_id
            WHERE ka.event_time >= :fromDate AND ka.event_time < :toDate
              AND kad.ticket_no IS NOT NULL AND kad.ticket_no <> ''
              AND UPPER(kad.ticket_status) = 'OPEN'
            """, nativeQuery = true)
    Long countOpenTicketsInRange(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

    @Query(value = """
            SELECT COUNT(DISTINCT kad.ticket_no)
            FROM kavach_alert ka
            INNER JOIN kavach_alert_details kad ON ka.id = kad.kavach_alert_id
            WHERE ka.event_time >= :fromDate AND ka.event_time < :toDate
              AND kad.ticket_no IS NOT NULL AND kad.ticket_no <> ''
              AND UPPER(kad.ticket_status) IN ('CLOSED', 'CLOSE')
            """, nativeQuery = true)
    Long countClosedTicketsInRange(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

    // ── Monthly category-wise data (last 12 months) ────────────────────────────

    @Query("""
            SELECT FUNCTION('YEAR', k.eventTime),
                   FUNCTION('MONTH', k.eventTime),
                   k.alertCategory,
                   COUNT(k)
            FROM KavachAlert k
            WHERE k.eventTime >= :fromDate
            GROUP BY FUNCTION('YEAR', k.eventTime),
                     FUNCTION('MONTH', k.eventTime),
                     k.alertCategory
            ORDER BY FUNCTION('YEAR', k.eventTime),
                     FUNCTION('MONTH', k.eventTime),
                     k.alertCategory
            """)
    List<Object[]> countByCategoryMonthly(@Param("fromDate") Date fromDate);
}