package com.railbit.tcasanalysis.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AlertCountService {

    @Autowired
    private EntityManager entityManager;

    /**
     * Count critical events from Onboard Regular + Access Request packets.
     *
     * @param locoId       required - filter by SOURCE_LOCO_ID
     * @param fromDate     required - start datetime
     * @param toDate       required - end datetime
     * @return map of alert type → count
     */
    public Map<String, Integer> getAlertCounts(Integer locoId, Date fromDate, Date toDate) {

        Map<String, Integer> counts = new LinkedHashMap<>();
        counts.put("SOS Alerts", 0);
        counts.put("Emergency Brake", 0);
        counts.put("Head-on Collision", 0);
        counts.put("Rear End Collision", 0);
        counts.put("Main Tag Missing", 0);
        counts.put("Duplicate Tag Missing", 0);

        // --- From onboard_regular_packet ---
        String onboardSql =
            "SELECT " +
            "  SUM(CASE WHEN orp.emergency_status = 2 THEN 1 ELSE 0 END) AS sos, " +
            "  SUM(CASE WHEN orp.brake_applied = 4 THEN 1 ELSE 0 END) AS emg_brake, " +
            "  SUM(CASE WHEN orp.emergency_status = 4 THEN 1 ELSE 0 END) AS head_on, " +
            "  SUM(CASE WHEN orp.emergency_status = 5 THEN 1 ELSE 0 END) AS rear_end, " +
            "  SUM(CASE WHEN orp.tag_link_info = 2 THEN 1 ELSE 0 END) AS main_tag, " +
            "  SUM(CASE WHEN orp.tag_link_info = 1 THEN 1 ELSE 0 END) AS dup_tag " +
            "FROM onboard_regular_packet orp " +
            "JOIN loco_packet lp ON orp.loco_packet_id = lp.id " +
            "WHERE (:locoId IS NULL OR orp.source_loco_id = :locoId) " +
            "AND lp.at_date BETWEEN :fromDate AND :toDate";

        Query onboardQuery = entityManager.createNativeQuery(onboardSql);
        onboardQuery.setParameter("locoId", locoId);
        onboardQuery.setParameter("fromDate", fromDate);
        onboardQuery.setParameter("toDate", toDate);

        Object[] onboardRow = (Object[]) onboardQuery.getSingleResult();
        if (onboardRow != null) {
            counts.put("SOS Alerts", counts.get("SOS Alerts") + toInt(onboardRow[0]));
            counts.put("Emergency Brake", counts.get("Emergency Brake") + toInt(onboardRow[1]));
            counts.put("Head-on Collision", counts.get("Head-on Collision") + toInt(onboardRow[2]));
            counts.put("Rear End Collision", counts.get("Rear End Collision") + toInt(onboardRow[3]));
            counts.put("Main Tag Missing", counts.get("Main Tag Missing") + toInt(onboardRow[4]));
            counts.put("Duplicate Tag Missing", counts.get("Duplicate Tag Missing") + toInt(onboardRow[5]));
        }

        // --- From access_request_packet (only EMERGENCY_STATUS, no brake/tag) ---
        String accessSql =
            "SELECT " +
            "  SUM(CASE WHEN arp.emergency_status = 2 THEN 1 ELSE 0 END) AS sos, " +
            "  SUM(CASE WHEN arp.emergency_status = 4 THEN 1 ELSE 0 END) AS head_on, " +
            "  SUM(CASE WHEN arp.emergency_status = 5 THEN 1 ELSE 0 END) AS rear_end " +
            "FROM access_request_packet arp " +
            "JOIN loco_packet lp ON arp.loco_packet_id = lp.id " +
            "WHERE arp.source_loco_id = :locoId " +
            "AND lp.at_date BETWEEN :fromDate AND :toDate";

        Query accessQuery = entityManager.createNativeQuery(accessSql);
        accessQuery.setParameter("locoId", locoId);
        accessQuery.setParameter("fromDate", fromDate);
        accessQuery.setParameter("toDate", toDate);

        Object[] accessRow = (Object[]) accessQuery.getSingleResult();
        if (accessRow != null) {
            counts.put("SOS Alerts", counts.get("SOS Alerts") + toInt(accessRow[0]));
            counts.put("Head-on Collision", counts.get("Head-on Collision") + toInt(accessRow[1]));
            counts.put("Rear End Collision", counts.get("Rear End Collision") + toInt(accessRow[2]));
        }

        return counts;
    }

    private int toInt(Object val) {
        if (val == null) return 0;
        return ((Number) val).intValue();
    }
}
