package com.railbit.tcasanalysis.repository.alertManagementRepo;

import com.railbit.tcasanalysis.entity.KavachAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmergencyMonitoringRepository extends JpaRepository<KavachAlert, Long> {

    // 🔹 Counts for cards
    @Query(value = """
        SELECT ka.alert_category, COUNT(*) 
        FROM kavach_alert ka
        WHERE ka.event_time >= CURDATE()
          AND ka.event_time < CURDATE() + INTERVAL 1 DAY
          AND UPPER(ka.severity) = 'CRITICAL'
          AND NOT EXISTS (
              SELECT 1 FROM alert_message_config amc
              WHERE amc.enabled = false
                AND amc.alert_category = ka.alert_category
                AND amc.alert_message = ka.alert_message
          )
        GROUP BY ka.alert_category
        ORDER BY COUNT(*) DESC
        """, nativeQuery = true)
    List<Object[]> getCriticalCounts();


    // 🔹 Table Data
    @Query(value = """
        SELECT 
            ka.event_time,
            ka.loco_id,
            s.name,
            ka.alert_message,
            ka.alert_category,
            ka.severity,
            kad.ticket_status
        FROM kavach_alert ka
        LEFT JOIN station s ON ka.station_id = s.tcas_subsys_id
        LEFT JOIN kavach_alert_details kad ON ka.id = kad.kavach_alert_id
        WHERE ka.event_time >= CURDATE()
          AND ka.event_time < CURDATE() + INTERVAL 1 DAY
          AND UPPER(ka.severity) = 'CRITICAL'
          AND NOT EXISTS (
              SELECT 1 FROM alert_message_config amc
              WHERE amc.enabled = false
                AND amc.alert_category = ka.alert_category
                AND amc.alert_message = ka.alert_message
          )
        ORDER BY ka.event_time DESC
        """, nativeQuery = true)
    List<Object[]> getTableData();
}
