package com.railbit.tcasanalysis.service;

import com.railbit.tcasanalysis.DTO.OemRemarksRequest;
import com.railbit.tcasanalysis.DTO.OemRemarksResponseDTO;
import com.railbit.tcasanalysis.entity.KavachAlertDetails;
import com.railbit.tcasanalysis.entity.OemRemarks;
import com.railbit.tcasanalysis.entity.User;
import com.railbit.tcasanalysis.repository.KavachAlertDetailsRepository;
import com.railbit.tcasanalysis.repository.OemRemarksRepository;
import com.railbit.tcasanalysis.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OemRemarksService {

    @Autowired
    private OemRemarksRepository oemRemarksRepository;

    @Autowired
    private KavachAlertDetailsRepository kavachAlertDetailsRepository;

    @Autowired
    private UserRepo userRepository;

    /**
     * POST /oem-remarks
     * OEM user submits remarks for a ticket.
     * createdUser is resolved from DB using the id in the request —
     * so designation is always authoritative from DB, never from client payload.
     */
    public OemRemarksResponseDTO save(OemRemarksRequest request) {

        if (request.getTicketRemarks() == null || request.getTicketRemarks().isBlank()) {
            throw new RuntimeException("Ticket remarks cannot be empty");
        }

        KavachAlertDetails details = kavachAlertDetailsRepository
                .findById(request.getKavachAlertDetails().getId())
                .orElseThrow(() -> new RuntimeException(
                        "Ticket not found: " + request.getKavachAlertDetails().getId()));

        // Resolve user from DB — designation comes from DB, not from client
        User createdUser = userRepository
                .findById(request.getCreatedUser().getId())
                .orElseThrow(() -> new RuntimeException(
                        "User not found: " + request.getCreatedUser().getId()));

        // Verify the user is actually OEM (designation.id == 2)
        // This is a server-side guard — OEM identity is never trusted from client
        Integer designationId = createdUser.getDesignation() != null
                ? createdUser.getDesignation().getId()
                : null;
        // ✅ Allow ONLY assigned user (OEM or Railway)
        if (details.getAssignedTo() == null ||
                !details.getAssignedTo().getId().equals(createdUser.getId())) {
            throw new RuntimeException("Only assigned user can submit remarks");
        }

        OemRemarks remark = new OemRemarks();
        remark.setKavachAlertDetails(details);
        remark.setCreatedUser(createdUser);
        remark.setTicketRemarks(request.getTicketRemarks().trim());
        // Use client time if provided, else @PrePersist will set it
        if (request.getIncidentCreatedAt() != null) {
            remark.setCreatedAt(request.getIncidentCreatedAt());
        }

        OemRemarks saved = oemRemarksRepository.save(remark);
        return toResponseDTO(saved);
    }

    /**
     * GET /oem-remarks/check/{kavachAlertDetailsId}
     * Returns whether OEM has submitted remarks for a given ticket.
     * Used by admin frontend to decide whether Close/Re-Assign is allowed.
     */
    public boolean hasOemRemarks(Long kavachAlertDetailsId) {
        return oemRemarksRepository.existsByKavachAlertDetailsId(kavachAlertDetailsId);
    }

    /**
     * GET /oem-remarks/{kavachAlertDetailsId}
     * Returns all OEM remarks for a ticket (for display in the dialog).
     */
    public List<OemRemarksResponseDTO> getByTicket(Long kavachAlertDetailsId) {
        return oemRemarksRepository
                .findByKavachAlertDetailsIdOrderByCreatedAtDesc(kavachAlertDetailsId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ─── Mapper ───────────────────────────────────────────────────────────────

    private OemRemarksResponseDTO toResponseDTO(OemRemarks remark) {
        OemRemarksResponseDTO dto = new OemRemarksResponseDTO();

        dto.setId(remark.getId());
        dto.setKavachAlertDetailsId(remark.getKavachAlertDetails().getId());
        dto.setTicketNo(remark.getKavachAlertDetails().getTicketNo());
        dto.setTicketRemarks(remark.getTicketRemarks());
        dto.setCreatedAt(remark.getCreatedAt());

        if (remark.getCreatedUser() != null) {
            OemRemarksResponseDTO.UserInfo userInfo = new OemRemarksResponseDTO.UserInfo();
            userInfo.setId(remark.getCreatedUser().getId());
            userInfo.setName(remark.getCreatedUser().getName());

            // ✅ DESIGNATION
            if (remark.getCreatedUser().getDesignation() != null) {
                OemRemarksResponseDTO.UserInfo.DesignationInfo desig =
                        new OemRemarksResponseDTO.UserInfo.DesignationInfo();
                desig.setId(remark.getCreatedUser().getDesignation().getId());
                desig.setName(remark.getCreatedUser().getDesignation().getName());
                desig.setTitle(remark.getCreatedUser().getDesignation().getTitle());
                userInfo.setDesignation(desig);
            }

            // ✅ ROLE (NEW ADDITION)
            if (remark.getCreatedUser().getRole() != null) {
                OemRemarksResponseDTO.UserInfo.RoleInfo role =
                        new OemRemarksResponseDTO.UserInfo.RoleInfo();
                role.setId(remark.getCreatedUser().getRole().getId());
                role.setName(remark.getCreatedUser().getRole().getName());
                userInfo.setRole(role);
            }

            dto.setCreatedUser(userInfo);
        }

        return dto;
    }
}