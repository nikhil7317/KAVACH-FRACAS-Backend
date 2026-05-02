package com.railbit.tcasanalysis.service;

import com.railbit.tcasanalysis.DTO.IncidentTrackDTO;
import com.railbit.tcasanalysis.DTO.KavachAlertDetailsRequest;
import com.railbit.tcasanalysis.DTO.KavachAlertDetailsResponseDTO;
import com.railbit.tcasanalysis.entity.KavachAlert;
import com.railbit.tcasanalysis.entity.KavachAlertDetails;
import com.railbit.tcasanalysis.entity.IncidentTrack;
import com.railbit.tcasanalysis.entity.User;
import com.railbit.tcasanalysis.repository.KavachAlertDetailsRepository;
import com.railbit.tcasanalysis.repository.IncidentTrackRepository;
import com.railbit.tcasanalysis.repository.KavachAlertRepository;
import com.railbit.tcasanalysis.repository.OemRemarksRepository;
import com.railbit.tcasanalysis.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
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
    private OemRemarksRepository oemRemarksRepository;

    @Autowired
    private UserRepo userRepository;

    // ─── POST /alertDetails ───────────────────────────────────────────────────

    public void save(KavachAlertDetailsRequest request) {
        KavachAlertDetails details = new KavachAlertDetails();

        details.setKavachAlert(
                kavachAlertRepository.findById(request.getKavachAlertId())
                        .orElseThrow(() -> new RuntimeException("KavachAlert not found"))
        );
        details.setCreatedUser(
                userRepository.findById(request.getCreatedUserId())
                        .orElseThrow(() -> new RuntimeException("Created user not found"))
        );
        details.setAssignedTo(
                userRepository.findById(request.getAssignedToId())
                        .orElseThrow(() -> new RuntimeException("AssignedTo user not found"))
        );
        details.setIncidentCreatedAt(request.getIncidentCreatedAt());
        details.setTicketNo(request.getTicketNo());
        details.setTicketStatus(request.getTicketStatus());

        KavachAlertDetails saved = detailsRepository.save(details);

        IncidentTrack track = new IncidentTrack();
        track.setKavachAlertDetails(saved);
        track.setCreatedUser(saved.getCreatedUser());
        track.setIncidentCreatedAt(saved.getIncidentCreatedAt());
        track.setTicketStatus(saved.getTicketStatus());
        track.setTicketRemarks(request.getTicketRemarks());
        incidentTrackRepository.save(track);
    }

    // ─── PUT /alertDetails/{id} ───────────────────────────────────────────────

    public void update(Long id, KavachAlertDetailsRequest request) {
        KavachAlertDetails details = detailsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alert details not found: " + id));

        details.setAssignedTo(
                userRepository.findById(request.getAssignedToId())
                        .orElseThrow(() -> new RuntimeException("AssignedTo user not found"))
        );
        details.setTicketStatus(request.getTicketStatus());

        KavachAlertDetails updated = detailsRepository.save(details);

        IncidentTrack track = new IncidentTrack();
        track.setKavachAlertDetails(updated);
        track.setCreatedUser(
                userRepository.findById(request.getCreatedUserId())
                        .orElseThrow(() -> new RuntimeException("Created user not found"))
        );
        track.setIncidentCreatedAt(LocalDateTime.now());
        track.setTicketStatus(request.getTicketStatus());
        track.setTicketRemarks(request.getTicketRemarks());
        incidentTrackRepository.save(track);
    }

    // ─── GET /alertDetails/{kavachAlertId} ───────────────────────────────────

    public KavachAlertDetailsResponseDTO getAlertDetailsWithTracks(Long kavachAlertId) {
        KavachAlertDetailsResponseDTO response = new KavachAlertDetailsResponseDTO();

        KavachAlertDetails details = detailsRepository.findByKavachAlertId(kavachAlertId);

        if (details == null) {
            // Fetch just the KavachAlert and return a partial response
            KavachAlert kavachAlert = kavachAlertRepository.findById(kavachAlertId)
                    .orElseThrow(() -> new RuntimeException(
                            "No alert found for kavach_alert_id: " + kavachAlertId));

            response.setKavachAlert(kavachAlert);
            response.setIncidentTracks(Collections.emptyList());
            response.setOemRemarksSubmitted(false);
            return response;
        }

        // --- existing logic below, unchanged ---
        List<IncidentTrack> tracks = incidentTrackRepository
                .findByKavachAlertDetailsIdOrderByIncidentCreatedAtDesc(details.getId());

        response.setId(details.getId());

        // FIX: Populate transient fields on KavachAlert before setting it
        KavachAlert kavachAlert = details.getKavachAlert();
        kavachAlert.setTicketNo(details.getTicketNo());
        kavachAlert.setTicketStatus(details.getTicketStatus());
        response.setKavachAlert(kavachAlert);

        response.setCreatedUser(details.getCreatedUser());
        response.setAssignedTo(details.getAssignedTo());
        response.setTicketNo(details.getTicketNo());
        response.setTicketStatus(details.getTicketStatus());
        response.setIncidentCreatedAt(details.getIncidentCreatedAt());

        boolean hasOemRemarks = oemRemarksRepository
                .existsByKavachAlertDetailsId(details.getId());
        response.setOemRemarksSubmitted(hasOemRemarks);

        response.setIncidentTracks(
                tracks.stream().map(this::mapToIncidentTrackDTO).collect(Collectors.toList())
        );

        return response;
    }

    // ─── Backward compat ─────────────────────────────────────────────────────

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

    // ─── Mapper ───────────────────────────────────────────────────────────────

    private IncidentTrackDTO mapToIncidentTrackDTO(IncidentTrack track) {
        IncidentTrackDTO dto = new IncidentTrackDTO();
        dto.setId(track.getId());
        dto.setIncidentCreatedAt(track.getIncidentCreatedAt());
        dto.setTicketRemarks(track.getTicketRemarks());
        dto.setTicketStatus(track.getTicketStatus());
        if (track.getCreatedUser() != null) {
            dto.setCreatedUser(mapToUserSummary(track.getCreatedUser()));
        }
        return dto;
    }

    public void saveIncidentTrack(IncidentTrack track) {
        // Resolve createdUser from DB using the id sent in payload
        // so designation is always correct — never trust client-side designation data
        if (track.getCreatedUser() != null && track.getCreatedUser().getId() != null) {
            User user = userRepository.findById(track.getCreatedUser().getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            track.setCreatedUser(user);  // full user with real designation from DB
        }
        // Remove ticketStatus enforcement — OEM check is purely by designation
        incidentTrackRepository.save(track);
    }

    private IncidentTrackDTO.UserSummaryDTO mapToUserSummary(User user) {
        IncidentTrackDTO.UserSummaryDTO userDTO = new IncidentTrackDTO.UserSummaryDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        if (user.getDesignation() != null) {
            IncidentTrackDTO.UserSummaryDTO.DesignationSummaryDTO desig =
                    new IncidentTrackDTO.UserSummaryDTO.DesignationSummaryDTO();
            desig.setId(user.getDesignation().getId());
            desig.setName(user.getDesignation().getName());
            desig.setTitle(user.getDesignation().getTitle());
            userDTO.setDesignation(desig);
        }
        return userDTO;
    }
}