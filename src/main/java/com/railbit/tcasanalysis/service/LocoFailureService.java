package com.railbit.tcasanalysis.service;

import com.railbit.tcasanalysis.DTO.LocoFailureListDTO;
import com.railbit.tcasanalysis.DTO.LocoFailureResponseDTO;
import com.railbit.tcasanalysis.entity.LocoFailure;
import com.railbit.tcasanalysis.entity.LocoFailureTrack;
import com.railbit.tcasanalysis.entity.User;
import com.railbit.tcasanalysis.repository.LocoFailureRepository;
import com.railbit.tcasanalysis.repository.LocoFailureTrackRepository;
import com.railbit.tcasanalysis.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocoFailureService {

    private final LocoFailureRepository locoFailureRepository;
    private final LocoFailureTrackRepository locoFailureTrackRepository;
    private final UserRepo userRepository;

    @Transactional
    public void save(Map<String, Object> request) {
        LocoFailure locoFailure = new LocoFailure();

        locoFailure.setLocoId((Integer) request.get("locoId"));
        locoFailure.setCreatedUser(
                userRepository.findById(((Number) request.get("createdUserId")).longValue())
                        .orElseThrow(() -> new RuntimeException("Created user not found"))
        );
        locoFailure.setAssignedTo(
                userRepository.findById(((Number) request.get("assignedToId")).longValue())
                        .orElseThrow(() -> new RuntimeException("AssignedTo user not found"))
        );
        locoFailure.setTicketNo(request.get("ticketNo") != null ? (String) request.get("ticketNo") : generateTicketNo());
        locoFailure.setTicketStatus(request.get("ticketStatus") != null ? (String) request.get("ticketStatus") : "OPEN");
        locoFailure.setSeverity((String) request.get("severity"));

        LocoFailure saved = locoFailureRepository.save(locoFailure);

        LocoFailureTrack track = new LocoFailureTrack();
        track.setLocoFailure(saved);
        track.setCreatedUser(saved.getCreatedUser());
        track.setTicketStatus(saved.getTicketStatus());
        track.setTicketRemarks((String) request.get("ticketRemarks"));
        locoFailureTrackRepository.save(track);
    }

    @Transactional(readOnly = true)
    public LocoFailureResponseDTO getLocoFailureWithTracks(Long locoFailureId) {
        LocoFailure locoFailure = locoFailureRepository.findById(locoFailureId)
                .orElseThrow(() -> new RuntimeException("No loco failure found for id: " + locoFailureId));

        List<LocoFailureTrack> tracks = locoFailureTrackRepository
                .findByLocoFailureIdOrderByIncidentCreatedAtDesc(locoFailureId);

        LocoFailureResponseDTO response = new LocoFailureResponseDTO();
        response.setId(locoFailure.getId());
        response.setLocoId(locoFailure.getLocoId());
        response.setIncidentCreatedAt(locoFailure.getIncidentCreatedAt());
        response.setTicketNo(locoFailure.getTicketNo());
        response.setTicketStatus(locoFailure.getTicketStatus());
        response.setSeverity(locoFailure.getSeverity());
        response.setIsLocoFailureNotifiedApp(locoFailure.getIsLocoFailureNotifiedApp());
        response.setIsLocoFailureNotifiedWeb(locoFailure.getIsLocoFailureNotifiedWeb());

        response.setCreatedUser(mapUser(locoFailure.getCreatedUser()));
        response.setAssignedTo(mapUser(locoFailure.getAssignedTo()));

        response.setLocoFailureTracks(tracks.stream()
                .map(this::mapTrack)
                .collect(Collectors.toList()));

        return response;
    }

    private LocoFailureResponseDTO.UserSummaryDTO mapUser(User user) {
        if (user == null) return null;
        LocoFailureResponseDTO.UserSummaryDTO dto = new LocoFailureResponseDTO.UserSummaryDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        if (user.getDesignation() != null) {
            LocoFailureResponseDTO.DesignationSummaryDTO desig = new LocoFailureResponseDTO.DesignationSummaryDTO();
            desig.setId(user.getDesignation().getId().longValue());
            desig.setName(user.getDesignation().getName());
            desig.setTitle(user.getDesignation().getTitle());
            dto.setDesignation(desig);
        }
        return dto;
    }

    private LocoFailureResponseDTO.LocoFailureTrackDTO mapTrack(LocoFailureTrack track) {
        LocoFailureResponseDTO.LocoFailureTrackDTO dto = new LocoFailureResponseDTO.LocoFailureTrackDTO();
        dto.setId(track.getId());
        dto.setTicketRemarks(track.getTicketRemarks());
        dto.setTicketStatus(track.getTicketStatus());
        dto.setCreatedUser(mapUser(track.getCreatedUser()));
        dto.setIncidentCreatedAt(track.getIncidentCreatedAt());
        return dto;
    }

    public Page<LocoFailureListDTO> getAllLocoFailures(Integer locoId, String fromDate, String toDate,
                                                       String severity, String ticketStatus, String ticketNo,
                                                       int page, int size) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime fromDateTime = null;
        LocalDateTime toDateTime = null;

        if (fromDate != null && !fromDate.isEmpty()) {
            fromDateTime = LocalDateTime.parse(fromDate, formatter);
        }
        if (toDate != null && !toDate.isEmpty()) {
            toDateTime = LocalDateTime.parse(toDate, formatter);
        }

        Page<LocoFailure> entityPage = locoFailureRepository.findAllWithFilters(
                locoId, fromDateTime, toDateTime, severity, ticketStatus, ticketNo,
                PageRequest.of(page, size));

        return entityPage.map(this::mapToListDTO);
    }

    private LocoFailureListDTO mapToListDTO(LocoFailure lf) {
        LocoFailureListDTO dto = new LocoFailureListDTO();
        dto.setId(lf.getId());
        dto.setLocoId(lf.getLocoId());
        dto.setIncidentCreatedAt(lf.getIncidentCreatedAt());
        dto.setTicketNo(lf.getTicketNo());
        dto.setTicketStatus(lf.getTicketStatus());
        dto.setSeverity(lf.getSeverity());
        dto.setIsLocoFailureNotifiedApp(lf.getIsLocoFailureNotifiedApp());
        dto.setIsLocoFailureNotifiedWeb(lf.getIsLocoFailureNotifiedWeb());
        dto.setCreatedUser(mapUserSummary(lf.getCreatedUser()));
        dto.setAssignedTo(mapUserSummary(lf.getAssignedTo()));
        return dto;
    }

    private LocoFailureListDTO.UserSummaryDTO mapUserSummary(User user) {
        if (user == null) return null;
        LocoFailureListDTO.UserSummaryDTO dto = new LocoFailureListDTO.UserSummaryDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        if (user.getDesignation() != null) {
            LocoFailureListDTO.DesignationSummaryDTO desig = new LocoFailureListDTO.DesignationSummaryDTO();
            desig.setId(user.getDesignation().getId().longValue());
            desig.setName(user.getDesignation().getName());
            desig.setTitle(user.getDesignation().getTitle());
            dto.setDesignation(desig);
        }
        return dto;
    }

    private String generateTicketNo() {
        String datePrefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "LF-" + datePrefix + "-" + uuid;
    }
}