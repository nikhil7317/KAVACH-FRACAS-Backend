package com.railbit.tcasanalysis.service.alertManagementService;

import com.railbit.tcasanalysis.repository.alertManagementRepo.AlertCardsCountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AlertCardsCountService {

    @Autowired
    private AlertCardsCountRepository repo;

    public List<Map<String, Object>> getTodayAlertCards() {

        long total = Optional.ofNullable(repo.countAlertsToday()).orElse(0L);
        long critical = Optional.ofNullable(repo.countCriticalAlertsToday()).orElse(0L);
        long open = Optional.ofNullable(repo.countOpenTicketsToday()).orElse(0L);
        long closed = Optional.ofNullable(repo.countClosedTicketsToday()).orElse(0L);

        List<Map<String, Object>> result = new ArrayList<>();

        result.add(card("Total Incidents", total));
        result.add(card("Critical Alerts", critical));
        result.add(card("Open Unique Tickets", open));
        result.add(card("Closed Unique Tickets", closed));

        return result;
    }

    // ✅ SAFE helper (NO Map.of)
    private Map<String, Object> card(String label, long value) {
        Map<String, Object> map = new HashMap<>();
        map.put("label", label != null ? label : "");
        map.put("value", value);
        return map;
    }
}