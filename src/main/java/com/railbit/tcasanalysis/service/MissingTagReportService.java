package com.railbit.tcasanalysis.service;


import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.*;

@Service
public class MissingTagReportService {

    @Autowired
    private EntityManager entityManager;

    /**
     * Get missing/faulty RFID tag report from kavach_alert table.
     *
     * @param fromDate required
     * @param toDate   required
     * @param locoId   optional - filter by loco
     * @return list of tag events + summary counts
     */
    public Map<String, Object> getMissingTagReport(Date fromDate, Date toDate, Integer locoId) {

        // 1. Fetch all TAG_LINK alerts
        String sql =
                "SELECT a.event_time, a.loco_id, a.station_id, a.alert_code, " +
                        "       a.alert_message, a.last_rfid_tag, a.train_speed, " +
                        "       a.loco_mode, a.abs_loco_loc, a.alert_category, a.source_pkt_type " +
                        "FROM kavach_alert a " +
                        "WHERE a.alert_category IN ('TAG_LINK', 'RFID_ISSUE') " +
                        "AND a.event_time BETWEEN :fromDate AND :toDate ";

        if (locoId != null) {
            sql += "AND a.loco_id = :locoId ";
        }
        sql += "ORDER BY a.event_time DESC";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);
        if (locoId != null) {
            query.setParameter("locoId", locoId);
        }

        List<Object[]> rows = query.getResultList();

        // 2. Build event list
        List<Map<String, Object>> events = new ArrayList<>();
        int dupMissing = 0, mainMissing = 0, bothMissing = 0, posInterchanged = 0;
        int missingRfid = 0, invalidRfid = 0, conflictRouteRfid = 0;
        Set<Integer> uniqueTags = new HashSet<>();
        Set<Integer> uniqueLocos = new HashSet<>();

        for (Object[] row : rows) {
            Map<String, Object> event = new LinkedHashMap<>();
            event.put("eventTime", row[0]);
            event.put("locoId", row[1]);
            event.put("stationId", row[2]);
            event.put("alertCode", row[3]);
            event.put("alertMessage", row[4]);
            event.put("lastRfidTag", row[5]);
            event.put("trainSpeed", row[6]);
            event.put("locoMode", row[7]);
            event.put("absLocoLoc", row[8]);
            event.put("alertCategory", row[9]);
            event.put("sourcePktType", row[10]);
            events.add(event);

            String category = row[9] != null ? row[9].toString() : "";
            int code = ((Number) row[3]).intValue();

            if ("TAG_LINK".equals(category)) {
                switch (code) {
                    case 1: dupMissing++; break;
                    case 2: mainMissing++; break;
                    case 3: bothMissing++; break;
                    case 4: posInterchanged++; break;
                }
            } else if ("RFID_ISSUE".equals(category)) {
                switch (code) {
                    case 38: missingRfid++; break;
                    case 39: invalidRfid++; break;
                    case 40: conflictRouteRfid++; break;
                }
            }

            if (row[5] != null) uniqueTags.add(((Number) row[5]).intValue());
            if (row[1] != null) uniqueLocos.add(((Number) row[1]).intValue());
        }

        // 3. Summary
        Map<String, Integer> summary = new LinkedHashMap<>();
        summary.put("Duplicate Tag Missing", dupMissing);
        summary.put("Main Tag Missing", mainMissing);
        summary.put("Both Tag Missing", bothMissing);
        summary.put("Tag Position Interchanged", posInterchanged);
        summary.put("Missing RFID (Station Health)", missingRfid);
        summary.put("Invalid RFID (Station Health)", invalidRfid);
        summary.put("Conflict Route RFID (Station Health)", conflictRouteRfid);
        summary.put("Total Events", rows.size());

        // 4. Build response
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("summary", summary);
        result.put("uniqueRfidTags", uniqueTags);
        result.put("uniqueRfidTagCount", uniqueTags.size());
        result.put("affectedLocos", uniqueLocos);
        result.put("affectedLocoCount", uniqueLocos.size());
        result.put("events", events);

        return result;
    }
}
 