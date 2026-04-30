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
            Integer locoId, String alertCategory,
            String severity, Integer stnId) {

        String sql =
                "SELECT a.event_time, a.loco_id, a.station_id, s.code AS station_code, a.alert_code, " +
                        "       a.alert_message, a.last_rfid_tag, a.train_speed, " +
                        "       a.loco_mode, a.abs_loco_loc, a.alert_category, a.source_pkt_type, a.severity " +
                        "FROM kavach_alert a " +
                        "LEFT JOIN station s ON a.station_id = s.tcas_subsys_id " +
                        "WHERE a.alert_category IN ('TAG_LINK', 'RFID_ISSUE') " +
                        "AND a.event_time BETWEEN :fromDate AND :toDate " +

                        // ✅ alert config filter
                        "AND NOT EXISTS ( " +
                        "    SELECT 1 FROM alert_message_config amc " +
                        "    WHERE amc.enabled = false " +
                        "      AND amc.alert_category = a.alert_category " +
                        "      AND amc.alert_message = a.alert_message " +
                        ") ";

        // ✅ optional filters
        if (locoId != null) {
            sql += "AND a.loco_id = :locoId ";
        }

        // ✅ CASE-INSENSITIVE CATEGORY FILTER
        if (alertCategory != null && !alertCategory.isEmpty()) {
            sql += "AND a.alert_message = :alertCategory ";
        }

        if (severity != null && !severity.isEmpty()) {
            sql += "AND UPPER(a.severity) = UPPER(:severity) ";
        }

        if (stnId != null) {
            sql += "AND a.station_id = :stnId ";
        }

        sql += "ORDER BY a.event_time DESC";

        Query query = entityManager.createNativeQuery(sql);

        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);

        if (locoId != null) {
            query.setParameter("locoId", locoId);
        }

        if (alertCategory != null && !alertCategory.trim().isEmpty()) {
            query.setParameter("alertCategory", alertCategory.trim());
        }

        if (severity != null && !severity.isEmpty()) {
            query.setParameter("severity", severity);
        }

        if (stnId != null) {
            query.setParameter("stnId", stnId);
        }

        List<Object[]> rows = Optional.ofNullable(query.getResultList())
                .orElse(new ArrayList<>());

        // ================= EVENTS =================
        List<Map<String, Object>> events = new ArrayList<>();

        int dupMissing = 0, mainMissing = 0, bothMissing = 0, posInterchanged = 0;
        int missingRfid = 0, invalidRfid = 0, conflictRouteRfid = 0;

        Set<Integer> uniqueTags = new HashSet<>();
        Set<Integer> uniqueLocos = new HashSet<>();

        for (Object[] row : rows) {

            Map<String, Object> event = new LinkedHashMap<>();

            event.put("eventTime", row[0]);
            event.put("locoId", row[1]);

            // ✅ station fields
            event.put("stationId", row[2]);
            event.put("stationCode", row[3] != null ? row[3] : "");

            event.put("alertCode", row[4] != null ? row[4] : 0);
            event.put("alertMessage", row[5] != null ? row[5] : "");
            event.put("lastRfidTag", row[6]);
            event.put("trainSpeed", row[7]);
            event.put("locoMode", row[8] != null ? row[8] : "");
            event.put("absLocoLoc", row[9]);
            event.put("alertCategory", row[10] != null ? row[10] : "");
            event.put("sourcePktType", row[11] != null ? row[11] : "");
            event.put("severity", row[12] != null ? row[12] : "");

            events.add(event);

            // ================= SUMMARY LOGIC =================
            int code = row[4] != null ? ((Number) row[4]).intValue() : 0;
            String category = row[10] != null ? row[10].toString() : "";

            if ("TAG_LINK".equalsIgnoreCase(category)) {
                switch (code) {
                    case 1: dupMissing++; break;
                    case 2: mainMissing++; break;
                    case 3: bothMissing++; break;
                    case 4: posInterchanged++; break;
                }
            } else if ("RFID_ISSUE".equalsIgnoreCase(category)) {
                switch (code) {
                    case 38: missingRfid++; break;
                    case 39: invalidRfid++; break;
                    case 40: conflictRouteRfid++; break;
                }
            }

            if (row[6] != null) {
                uniqueTags.add(((Number) row[6]).intValue());
            }

            if (row[1] != null) {
                uniqueLocos.add(((Number) row[1]).intValue());
            }
        }

        // ================= SUMMARY =================
        Map<String, Integer> summary = new LinkedHashMap<>();
        summary.put("Duplicate Tag Missing", dupMissing);
        summary.put("Main Tag Missing", mainMissing);
        summary.put("Both Tag Missing", bothMissing);
        summary.put("Tag Position Interchanged", posInterchanged);
        summary.put("Missing RFID (Station Health)", missingRfid);
        summary.put("Invalid RFID (Station Health)", invalidRfid);
        summary.put("Conflict Route RFID (Station Health)", conflictRouteRfid);
        summary.put("Total Events", rows.size());

        // ================= FINAL RESPONSE =================
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

        List<String> messages = repository.findDistinctMessages(category);
        Collections.reverse(messages);

        List<Map<String, Object>> result = new ArrayList<>();

        int id = 1; // custom ID since alertCode removed
        for (String message : messages) {

            Map<String, Object> map = new HashMap<>();
            map.put("id", id++);
            map.put("message", message);

            result.add(map);
        }

        return result;
    }
}
 