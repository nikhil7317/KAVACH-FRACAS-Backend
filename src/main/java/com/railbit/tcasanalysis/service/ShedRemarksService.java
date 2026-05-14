package com.railbit.tcasanalysis.service;

import com.railbit.tcasanalysis.DTO.ShedRemarksRequest;
import com.railbit.tcasanalysis.DTO.ShedRemarksResponseDTO;
import com.railbit.tcasanalysis.entity.LocoFailure;
import com.railbit.tcasanalysis.entity.ShedRemarks;
import com.railbit.tcasanalysis.entity.User;
import com.railbit.tcasanalysis.repository.LocoFailureRepository;
import com.railbit.tcasanalysis.repository.ShedRemarksRepository;
import com.railbit.tcasanalysis.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShedRemarksService {

    private final ShedRemarksRepository shedRemarksRepository;
    private final LocoFailureRepository locoFailureRepository;
    private final UserRepo userRepository;

    public ShedRemarksResponseDTO save(ShedRemarksRequest request) {

        if (request.getTicketRemarks() == null || request.getTicketRemarks().isBlank()) {
            throw new RuntimeException("Ticket remarks cannot be empty");
        }

        LocoFailure locoFailure = locoFailureRepository
                .findById(request.getLocoFailure().getId())
                .orElseThrow(() -> new RuntimeException(
                        "Loco failure not found: " + request.getLocoFailure().getId()));

        User createdUser = userRepository
                .findById(request.getCreatedUser().getId())
                .orElseThrow(() -> new RuntimeException(
                        "User not found: " + request.getCreatedUser().getId()));

        // Only assigned user can submit remarks
        if (locoFailure.getAssignedTo() == null ||
                !locoFailure.getAssignedTo().getId().equals(createdUser.getId())) {
            throw new RuntimeException("Only assigned user can submit remarks");
        }

        ShedRemarks remark = new ShedRemarks();
        remark.setLocoFailure(locoFailure);
        remark.setCreatedUser(createdUser);
        remark.setTicketRemarks(request.getTicketRemarks().trim());
        if (request.getIncidentCreatedAt() != null) {
            remark.setCreatedAt(request.getIncidentCreatedAt());
        }

        ShedRemarks saved = shedRemarksRepository.save(remark);
        return toResponseDTO(saved);
    }

    public boolean hasShedRemarks(Long locoFailureId) {
        return shedRemarksRepository.existsByLocoFailureId(locoFailureId);
    }

    public List<ShedRemarksResponseDTO> getByLocoFailure(Long locoFailureId) {
        return shedRemarksRepository
                .findByLocoFailureIdOrderByCreatedAtDesc(locoFailureId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private ShedRemarksResponseDTO toResponseDTO(ShedRemarks remark) {
        ShedRemarksResponseDTO dto = new ShedRemarksResponseDTO();

        dto.setId(remark.getId());
        dto.setLocoFailureId(remark.getLocoFailure().getId());
        dto.setTicketNo(remark.getLocoFailure().getTicketNo());
        dto.setTicketRemarks(remark.getTicketRemarks());
        dto.setCreatedAt(remark.getCreatedAt());

        if (remark.getCreatedUser() != null) {
            ShedRemarksResponseDTO.UserInfo userInfo = new ShedRemarksResponseDTO.UserInfo();
            userInfo.setId(remark.getCreatedUser().getId());
            userInfo.setName(remark.getCreatedUser().getName());

            if (remark.getCreatedUser().getDesignation() != null) {
                ShedRemarksResponseDTO.UserInfo.DesignationInfo desig =
                        new ShedRemarksResponseDTO.UserInfo.DesignationInfo();
                desig.setId(remark.getCreatedUser().getDesignation().getId());
                desig.setName(remark.getCreatedUser().getDesignation().getName());
                desig.setTitle(remark.getCreatedUser().getDesignation().getTitle());
                userInfo.setDesignation(desig);
            }

            if (remark.getCreatedUser().getRole() != null) {
                ShedRemarksResponseDTO.UserInfo.RoleInfo role =
                        new ShedRemarksResponseDTO.UserInfo.RoleInfo();
                role.setId(remark.getCreatedUser().getRole().getId());
                role.setName(remark.getCreatedUser().getRole().getName());
                userInfo.setRole(role);
            }

            dto.setCreatedUser(userInfo);
        }

        return dto;
    }
}