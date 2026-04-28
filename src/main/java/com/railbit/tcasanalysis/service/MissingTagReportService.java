package com.railbit.tcasanalysis.service;


import com.railbit.tcasanalysis.repository.KavachAlertRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.*;

@Service
public class MissingTagReportService {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private KavachAlertRepository repository;

    /**
     * Get missing/faulty RFID tag report from kavach_alert table.
     *
     * @param fromDate required
     * @param toDate   required
     * @param locoId   optional - filter by loco
     * @return list of tag events + summary counts
     */
    public Map<String, Object> getMissingTagReport(
            Date fromDate, Date toDate,
            Integer locoId, Integer alertCode,
            String severity, String stnCode){

        // 1. Fetch all TAG_LINK alerts
        String sql =
                "SELECT a.event_time, a.loco_id, s.code AS station_code, a.alert_code, " +
                        "       a.alert_message, a.last_rfid_tag, a.train_speed, " +
                        "       a.loco_mode, a.abs_loco_loc, a.alert_category, a.source_pkt_type, a.severity " +
                        "FROM kavach_alert a " +
                        "LEFT JOIN station s ON a.station_id = s.tcas_subsys_id " +
                        "WHERE a.alert_category IN ('TAG_LINK', 'RFID_ISSUE') " +
                        "AND a.event_time BETWEEN :fromDate AND :toDate " +

                        // ✅ ADD THIS BLOCK
                        "AND NOT EXISTS ( " +
                        "    SELECT 1 FROM alert_message_config amc " +
                        "    WHERE amc.enabled = false " +
                        "      AND amc.alert_category = a.alert_category " +
                        "      AND amc.alert_message = a.alert_message " +
                        ") ";

        if (locoId != null) {
            sql += "AND a.loco_id = :locoId ";
        }

        if (alertCode != null) {
            sql += "AND a.alert_code = :alertCode ";
        }
        if (severity != null && !severity.isEmpty()) {
            sql += "AND a.severity = :severity ";
        }
        if (stnCode != null && !stnCode.isEmpty()) {
            sql += "AND s.code = :stnCode ";
        }
        sql += "ORDER BY a.event_time DESC";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);
        if (locoId != null) {
            query.setParameter("locoId", locoId);
        }
        if (alertCode != null) {
            query.setParameter("alertCode", alertCode);
        }

        if (severity != null && !severity.isEmpty()) {
            query.setParameter("severity", severity);
        }
        if (stnCode != null && !stnCode.isEmpty()) {
            query.setParameter("stnCode", stnCode);
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
            event.put("stationCode", row[2]);
            event.put("alertCode", row[3]);
            event.put("alertMessage", row[4]);
            event.put("lastRfidTag", row[5]);
            event.put("trainSpeed", row[6]);
            event.put("locoMode", row[7]);
            event.put("absLocoLoc", row[8]);
            event.put("alertCategory", row[9]);
            event.put("sourcePktType", row[10]);
            event.put("severity", row[11]);
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

    public List<Map<String, Object>> getDistinctAlertMessagesWithId(String category) {

        List<Object[]> rows = repository.findDistinctAlertCodeAndMessage(category);

        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] row : rows) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", row[0]);          // alertCode
            map.put("message", row[1]);     // alertMessage
            result.add(map);
        }

        return result;
    }
}
 