package com.railbit.tcasanalysis.service.alertManagementService;

import com.railbit.tcasanalysis.repository.alertManagementRepo.AlertCardsCountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AlertCardsCountService {

    @Autowired
    private AlertCardsCountRepository repo;

    public List<Map<String, Object>> getTodayAlertCards() {

        long total = repo.countAlertsToday();
        long critical = repo.countCriticalAlertsToday();



        long open = repo.countOpenTicketsToday();
        long closed = repo.countClosedTicketsToday();


        return List.of(
                card("Total Incidents", total),
                card("Critical Alerts", critical),
                card("Open Unique Tickets", open),
                card("Closed Unique Tickets", closed)
        );
    }

    private Map<String, Object> card(String label, long value) {
        return Map.of(
                "label", label,
                "value", value
        );
    }
}