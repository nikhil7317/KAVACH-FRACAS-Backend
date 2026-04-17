package com.railbit.tcasanalysis.repository.alertManagementRepo;

import com.railbit.tcasanalysis.entity.KavachAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertCardsCountRepository extends JpaRepository<KavachAlert, Long> {

    @Query(value = """
            SELECT COUNT(*)
            FROM kavach_alert ka
            WHERE DATE(ka.event_time) = CURRENT_DATE
              AND NOT EXISTS (
                  SELECT 1 FROM alert_message_config amc
                  WHERE amc.enabled = false
                    AND amc.alert_category = ka.alert_category
                    AND amc.alert_message  = ka.alert_message
              )
            """, nativeQuery = true)
    Long countAlertsToday();

    @Query(value = """
            SELECT COUNT(*)
            FROM kavach_alert ka
            WHERE DATE(ka.event_time) = CURRENT_DATE
              AND UPPER(ka.severity) = 'CRITICAL'
              AND NOT EXISTS (
                  SELECT 1 FROM alert_message_config amc
                  WHERE amc.enabled = false
                    AND amc.alert_category = ka.alert_category
                    AND amc.alert_message  = ka.alert_message
              )
            """, nativeQuery = true)
    Long countCriticalAlertsToday();



    @Query(value = """
        SELECT COUNT(DISTINCT kad.ticket_no)
        FROM incident_track it
        INNER JOIN kavach_alert_details kad ON it.kavach_alert_details_id = kad.id
        INNER JOIN kavach_alert ka ON ka.id = kad.kavach_alert_id
        WHERE DATE(it.incident_created_at) = CURRENT_DATE
          AND UPPER(it.ticket_status) = 'OPEN'
          AND kad.ticket_no IS NOT NULL AND kad.ticket_no <> ''
          AND NOT EXISTS (
              SELECT 1 FROM alert_message_config amc
              WHERE amc.enabled = false
                AND amc.alert_category = ka.alert_category
                AND amc.alert_message  = ka.alert_message
          )
        """, nativeQuery = true)
    Long countOpenTicketsToday();


    @Query(value = """
        SELECT COUNT(DISTINCT kad.ticket_no)
        FROM incident_track it
        INNER JOIN kavach_alert_details kad ON it.kavach_alert_details_id = kad.id
        INNER JOIN kavach_alert ka ON ka.id = kad.kavach_alert_id
        WHERE DATE(it.incident_created_at) = CURRENT_DATE
          AND UPPER(it.ticket_status) IN ('CLOSED', 'CLOSE')
          AND kad.ticket_no IS NOT NULL AND kad.ticket_no <> ''
          AND NOT EXISTS (
              SELECT 1 FROM alert_message_config amc
              WHERE amc.enabled = false
                AND amc.alert_category = ka.alert_category
                AND amc.alert_message  = ka.alert_message
          )
        """, nativeQuery = true)
    Long countClosedTicketsToday();
}