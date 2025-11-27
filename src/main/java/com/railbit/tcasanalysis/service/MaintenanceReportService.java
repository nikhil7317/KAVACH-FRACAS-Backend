package com.railbit.tcasanalysis.service;
import com.railbit.tcasanalysis.entity.MaintenanceCheckpoint;
import com.railbit.tcasanalysis.entity.MaintenanceReport;
import com.railbit.tcasanalysis.entity.MaintenanceUser;
import com.railbit.tcasanalysis.repository.MaintenanceCheckpointRepo;
import com.railbit.tcasanalysis.repository.MaintenanceReportRepo;
import com.railbit.tcasanalysis.repository.MaintenanceUserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class MaintenanceReportService {
    private static final Logger logger = LoggerFactory.getLogger(MaintenanceReportService.class);

    @Autowired
    FileService fileService;

    @Autowired
    MaintenanceReportRepo reportRepo;


    public String addMaintenanceRecords(MaintenanceReport request, UserDetails userDetails) {
        // Validate input parameters
        if (request == null) {
            throw new IllegalArgumentException("Maintenance report request is null.");
        }

        if (userDetails == null) {
            throw new IllegalArgumentException("User details are null.");
        }

        try {
            // Set creation timestamp
            request.setCreatedAt(LocalDateTime.now());

            // Save the initial MaintenanceReport to generate its ID
            MaintenanceReport savedReport = reportRepo.save(request);

            // Validate the generated ID
            if (savedReport.getId() == null || savedReport.getId() <= 0) {
                throw new IllegalArgumentException("Failed to save MaintenanceReport. Primary key not generated.");
            }


            // Validate station ID
//            if (request.getStnId() == null) {
//                throw new IllegalArgumentException("Station ID is missing.");
//            }

            // Validate file data
            if (request.getFileData() == null || request.getFileData().isEmpty()) {
                throw new IllegalArgumentException("File data is missing.");
            }

            // Validate checkpoints
            if (request.getCheckpoints() == null || request.getCheckpoints().isEmpty()) {
                throw new IllegalArgumentException("Checkpoints are missing.");
            }

            // Process and attach checkpoints
            for (MaintenanceCheckpoint checkpoint : request.getCheckpoints()) {
                if (checkpoint.getFileData() != null && !checkpoint.getFileData().isEmpty()) {
                    String fileUrl = fileService.storeFiles(checkpoint.getFileData(), savedReport.getId(), checkpoint.getFileName());
                    checkpoint.setFileUrl(fileUrl);
                }
                checkpoint.setMaintenanceReport(savedReport); // Set the relationship
            }

            // Process and attach users
            if (request.getUsers() != null && !request.getUsers().isEmpty()) {
                for (MaintenanceUser user : request.getUsers()) {
                    user.setMaintenanceReport(savedReport);
                }
            }

            // Attach updated lists to the saved report
            savedReport.setCheckpoints(request.getCheckpoints());
            savedReport.setUsers(request.getUsers());

            // Process and attach main file data
            if (request.getFileData() != null && !request.getFileData().isEmpty()) {
                String fileUrl = fileService.storeFiles(request.getFileData(), savedReport.getId(), request.getFileName());
                savedReport.setFileUrl(fileUrl);
            }

            // Save the fully populated MaintenanceReport
            reportRepo.save(savedReport);

            logger.info("Successfully saved MaintenanceReport with ID: {}", savedReport.getId());

            return "Success";

        } catch (Exception e) {
            logger.error("Error saving MaintenanceReport: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Failed to save MaintenanceReport: " + e.getMessage(), e);
        }
    }


    public Page<MaintenanceReport> getFilteredReports(Integer divId, Integer stnId, Integer locoId, String startDate, String endDate,int page,int size) {
        LocalDateTime start = startDate != null ? LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay(): null;
        LocalDateTime end = endDate != null ? LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE).atTime(23, 59, 59): null;
        //Sort sort = sortDirection.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, size);

        Specification<MaintenanceReport> specification = Specification.where(MaintenanceReportSpecification.hasDivId(divId))
                .and(MaintenanceReportSpecification.hasStnId(stnId))
                .and(MaintenanceReportSpecification.hasLocoId(locoId))
                .and(MaintenanceReportSpecification.hasDateBetween(start, end));
        Page<MaintenanceReport> paginatedReports = reportRepo.findAll(specification, pageable);

        return paginatedReports;
    }

    public void deleteMaintenanceReportById(Long id) {
        reportRepo.deleteById(id);
    }

    public void updateMaintenance(MaintenanceReport report) {
        MaintenanceReport existing = reportRepo.findById(report.getId())
                .orElseThrow(() -> new RuntimeException("MaintenanceReport not found with id: " + report.getId()));

        if (report.getDate() != null) existing.setDate(report.getDate());
        if (report.getFrequency() != null) existing.setFrequency(report.getFrequency());
        if (report.getBriefDesc() != null) existing.setBriefDesc(report.getBriefDesc());
        if (report.getYear() != null) existing.setYear(report.getYear());
        if (report.getFileUrl() != null) existing.setFileUrl(report.getFileUrl());
        if (report.getFileName() != null) existing.setFileName(report.getFileName());
        if (report.getCreatedBy() != null) existing.setCreatedBy(report.getCreatedBy());
        if (report.getCreatedAt() != null) existing.setCreatedAt(report.getCreatedAt());
        if (report.getMaintType() != null) existing.setMaintType(report.getMaintType());
        if (report.getZoneId() != null) existing.setZoneId(report.getZoneId());
        if (report.getDivId() != null) existing.setDivId(report.getDivId());
        if (report.getStnId() != null) existing.setStnId(report.getStnId());
        if (report.getLocoId() != null) existing.setLocoId(report.getLocoId());

        if (report.getCheckpoints() != null) {
            existing.getCheckpoints().clear();
            report.getCheckpoints().forEach(cp -> {
                cp.setMaintenanceReport(existing);
                existing.getCheckpoints().add(cp);
            });
        }

        if (report.getUsers() != null) {
            existing.getUsers().clear();
            report.getUsers().forEach(user -> {
                user.setMaintenanceReport(existing);
                existing.getUsers().add(user);
            });
        }

        reportRepo.save(existing);
    }


}