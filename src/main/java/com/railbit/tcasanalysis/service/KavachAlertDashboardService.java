package com.railbit.tcasanalysis.service;

import com.railbit.tcasanalysis.repository.KavachAlertDashboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    public List<Map<String, Object>> getDashboardCountCards(Date fromDate, Date toDate) {
        Date adjustedTo = endOfDay(toDate);

        long total = repo.countAlertsInRange(fromDate, adjustedTo);

        // Ticket counts — replace these queries if ticket data is in another table
        long withTicket    = repo.countAlertsWithTicketInRange(fromDate, adjustedTo);
        long withoutTicket = total - withTicket;
        long openTickets   = repo.countOpenTicketsInRange(fromDate, adjustedTo);
        long closedTickets = repo.countClosedTicketsInRange(fromDate, adjustedTo);
        long uniqueTickets = openTickets + closedTickets;

        return List.of(
                card("Total Incidents",                       total),
                card("Incidents Without Attached Ticket",     withoutTicket),
                card("Incidents With Attached Ticket",        withTicket),
                card("Total Unique Tickets Generated",        uniqueTickets),
                card("Open Unique Tickets",                   openTickets),
                card("Closed Unique Tickets",                 closedTickets)
        );
    }

    // ── 3. Loco-wise counts ────────────────────────────────────────────────────

    public List<Map<String, Object>> getAlertsLocoWise(Date fromDate, Date toDate) {
        List<Object[]> rows = repo.countByLocoId(fromDate, endOfDay(toDate));
        List<Map<String, Object>> result = new ArrayList<>();
        int colourIdx = 0;
        for (Object[] row : rows) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name",      String.valueOf(row[0]));          // locoId as string
            item.put("count",     ((Number) row[1]).longValue());
            item.put("colorCode", PALETTE.get(colourIdx % PALETTE.size()));
            result.add(item);
            colourIdx++;
        }
        return result;
    }

    // ── 4. Category-wise counts ────────────────────────────────────────────────

    public List<Map<String, Object>> getAlertsCategoryWise(Date fromDate, Date toDate) {
        List<Object[]> rows = repo.countByAlertCategory(fromDate, endOfDay(toDate));
        List<Map<String, Object>> result = new ArrayList<>();
        int colourIdx = 0;
        for (Object[] row : rows) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name",      (String) row[0]);
            item.put("count",     ((Number) row[1]).longValue());
            item.put("colorCode", PALETTE.get(colourIdx % PALETTE.size()));
            result.add(item);
            colourIdx++;
        }
        return result;
    }

    // ── 5. Station-wise counts ─────────────────────────────────────────────────

    public List<Map<String, Object>> getAlertsStationWise(Date fromDate, Date toDate) {
        List<Object[]> rows = repo.countByStationId(fromDate, endOfDay(toDate));
        List<Map<String, Object>> result = new ArrayList<>();
        int colourIdx = 0;
        for (Object[] row : rows) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name",      String.valueOf(row[0]));          // stationId as string
            item.put("count",     ((Number) row[1]).longValue());
            item.put("colorCode", PALETTE.get(colourIdx % PALETTE.size()));
            result.add(item);
            colourIdx++;
        }
        return result;
    }

    // ── 6. Category-wise yearly (12-month) graph data ─────────────────────────

    public List<Map<String, Object>> getCategoryWiseYearlyGraphData() {
        // Go back 12 months from the start of the current month
        LocalDate firstDayOf12MonthsAgo = LocalDate.now()
                .minusMonths(11)
                .withDayOfMonth(1);
        Date fromDate = java.sql.Date.valueOf(firstDayOf12MonthsAgo);

        List<Object[]> rows = repo.countByCategoryMonthly(fromDate);

        // Build a sorted map: "May 2025" → { categoryName → count }
        // Use LinkedHashMap to preserve chronological order
        Map<String, Map<String, Long>> monthMap = new LinkedHashMap<>();

        // Pre-populate all 12 months so months with zero data still appear
        for (int i = 11; i >= 0; i--) {
            YearMonth ym = YearMonth.now().minusMonths(i);
            String label = ym.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                    + " " + ym.getYear();
            monthMap.putIfAbsent(label, new LinkedHashMap<>());
        }

        // Collect all unique category names for consistent colours
        Set<String> allCategories = new LinkedHashSet<>();
        for (Object[] row : rows) {
            allCategories.add((String) row[2]);
        }

        // Assign a fixed colour per category
        Map<String, String> categoryColour = new LinkedHashMap<>();
        int ci = 0;
        for (String cat : allCategories) {
            categoryColour.put(cat, PALETTE.get(ci % PALETTE.size()));
            ci++;
        }

        // Fill month → category → count
        for (Object[] row : rows) {
            int year     = ((Number) row[0]).intValue();
            int month    = ((Number) row[1]).intValue();
            String cat   = (String) row[2];
            long  count  = ((Number) row[3]).longValue();

            String label = Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                    + " " + year;
            monthMap.computeIfAbsent(label, k -> new LinkedHashMap<>())
                    .put(cat, count);
        }

        // Build final response list
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Map<String, Long>> entry : monthMap.entrySet()) {
            Map<String, Object> monthEntry = new LinkedHashMap<>();
            monthEntry.put("month", entry.getKey());

            List<Map<String, Object>> barList = new ArrayList<>();
            for (String cat : allCategories) {
                long count = entry.getValue().getOrDefault(cat, 0L);
                Map<String, Object> bar = new LinkedHashMap<>();
                bar.put("name",      cat);
                bar.put("value",     (double) count);
                bar.put("label",     String.valueOf(count));
                bar.put("colorCode", categoryColour.get(cat));
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
}