package com.railbit.tcasanalysis.service;

import com.railbit.tcasanalysis.DTO.AutoTicketConfigDTO;
import com.railbit.tcasanalysis.entity.AutoTicketConfig;
import com.railbit.tcasanalysis.entity.KavachAlert;
import com.railbit.tcasanalysis.entity.KavachAlertDetails;
import com.railbit.tcasanalysis.entity.User;
import com.railbit.tcasanalysis.repository.AutoTicketConfigRepository;
import com.railbit.tcasanalysis.repository.CategoryRepository;
import com.railbit.tcasanalysis.repository.KavachAlertDetailsRepository;

import com.railbit.tcasanalysis.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutoTicketConfigService {

    private final AutoTicketConfigRepository configRepository;
    private final KavachAlertDetailsRepository alertDetailsRepository;
    private final UserRepo userRepository;          // needed to resolve User entities
    private final CategoryRepository categoryRepository;
    // private final EmailService emailService;

    // ── CRUD ──────────────────────────────────────────────────────────────────

    public AutoTicketConfigDTO getActiveConfig() {
        return configRepository.findByIsActiveTrue()
                .map(this::toDTO)
                .orElseGet(AutoTicketConfigDTO::new);
    }

    @Transactional
    public AutoTicketConfigDTO saveConfig(AutoTicketConfigDTO dto) {
        configRepository.deactivateAll();

        List<String> categoryNames = dto.getSelectedCategories().stream()
                .map(id -> categoryRepository.findById(Long.valueOf(id))
                        .map(c -> c.getName())
                        .orElse(String.valueOf(id)))
                .collect(Collectors.toList());

        AutoTicketConfig config = AutoTicketConfig.builder()
                .selectedCategories(String.join(",", categoryNames))
                .autoTicketEnabled(dto.isAutoTicketEnabled())
                .autoEmailEnabled(dto.isAutoEmailEnabled())
                .userType(dto.getUserType())
                .assignedToUserId(dto.getAssignedToUserId())
                .createdByUserId(dto.getCreatedByUserId())
                .isActive(true)
                .build();

        AutoTicketConfig saved = configRepository.save(config);
        log.info("AutoTicketConfig saved: id={}, categories={}, autoTicket={}, autoEmail={}",
                saved.getId(), saved.getSelectedCategories(),
                saved.isAutoTicketEnabled(), saved.isAutoEmailEnabled());
        return toDTO(saved);
    }

    // ── Auto-ticket trigger ───────────────────────────────────────────────────

    @Transactional
    public void processAlertForAutoTicket(KavachAlert alert) {
        Optional<AutoTicketConfig> opt = configRepository.findByIsActiveTrue();
        if (opt.isEmpty()) return;

        AutoTicketConfig config = opt.get();

        if (!config.isAutoTicketEnabled()) return;
        if (!config.matchesCategory(alert.getAlertCategory())) return;

        // Avoid duplicate auto-tickets for the same alert
        if (alertDetailsRepository.existsByKavachAlertId(alert.getId())) {
            log.debug("Auto-ticket skipped — already exists for alertId={}", alert.getId());
            return;
        }

        // ── Resolve User entities from stored IDs ─────────────────────────────
        // KavachAlertDetails.assignedTo and .createdUser are @ManyToOne User,
        // so we must pass User objects — NOT raw Long IDs.

        User assignedTo = null;
        if (config.getAssignedToUserId() != null) {
            assignedTo = userRepository.findById(config.getAssignedToUserId())
                    .orElseThrow(() -> new RuntimeException(
                            "Assigned user not found: id=" + config.getAssignedToUserId()));
        }

        User createdByUser = null;
        if (config.getCreatedByUserId() != null) {
            // best-effort — don't fail if the admin user row is missing
            createdByUser = userRepository.findById(config.getCreatedByUserId()).orElse(null);
        }

        // ── Build and persist KavachAlertDetails ──────────────────────────────
        String ticketNo = generateTicketNumber();

        KavachAlertDetails details = new KavachAlertDetails();
        details.setKavachAlert(alert);
        details.setTicketNo(ticketNo);
        details.setTicketStatus("OPEN");
        details.setAssignedTo(assignedTo);         // ✅  User object
        details.setCreatedUser(createdByUser);     // ✅  User object
        details.setIncidentCreatedAt(LocalDateTime.now());
        details.setAutoCreated(true);

        alertDetailsRepository.save(details);
        log.info("Auto-ticket created: ticketNo={}, alertId={}, assignedTo={}",
                ticketNo, alert.getId(), config.getAssignedToUserId());

        if (config.isAutoEmailEnabled()) {
            // emailService.sendAutoTicketEmail(details, config);
            log.info("Auto-email triggered for ticketNo={}", ticketNo);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private AutoTicketConfigDTO toDTO(AutoTicketConfig c) {
        // Convert stored names "BRAKE,RFID_ISSUE" → IDs [1, 7]
        List<Integer> categoryIds = c.getCategoryList().stream()
                .map(name -> categoryRepository.findByName(name)
                        .map(cat -> cat.getId().intValue())
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return AutoTicketConfigDTO.builder()
                .id(c.getId())
                .selectedCategories(categoryIds)   // ✅ List<Integer>
                .autoTicketEnabled(c.isAutoTicketEnabled())
                .autoEmailEnabled(c.isAutoEmailEnabled())
                .userType(c.getUserType())
                .assignedToUserId(c.getAssignedToUserId())
                .createdByUserId(c.getCreatedByUserId())
                .build();
    }

    private String generateTicketNumber() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        String dd = String.format("%02d", cal.get(java.util.Calendar.DAY_OF_MONTH));
        String mm = String.format("%02d", cal.get(java.util.Calendar.MONTH) + 1);
        String yy = String.format("%02d", cal.get(java.util.Calendar.YEAR) % 100);
        long todayCount = alertDetailsRepository.countTodayTickets();
        return String.format("PYG/%s/%s/%s/%d", dd, mm, yy, todayCount + 1);
    }
}