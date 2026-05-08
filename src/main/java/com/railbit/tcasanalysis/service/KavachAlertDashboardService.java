package com.railbit.tcasanalysis.service;

import com.railbit.tcasanalysis.repository.KavachAlertDashboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;

@Service
@RequiredArgsConstructor
public class KavachAlertDashboardService {

    private final KavachAlertDashboardRepository repo;

    // ── Severity keys always present in output ─────────────────────────────────
    private static final List<String> SEVERITY_KEYS = List.of("CRITICAL", "WARNING", "MEDIUM", "INFO");

    private static final List<String> PALETTE = List.of(
            "#ff6384", "#36a2eb", "#ffce56", "#4bc0c0",
            "#9966ff", "#ff9f40", "#73937E", "#C4DACF",
            "#ACE4AA", "#DCCFEC", "#948392", "#7FB7BE",
            "#F4F1DE", "#E07A5F", "#3D405B", "#81B29A"
    );

    // ── 1. Last alert date ─────────────────────────────────────────────────────

    public String getLastAlertDate() {
        Date lastDate = repo.findLastAlertDate();
        if (lastDate == null) return null;
        return new SimpleDateFormat("yyyy-MM-dd").format(lastDate);
    }

    // ── 2. Dashboard count cards ───────────────────────────────────────────────

    /**
     * All counts are derived purely from the kavach_alert table.
     * "Ticket" columns use the @Transient ticketNo / ticketStatus fields —
     * if your ticket data lives in a separate table, wire in that repo here.
     */
    public List<Map<String, Object>> getDashboardCountCards(Date fromDate, Date toDate, Integer divisionId) {
        Date adjustedTo = endOfDay(toDate);

        long total         = repo.countAlertsInRange(fromDate, adjustedTo);
        long criticalAlerts = repo.countCriticalAlertsInRange(fromDate, adjustedTo);

        // Single query for all ticket stats
        List<Object[]> ticketStatRows = repo.getTicketStatsSingleQuery(fromDate, adjustedTo);
        Object[] ticketStats = (ticketStatRows != null && !ticketStatRows.isEmpty())
                ? ticketStatRows.get(0)
                : new Object[]{0L, 0L, 0L};

        long uniqueTickets = ticketStats[0] != null ? ((Number) ticketStats[0]).longValue() : 0L;
        long openTickets   = ticketStats[1] != null ? ((Number) ticketStats[1]).longValue() : 0L;
        long closedTickets = ticketStats[2] != null ? ((Number) ticketStats[2]).longValue() : 0L;

        return List.of(
                card("Total Incidents",    total),
                card("Critical Alerts",    criticalAlerts),
                card("Tickets Generated",  uniqueTickets),   // now counts ALL statuses (OPEN, CLOSE, RE-ASSIGN, etc.)
                card("Open Tickets",       openTickets),
                card("Closed Tickets",     closedTickets),
                card("Loco Movement",      0L)               // placeholder — logic TBD
        );
    }

    // ── 3. Loco-wise counts ────────────────────────────────────────────────────

    public List<Map<String, Object>> getAlertsLocoWise(
            Date fromDate,
            Date toDate,
            Integer divisionId) {

        Date to = endOfDay(toDate);

        List<Object[]> rows =
                repo.countByLocoId(fromDate, to);

        Map<String, Map<String, Long>> severityPivot =
                buildSeverityPivot(
                        repo.countByLocoIdAndSeverity(fromDate, to));

        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] row : rows) {

            String locoId = String.valueOf(row[0]);

            Long total = ((Number) row[1]).longValue();

            Map<String, Long> severity =
                    severityPivot.getOrDefault(
                            locoId,
                            defaultSeverityMap());

            Map<String, Object> item = new LinkedHashMap<>();

            item.put("locoId", locoId);

            item.put("CRITICAL",
                    severity.getOrDefault("CRITICAL", 0L));

            item.put("WARNING",
                    severity.getOrDefault("WARNING", 0L));

            item.put("MEDIUM",
                    severity.getOrDefault("MEDIUM", 0L));

            item.put("INFO",
                    severity.getOrDefault("INFO", 0L));

            item.put("TOTAL", total);

            result.add(item);
        }

        return result;
    }

    // ── 4. Category-wise counts ────────────────────────────────────────────────

    public List<Map<String, Object>> getAlertsCategoryWise(Date fromDate, Date toDate ,Integer divisionId) {
        Date to = endOfDay(toDate);
        List<Object[]> rows         = repo.countByAlertCategory(fromDate, to);
        Map<String, Map<String, Long>> severityPivot =
                buildSeverityPivot(repo.countByAlertCategoryAndSeverity(fromDate, to));

        List<Map<String, Object>> result = new ArrayList<>();
        int colourIdx = 0;
        for (Object[] row : rows) {
            String key = (String) row[0];
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name",      key);
            item.put("count",     ((Number) row[1]).longValue());
            item.put("colorCode", PALETTE.get(colourIdx % PALETTE.size()));
            item.put("severity",  severityPivot.getOrDefault(key, defaultSeverityMap()));
            result.add(item);
            colourIdx++;
        }
        return result;
    }

    // ── 5. Station-wise counts ─────────────────────────────────────────────────

    public List<Map<String, Object>> getAlertsStationWise(Date fromDate, Date toDate,Integer divisionId) {
        Date to = endOfDay(toDate);
        List<Object[]> rows         = repo.countByStationId(fromDate, to);
        Map<String, Map<String, Long>> severityPivot =
                buildSeverityPivot(repo.countByStationIdAndSeverity(fromDate, to));

        List<Map<String, Object>> result = new ArrayList<>();
        int colourIdx = 0;
        for (Object[] row : rows) {
            String key = String.valueOf(row[0]);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name",      key);
            item.put("count",     ((Number) row[1]).longValue());
            item.put("colorCode", PALETTE.get(colourIdx % PALETTE.size()));
            item.put("severity",  severityPivot.getOrDefault(key, defaultSeverityMap()));
            result.add(item);
            colourIdx++;
        }
        return result;
    }

    // ── 6. Category-wise yearly (12-month) graph data ─────────────────────────

    public List<Map<String, Object>> getCategoryWiseYearlyGraphData(
            Integer divisionId) {

        LocalDate firstDayOf12MonthsAgo =
                LocalDate.now().minusMonths(11).withDayOfMonth(1);

        Date fromDate = java.sql.Date.valueOf(firstDayOf12MonthsAgo);

        List<Object[]> rows =
                repo.countByCategoryMonthly(fromDate);

        List<Object[]> severityRows =
                repo.countByCategoryAndSeverityMonthly(fromDate);

        // Build severity pivot
        Map<String, Map<String, Long>> severityPivot =
                new LinkedHashMap<>();

        for (Object[] row : severityRows) {

            int year = ((Number) row[0]).intValue();
            int month = ((Number) row[1]).intValue();

            String cat = (String) row[2];

            String severity =
                    row[3] != null
                            ? ((String) row[3]).toUpperCase()
                            : "UNKNOWN";

            long count = ((Number) row[4]).longValue();

            String label =
                    Month.of(month)
                            .getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                            + " " + year;

            String pivotKey = label + "||" + cat;

            severityPivot.computeIfAbsent(pivotKey, k -> {
                Map<String, Long> m = new LinkedHashMap<>();
                SEVERITY_KEYS.forEach(s -> m.put(s, 0L));
                return m;
            }).merge(severity, count, Long::sum);
        }

        Map<String, Map<String, Long>> monthMap = new LinkedHashMap<>();

        for (int i = 11; i >= 0; i--) {

            YearMonth ym = YearMonth.now().minusMonths(i);

            String label =
                    ym.getMonth().getDisplayName(
                            TextStyle.FULL,
                            Locale.ENGLISH
                    ) + " " + ym.getYear();

            monthMap.putIfAbsent(label, new LinkedHashMap<>());
        }

        Set<String> allCategories = new LinkedHashSet<>();

        for (Object[] row : rows) {
            allCategories.add((String) row[2]);
        }

        Map<String, String> categoryColour = new LinkedHashMap<>();

        int ci = 0;

        for (String cat : allCategories) {
            categoryColour.put(
                    cat,
                    PALETTE.get(ci % PALETTE.size())
            );
            ci++;
        }

        for (Object[] row : rows) {

            int year = ((Number) row[0]).intValue();
            int month = ((Number) row[1]).intValue();

            String cat = (String) row[2];

            long count = ((Number) row[3]).longValue();

            String label =
                    Month.of(month)
                            .getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                            + " " + year;

            monthMap.computeIfAbsent(
                    label,
                    k -> new LinkedHashMap<>()
            ).put(cat, count);
        }

        List<Map<String, Object>> result = new ArrayList<>();

        for (Map.Entry<String, Map<String, Long>> entry : monthMap.entrySet()) {

            String monthLabel = entry.getKey();

            Map<String, Object> monthEntry = new LinkedHashMap<>();

            monthEntry.put("month", monthLabel);

            List<Map<String, Object>> barList = new ArrayList<>();

            for (String cat : allCategories) {

                long count =
                        entry.getValue().getOrDefault(cat, 0L);

                String pivotKey = monthLabel + "||" + cat;

                Map<String, Object> bar = new LinkedHashMap<>();

                bar.put("name", cat);
                bar.put("value", (double) count);
                bar.put("label", String.valueOf(count));
                bar.put("colorCode", categoryColour.get(cat));

                bar.put(
                        "severity",
                        severityPivot.getOrDefault(
                                pivotKey,
                                defaultSeverityMap()
                        )
                );

                barList.add(bar);
            }

            monthEntry.put("barGraphDataSetList", barList);

            result.add(monthEntry);
        }

        return result;
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    /** Sets time to 23:59:59.999 so the range includes the full end day. */
    private Date endOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE,      59);
        cal.set(Calendar.SECOND,      59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    private Map<String, Object> card(String name, long count) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("name",      name);
        m.put("count",     count);
        m.put("colorCode", null);
        return m;
    }



    /**
     * Builds a pivot map: groupKey → { "CRITICAL" → n, "WARNING" → n, ... }
     * Each Object[] row is expected as: [groupKey, severity, count]
     */
    private Map<String, Map<String, Long>> buildSeverityPivot(List<Object[]> rows) {
        Map<String, Map<String, Long>> pivot = new LinkedHashMap<>();
        for (Object[] row : rows) {
            String groupKey  = String.valueOf(row[0]);
            String severity  = row[1] != null ? ((String) row[1]).toUpperCase() : "UNKNOWN";
            long   count     = ((Number) row[2]).longValue();

            pivot.computeIfAbsent(groupKey, k -> {
                // Pre-populate all known severity levels with 0
                Map<String, Long> m = new LinkedHashMap<>();
                SEVERITY_KEYS.forEach(s -> m.put(s, 0L));
                return m;
            }).merge(severity, count, Long::sum);
        }
        return pivot;
    }
    private Map<String, Long> defaultSeverityMap() {
        Map<String, Long> m = new LinkedHashMap<>();
        SEVERITY_KEYS.forEach(s -> m.put(s, 0L));
        return m;
    }
}