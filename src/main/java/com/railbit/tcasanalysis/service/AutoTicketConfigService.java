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
import java.time.ZoneId;
import java.util.Date;
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
    private final UserRepo userRepository;
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
                .railwayUserId(dto.getRailwayUserId())
                .oemUserId(dto.getOemUserId())
                .createdByUserId(dto.getCreatedByUserId())
                .isActive(true)
                .build();

        AutoTicketConfig saved = configRepository.save(config);
        log.info("AutoTicketConfig saved: id={}, categories={}, autoTicket={}, autoEmail={}, railwayUserId={}, oemUserId={}",
                saved.getId(), saved.getSelectedCategories(),
                saved.isAutoTicketEnabled(), saved.isAutoEmailEnabled(),
                saved.getRailwayUserId(), saved.getOemUserId());
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

        // ── Resolve User entity from stored IDs ─────────────────────────────
        // Priority: Railway user first, then OEM user
        User assignedTo = null;

        if (config.getRailwayUserId() != null) {
            assignedTo = userRepository.findById(config.getRailwayUserId())
                    .orElse(null);
            if (assignedTo == null) {
                log.warn("Railway user not found: id={}", config.getRailwayUserId());
            }
        }

        // Fall back to OEM user if Railway user not found or not set
        if (assignedTo == null && config.getOemUserId() != null) {
            assignedTo = userRepository.findById(config.getOemUserId())
                    .orElse(null);
            if (assignedTo == null) {
                log.warn("OEM user not found: id={}", config.getOemUserId());
            }
        }

        if (assignedTo == null) {
            log.error("No valid user found for auto-ticket creation. RailwayUserId={}, OemUserId={}",
                    config.getRailwayUserId(), config.getOemUserId());
            return;
        }

        User createdByUser = null;
        if (config.getCreatedByUserId() != null) {
            createdByUser = userRepository.findById(config.getCreatedByUserId()).orElse(null);
        }

        // ── Build and persist KavachAlertDetails ──────────────────────────────
        String ticketNo = generateTicketNumber();

        KavachAlertDetails details = new KavachAlertDetails();
        details.setKavachAlert(alert);
        details.setTicketNo(ticketNo);
        details.setTicketStatus("OPEN");
        details.setAssignedTo(assignedTo);
        details.setCreatedUser(createdByUser);
        details.setIncidentCreatedAt(LocalDateTime.now());
        details.setAutoCreated(true);

        alertDetailsRepository.save(details);
        log.info("Auto-ticket created: ticketNo={}, alertId={}, assignedTo={} (userId={})",
                ticketNo, alert.getId(), assignedTo.getName(), assignedTo.getId());

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
                .selectedCategories(categoryIds)
                .autoTicketEnabled(c.isAutoTicketEnabled())
                .autoEmailEnabled(c.isAutoEmailEnabled())
                .railwayUserId(c.getRailwayUserId())
                .oemUserId(c.getOemUserId())
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