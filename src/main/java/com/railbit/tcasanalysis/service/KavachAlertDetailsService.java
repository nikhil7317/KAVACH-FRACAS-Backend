package com.railbit.tcasanalysis.service;

import com.railbit.tcasanalysis.entity.KavachAlertDetails;
import com.railbit.tcasanalysis.entity.IncidentTrack;
import com.railbit.tcasanalysis.repository.KavachAlertDetailsRepository;
import com.railbit.tcasanalysis.repository.IncidentTrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class KavachAlertDetailsService {

    @Autowired
    private KavachAlertDetailsRepository detailsRepository;

    @Autowired
    private IncidentTrackRepository incidentTrackRepository;

    public void save(KavachAlertDetails details) {
        KavachAlertDetails saved = detailsRepository.save(details);

        // Create first incident track entry
        IncidentTrack track = new IncidentTrack();
        track.setKavachAlertDetails(saved);
        track.setCreatedUser(saved.getCreatedUser());
        track.setIncidentCreatedAt(saved.getIncidentCreatedAt());
        track.setTicketStatus(saved.getTicketStatus());
        track.setTicketRemarks("Incident created");

        incidentTrackRepository.save(track);
    }

    public void update(Long id, KavachAlertDetails details) {
        details.setId(id);
        KavachAlertDetails updated = detailsRepository.save(details);

        // Create incident track for update
        IncidentTrack track = new IncidentTrack();
        track.setKavachAlertDetails(updated);
        track.setCreatedUser(updated.getCreatedUser());
        track.setIncidentCreatedAt(LocalDateTime.now());
        track.setTicketStatus(updated.getTicketStatus());
        track.setTicketRemarks("Incident updated");

        incidentTrackRepository.save(track);
    }

    public KavachAlertDetails findById(Long id) {
        return detailsRepository.findById(id).orElse(null);
    }

    public KavachAlertDetails findByKavachAlertId(Long kavachAlertId) {
        return detailsRepository.findByKavachAlertId(kavachAlertId).orElse(null);
    }

    public void saveIncidentTrack(IncidentTrack track) {
        incidentTrackRepository.save(track);
    }

    public List<IncidentTrack> findIncidentTrackByKavachAlertDetailsId(Long kavachAlertDetailsId) {
        return incidentTrackRepository.findByKavachAlertDetailsIdOrderByIncidentCreatedAtDesc(kavachAlertDetailsId);
    }
}