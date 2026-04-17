package com.railbit.tcasanalysis.service;

import com.railbit.tcasanalysis.DTO.AlertMessageConfigRequest;
import com.railbit.tcasanalysis.DTO.AlertMessageConfigResponse;
import com.railbit.tcasanalysis.entity.AlertMessageConfig;
import com.railbit.tcasanalysis.repository.AlertMessageConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertMessageConfigService {

    private final AlertMessageConfigRepository repo;

    // ── READ ──────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<AlertMessageConfigResponse> getAll() {
        return repo.findAllByOrderByAlertCategoryAscAlertMessageAsc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AlertMessageConfigResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    // ── CREATE ────────────────────────────────────────────────────────────────

    @Transactional
    public AlertMessageConfigResponse create(AlertMessageConfigRequest req) {
        // Duplicate guard
        repo.findByAlertCategoryAndAlertMessage(
                        req.getAlertCategory().trim(),
                        req.getAlertMessage().trim())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Config already exists for category='" + req.getAlertCategory()
                                    + "' and message='" + req.getAlertMessage() + "'"
                    );
                });

        AlertMessageConfig entity = AlertMessageConfig.builder()
                .alertCategory(req.getAlertCategory().trim())
                .alertMessage(req.getAlertMessage().trim())
                .enabled(req.getEnabled() == null ? Boolean.TRUE : req.getEnabled())
                .build();

        AlertMessageConfig saved = repo.save(entity);
        log.info("Created AlertMessageConfig id={} category={} message={}",
                saved.getId(), saved.getAlertCategory(), saved.getAlertMessage());
        return toResponse(saved);
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    @Transactional
    public AlertMessageConfigResponse update(Long id, AlertMessageConfigRequest req) {
        AlertMessageConfig entity = findOrThrow(id);

        String newCategory = req.getAlertCategory().trim();
        String newMessage  = req.getAlertMessage().trim();

        // Duplicate guard (exclude self)
        repo.findByAlertCategoryAndAlertMessage(newCategory, newMessage)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(dup -> {
                    throw new IllegalArgumentException(
                            "Another config already exists for category='" + newCategory
                                    + "' and message='" + newMessage + "'"
                    );
                });

        entity.setAlertCategory(newCategory);
        entity.setAlertMessage(newMessage);
        if (req.getEnabled() != null) {
            entity.setEnabled(req.getEnabled());
        }

        log.info("Updated AlertMessageConfig id={}", id);
        return toResponse(repo.save(entity));
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("AlertMessageConfig not found: id=" + id);
        }
        repo.deleteById(id);
        log.info("Deleted AlertMessageConfig id={}", id);
    }

    // ── TOGGLE ────────────────────────────────────────────────────────────────

    @Transactional
    public AlertMessageConfigResponse toggle(Long id) {
        AlertMessageConfig entity = findOrThrow(id);
        entity.setEnabled(!entity.getEnabled());
        AlertMessageConfig saved = repo.save(entity);
        log.info("Toggled AlertMessageConfig id={} → enabled={}",
                saved.getId(), saved.getEnabled());
        return toResponse(saved);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private AlertMessageConfig findOrThrow(Long id) {
        return repo.findById(id).orElseThrow(() ->
                new IllegalArgumentException("AlertMessageConfig not found: id=" + id));
    }

    private AlertMessageConfigResponse toResponse(AlertMessageConfig e) {
        return AlertMessageConfigResponse.builder()
                .id(e.getId())
                .alertCategory(e.getAlertCategory())
                .alertMessage(e.getAlertMessage())
                .enabled(e.getEnabled())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}