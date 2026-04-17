package com.railbit.tcasanalysis.repository.alertManagementRepo;

import com.railbit.tcasanalysis.entity.KavachAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HourlySeverityTrendRepository extends JpaRepository<KavachAlert, Long> {

    @Query(value = """
            SELECT 
                HOUR(ka.event_time) AS hr,
                UPPER(ka.severity) AS severity,
                COUNT(*) AS cnt
            FROM kavach_alert ka
            WHERE ka.event_time >= NOW() - INTERVAL 1 HOUR
              AND DATE(ka.event_time) = CURRENT_DATE
              AND UPPER(ka.severity) IN ('CRITICAL', 'WARNING', 'MEDIUM')
              AND NOT EXISTS (
                  SELECT 1 FROM alert_message_config amc
                  WHERE amc.enabled = false
                    AND amc.alert_category = ka.alert_category
                    AND amc.alert_message = ka.alert_message
              )
            GROUP BY hr, severity
            ORDER BY hr ASC
            """, nativeQuery = true)
    List<Object[]> getHourlyTrendLast1Hour();
}
