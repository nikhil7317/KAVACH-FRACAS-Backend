package com.railbit.tcasanalysis.service.serviceImpl;


import com.railbit.tcasanalysis.entity.Asset;
import com.railbit.tcasanalysis.entity.analysis.StringAndCount;
import com.railbit.tcasanalysis.repository.AssetRepo;
import com.railbit.tcasanalysis.repository.DashboardRepo;
import com.railbit.tcasanalysis.service.AssetService;
import com.railbit.tcasanalysis.service.DashboardService;
import com.railbit.tcasanalysis.util.HelpingHand;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private static final Logger log = LoggerFactory.getLogger(DashboardServiceImpl.class);
    private final DashboardRepo dashboardRepo;

    @Override
    public List<StringAndCount> getMajorIncidentsStationWise(String filter) {
        // Define fromDate based on filter
        LocalDate toDate = LocalDate.now();
        LocalDate fromDate = switch (filter) {
            case "1 week" -> toDate.minusWeeks(1);
            case "15 days" -> toDate.minusDays(15);
            case "1 month" -> toDate.minusMonths(1);
            case "3 months" -> toDate.minusMonths(3);
            case "6 months" -> toDate.minusMonths(6);
            case "1 year" -> toDate.minusYears(1);
            case "all time" -> LocalDate.MIN;
            default -> throw new IllegalArgumentException("Invalid filter value: " + filter);
        };

        // Define pageable to limit results to top 5
        Pageable pageable = PageRequest.of(0, 5);

        // Call repository method with fromDate, toDate, and pageable
        List<Object[]> topStations = dashboardRepo.findTopStationsWithMostIssuesBetweenDates(fromDate, toDate, pageable);

        // Map results to List<StringAndCount>

        return topStations.stream()
                .map(result -> new StringAndCount((String) result[0], ((Long) result[1]).intValue(), HelpingHand.getRandomColor()))
                .collect(Collectors.toList());
    }

    @Override
    public List<StringAndCount> getMajorIncidentsLocoWise(String filter) {
        // Define fromDate based on filter
        LocalDate toDate = LocalDate.now();
        LocalDate fromDate = switch (filter) {
            case "1 week" -> toDate.minusWeeks(1);
            case "15 days" -> toDate.minusDays(15);
            case "1 month" -> toDate.minusMonths(1);
            case "3 months" -> toDate.minusMonths(3);
            case "6 months" -> toDate.minusMonths(6);
            case "1 year" -> toDate.minusYears(1);
            case "all time" -> LocalDate.MIN;
            default -> throw new IllegalArgumentException("Invalid filter value: " + filter);
        };

        // Define pageable to limit results to top 5
        Pageable pageable = PageRequest.of(0, 5);

        // Call repository method with fromDate, toDate, and pageable
        List<Object[]> topStations = dashboardRepo.findTopLocosWithMostIssuesBetweenDates(fromDate, toDate, pageable);

        // Map results to List<StringAndCount>

        return topStations.stream()
                .map(result -> new StringAndCount((String) result[0], ((Long) result[1]).intValue(), HelpingHand.getRandomColor()))
                .collect(Collectors.toList());
    }

    @Override
    public List<StringAndCount> getMajorIncidentsCauseWise(String filter) {
        // Define fromDate based on filter
        LocalDate toDate = LocalDate.now();
        LocalDate fromDate = switch (filter) {
            case "1 week" -> toDate.minusWeeks(1);
            case "15 days" -> toDate.minusDays(15);
            case "1 month" -> toDate.minusMonths(1);
            case "3 months" -> toDate.minusMonths(3);
            case "6 months" -> toDate.minusMonths(6);
            case "1 year" -> toDate.minusYears(1);
            case "all time" -> LocalDate.MIN;
            default -> throw new IllegalArgumentException("Invalid filter value: " + filter);
        };

        // Define pageable to limit results to top 5
        Pageable pageable = PageRequest.of(0, 5);

        // Call repository method with fromDate, toDate, and pageable
        List<Object[]> topStations = dashboardRepo.findTopCausesWithMostIssuesBetweenDates(fromDate, toDate, pageable);

        // Map results to List<StringAndCount>

        return topStations.stream()
                .map(result -> new StringAndCount((String) result[0], ((Long) result[1]).intValue(), HelpingHand.getRandomColor()))
                .collect(Collectors.toList());
    }

    @Override
    public List<StringAndCount> getMajorIncidentsDivisionWise(String filter) {
        // Define fromDate based on filter
        LocalDate toDate = LocalDate.now();
        LocalDate fromDate = switch (filter) {
            case "1 week" -> toDate.minusWeeks(1);
            case "15 days" -> toDate.minusDays(15);
            case "1 month" -> toDate.minusMonths(1);
            case "3 months" -> toDate.minusMonths(3);
            case "6 months" -> toDate.minusMonths(6);
            case "1 year" -> toDate.minusYears(1);
            case "all time" -> LocalDate.MIN;
            default -> throw new IllegalArgumentException("Invalid filter value: " + filter);
        };

        // Define pageable to limit results to top 5
        Pageable pageable = PageRequest.of(0, 5);
        log.info("From Date {}",fromDate);
        log.info("To Date {}",toDate);
        // Call repository method with fromDate, toDate, and pageable
        List<Object[]> topStations = dashboardRepo.findTopDivisionsWithMostIssuesBetweenDates(fromDate, toDate, pageable);

        // Map results to List<StringAndCount>

        return topStations.stream()
                .map(result -> new StringAndCount((String) result[0], ((Long) result[1]).intValue(), HelpingHand.getRandomColor()))
                .collect(Collectors.toList());
    }

    @Override
    public List<StringAndCount> getMajorIncidentsOEMWise(String filter) {
        // Define fromDate based on filter
        LocalDate toDate = LocalDate.now();
        LocalDate fromDate = switch (filter) {
            case "1 week" -> toDate.minusWeeks(1);
            case "15 days" -> toDate.minusDays(15);
            case "1 month" -> toDate.minusMonths(1);
            case "3 months" -> toDate.minusMonths(3);
            case "6 months" -> toDate.minusMonths(6);
            case "1 year" -> toDate.minusYears(1);
            case "all time" -> LocalDate.MIN;
            default -> throw new IllegalArgumentException("Invalid filter value: " + filter);
        };

        // Define pageable to limit results to top 5
        Pageable pageable = PageRequest.of(0, 5);

        // Call repository method with fromDate, toDate, and pageable
        List<Object[]> topStations = dashboardRepo.findTopOEMsWithMostIssuesBetweenDates(fromDate, toDate, pageable);

        // Map results to List<StringAndCount>

        return topStations.stream()
                .map(result -> new StringAndCount((String) result[0], ((Long) result[1]).intValue(), HelpingHand.getRandomColor()))
                .collect(Collectors.toList());
    }
}
