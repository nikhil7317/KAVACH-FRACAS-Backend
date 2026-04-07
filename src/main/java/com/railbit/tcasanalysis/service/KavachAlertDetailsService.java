package com.railbit.tcasanalysis.service;

import com.railbit.tcasanalysis.DTO.IncidentTrackDTO;
import com.railbit.tcasanalysis.DTO.KavachAlertDetailsRequest;
import com.railbit.tcasanalysis.DTO.KavachAlertDetailsResponseDTO;
import com.railbit.tcasanalysis.entity.KavachAlertDetails;
import com.railbit.tcasanalysis.entity.IncidentTrack;
import com.railbit.tcasanalysis.repository.KavachAlertDetailsRepository;
import com.railbit.tcasanalysis.repository.IncidentTrackRepository;
import com.railbit.tcasanalysis.repository.KavachAlertRepository;
import com.railbit.tcasanalysis.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KavachAlertDetailsService {

    @Autowired
    private KavachAlertDetailsRepository detailsRepository;

    @Autowired
    private IncidentTrackRepository incidentTrackRepository;

    @Autowired
    private KavachAlertRepository kavachAlertRepository;

    @Autowired
    private UserRepo userRepository;


    public void save(KavachAlertDetailsRequest request) {

        KavachAlertDetails details = new KavachAlertDetails();

        // Fetch entity by ID, then set it
        details.setKavachAlert(
                kavachAlertRepository.findById(request.getKavachAlertId())
                        .orElseThrow(() -> new RuntimeException("KavachAlert not found"))
        );
        details.setCreatedUser(
                userRepository.findById(request.getCreatedUserId())
                        .orElseThrow(() -> new RuntimeException("User not found"))
        );
        details.setAssignedTo(
                userRepository.findById(request.getAssignedToId())
                        .orElseThrow(() -> new RuntimeException("AssignedTo User not found"))
        );

        details.setIncidentCreatedAt(request.getIncidentCreatedAt());
        details.setTicketNo(request.getTicketNo());

        // FIX: Set ticketStatus from request - THIS WAS MISSING
        details.setTicketStatus(request.getTicketStatus());

        KavachAlertDetails saved = detailsRepository.save(details);

        // Remarks go ONLY into incident_track
        IncidentTrack track = new IncidentTrack();
        track.setKavachAlertDetails(saved);
        track.setCreatedUser(saved.getCreatedUser());
        track.setIncidentCreatedAt(saved.getIncidentCreatedAt());
        track.setTicketStatus(saved.getTicketStatus());  // Now this will have the value
        track.setTicketRemarks(request.getTicketRemarks());

        incidentTrackRepository.save(track);
    }

    public void update(Long id, KavachAlertDetailsRequest request) {
        KavachAlertDetails details = detailsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alert details not found with id: " + id));

        // Update only the fields that can change
        details.setAssignedTo(
                userRepository.findById(request.getAssignedToId())
                        .orElseThrow(() -> new RuntimeException("AssignedTo User not found"))
        );
        details.setTicketStatus(request.getTicketStatus());
        // Keep existing: kavachAlert, createdUser, incidentCreatedAt, ticketNo

        KavachAlertDetails updated = detailsRepository.save(details);

        // Add new track record for this update
        IncidentTrack track = new IncidentTrack();
        track.setKavachAlertDetails(updated);
        track.setCreatedUser(
                userRepository.findById(request.getCreatedUserId())
                        .orElseThrow(() -> new RuntimeException("User not found"))
        );
        track.setIncidentCreatedAt(LocalDateTime.now());
        track.setTicketStatus(request.getTicketStatus());
        track.setTicketRemarks(request.getTicketRemarks());

        incidentTrackRepository.save(track);
    }
    // Keep for backward compatibility if needed
    public KavachAlertDetails findById(Long id) {
        KavachAlertDetails details = detailsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
        List<IncidentTrack> tracks = incidentTrackRepository
                .findByKavachAlertDetailsIdOrderByIncidentCreatedAtDesc(id);
        details.setTicketRemarks(
                tracks.isEmpty() ? null : tracks.get(0).getTicketRemarks()
        );
        return details;
    }

    public KavachAlertDetailsResponseDTO getAlertDetailsWithTracks(Long kavachAlertId) {
        KavachAlertDetails details = detailsRepository.findByKavachAlertId(kavachAlertId);

        if (details == null) {
            throw new RuntimeException("Alert details not found for kavach_alert_id: " + kavachAlertId);
        }

        List<IncidentTrack> tracks = incidentTrackRepository
                .findByKavachAlertDetailsIdOrderByIncidentCreatedAtDesc(details.getId());

        KavachAlertDetailsResponseDTO response = new KavachAlertDetailsResponseDTO();

        response.setId(details.getId());
        response.setKavachAlert(details.getKavachAlert());   // parent
        response.setCreatedUser(details.getCreatedUser());
        response.setTicketNo(details.getTicketNo());

        List<IncidentTrackDTO> trackDTOs = tracks.stream()
                .map(this::mapToIncidentTrackDTO)
                .collect(Collectors.toList());

        response.setIncidentTracks(trackDTOs);   // all child records

        return response;
    }

    public void saveIncidentTrack(IncidentTrack track) {
        incidentTrackRepository.save(track);
    }

    private IncidentTrackDTO mapToIncidentTrackDTO(IncidentTrack track) {
        IncidentTrackDTO dto = new IncidentTrackDTO();
        dto.setId(track.getId());
        dto.setIncidentCreatedAt(track.getIncidentCreatedAt());
        dto.setTicketRemarks(track.getTicketRemarks());
        dto.setTicketStatus(track.getTicketStatus());
        return dto;
    }
}