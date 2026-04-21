package com.railbit.tcasanalysis.repository.alertManagementRepo;

import com.railbit.tcasanalysis.entity.KavachAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertAnalysisRepository extends JpaRepository<KavachAlert, Long> {

    @Query(value = """
            SELECT ka.alert_category, COUNT(*) AS cnt
            FROM kavach_alert ka
            WHERE ka.event_time >= CURDATE()
              AND ka.event_time < CURDATE() + INTERVAL 1 DAY
              AND NOT EXISTS (
                  SELECT 1 FROM alert_message_config amc
                  WHERE amc.enabled = false
                    AND amc.alert_category = ka.alert_category
                    AND amc.alert_message = ka.alert_message
              )
            GROUP BY ka.alert_category
            ORDER BY cnt DESC
            """, nativeQuery = true)
    List<Object[]> countByAlertCategoryToday();

    @Query(value = """
        SELECT 
            COUNT(*) AS total,
            COUNT(CASE WHEN UPPER(kad.ticket_status) = 'OPEN' THEN 1 END) AS pending,
            COUNT(CASE WHEN UPPER(kad.ticket_status) IN ('CLOSED','CLOSE') THEN 1 END) AS resolved
        FROM kavach_alert ka
        LEFT JOIN kavach_alert_details kad ON ka.id = kad.kavach_alert_id
        WHERE ka.event_time >= CURDATE()
          AND ka.event_time < CURDATE() + INTERVAL 1 DAY
          AND NOT EXISTS (
              SELECT 1 FROM alert_message_config amc
              WHERE amc.enabled = false
                AND amc.alert_category = ka.alert_category
                AND amc.alert_message = ka.alert_message
          )
        """, nativeQuery = true)
    List<Object[]> getResolutionStatusToday();

    @Query(value = """
        SELECT 
            s.name AS station_name,
            DAYOFWEEK(ka.event_time) AS day_num,
            COUNT(*) AS cnt
        FROM kavach_alert ka
        LEFT JOIN station s ON ka.station_id = s.tcas_subsys_id
        WHERE ka.event_time >= CURDATE() - INTERVAL 6 DAY
          AND ka.event_time < CURDATE() + INTERVAL 1 DAY
          AND s.name IS NOT NULL
          AND NOT EXISTS (
              SELECT 1 FROM alert_message_config amc
              WHERE amc.enabled = false
                AND amc.alert_category = ka.alert_category
                AND amc.alert_message = ka.alert_message
          )
        GROUP BY s.name, day_num
        """, nativeQuery = true)
    List<Object[]> getStationHeatmap();
}