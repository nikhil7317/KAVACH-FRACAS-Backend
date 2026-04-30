package com.railbit.tcasanalysis.repository.alertManagementRepo;

import com.railbit.tcasanalysis.entity.KavachAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertLogRepository extends JpaRepository<KavachAlert, Long> {

    @Query(value = """
        SELECT 
            ka.loco_id,
            s.name AS station_name,
            ka.alert_message,
            ka.severity,
            ka.event_time,
            kad.ticket_status,
            ka.station_id                 -- ✅ NEW
        FROM kavach_alert ka
        LEFT JOIN station s ON ka.station_id = s.tcas_subsys_id
        LEFT JOIN kavach_alert_details kad ON ka.id = kad.kavach_alert_id
        WHERE ka.event_time >= NOW() - INTERVAL 10 MINUTE
          AND ka.event_time >= CURDATE()                 -- ✅ current date start
          AND ka.event_time < CURDATE() + INTERVAL 1 DAY -- ✅ current date end
          AND UPPER(ka.severity) IN ('CRITICAL', 'WARNING', 'MEDIUM')
          AND NOT EXISTS (
              SELECT 1 FROM alert_message_config amc
              WHERE amc.enabled = false
                AND amc.alert_category = ka.alert_category
                AND amc.alert_message = ka.alert_message
          )
        ORDER BY ka.event_time DESC
        """, nativeQuery = true)
    List<Object[]> getAlertLogs();
}