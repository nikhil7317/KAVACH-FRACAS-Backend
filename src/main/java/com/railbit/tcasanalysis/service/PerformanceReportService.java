package com.railbit.tcasanalysis.service;

import com.railbit.tcasanalysis.DTO.performanceReport.OverallPerformanceReportDTO;
import com.railbit.tcasanalysis.entity.Division;
import com.railbit.tcasanalysis.entity.IssueCategory;
import com.railbit.tcasanalysis.entity.TcasBreakingInspection;
import com.railbit.tcasanalysis.entity.Zone;
import com.railbit.tcasanalysis.entity.cmsabn.CMSAbn;
import com.railbit.tcasanalysis.entity.loco.Loco;
import com.railbit.tcasanalysis.entity.loco.LocoType;
import com.railbit.tcasanalysis.util.HelpingHand;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class PerformanceReportService {

    @Autowired
    EntityManager entityManager;

    public List<OverallPerformanceReportDTO> getOverallPerformanceReportMonthWise(Long zoneId, Long divisionId, Integer year) {

        List<OverallPerformanceReportDTO> overallPerformanceReportDTOList = new ArrayList<>();

        for (int i = 0; i < 12; i++) {

            int month = i+1;
            int totalTrips = Math.toIntExact(getUniqueTripCountByZoneDivisionAndMonth(zoneId, divisionId, month, year));
            if (totalTrips < 1) {
                continue;
            }

            String monthName = (HelpingHand.getMonthName(month));
            Integer emuTrips = Math.toIntExact(getUniqueEMUTripCountByZoneDivisionAndMonth(zoneId, divisionId, month,year));
            Integer nonEmuTrips = Math.toIntExact(getUniqueNonEMUTripCountByZoneDivisionAndMonth(zoneId, divisionId, month,year));
            Integer undesirableBraking = Math.toIntExact(getCountByMonthAndIssueCategoryName(zoneId, divisionId, month, "Undesirable Braking",year));
            Integer desirableBraking = Math.toIntExact(getCountByMonthAndIssueCategoryName(zoneId, divisionId, month, "Desirable Braking",year));
            Integer modeChange = Math.toIntExact(getCountByMonthAndIssueCategoryName(zoneId, divisionId, month, "Mode Change",year));

            OverallPerformanceReportDTO overallPerformanceReportDTO = new OverallPerformanceReportDTO(monthName,totalTrips,emuTrips,undesirableBraking,desirableBraking,modeChange);

            overallPerformanceReportDTOList.add(overallPerformanceReportDTO);
        }

        return overallPerformanceReportDTOList;
    }

    public long getUniqueNonEMUTripCountByZoneDivisionAndMonth(Long zoneId, Long divisionId, int month, Integer year) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Invalid month. Please provide a value between 1 and 12.");
        }

        // Create CriteriaBuilder
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        // Create CriteriaQuery for count
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        // Define Root for TcasBreakingInspection
        Root<TcasBreakingInspection> inspectionRoot = criteriaQuery.from(TcasBreakingInspection.class);

        // Join Division and Zone
        Join<TcasBreakingInspection, Loco> locoJoin = inspectionRoot.join("loco");
        Join<Loco, LocoType> locoTypeJoin = locoJoin.join("locoType");
        Join<TcasBreakingInspection, Division> divisionJoin = inspectionRoot.join("division");
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone");

        // Define predicates
        List<Predicate> predicates = new ArrayList<>();

        // Add predicates for zone and division if provided
        if (zoneId != null && zoneId != 0) {
            predicates.add(criteriaBuilder.equal(zoneJoin.get("id"), zoneId));
        }
        if (divisionId != null && divisionId != 0) {
            predicates.add(criteriaBuilder.equal(divisionJoin.get("id"), divisionId));
        }

        // Add predicate to exclude locoTypeJoin.id = 7 or 9
        Predicate excludeLocoTypePredicate = criteriaBuilder.not(
                criteriaBuilder.or(
                        criteriaBuilder.equal(locoTypeJoin.get("id"), 7),
                        criteriaBuilder.equal(locoTypeJoin.get("id"), 9)
                )
        );
        predicates.add(excludeLocoTypePredicate);

        // Add predicate for the specified month
        predicates.add(criteriaBuilder.equal(criteriaBuilder.function("MONTH", Integer.class, inspectionRoot.get("tripDate")), month));

        // Add predicate for the specified year
        if (year != null) {
            predicates.add(criteriaBuilder.equal(criteriaBuilder.function("YEAR", Integer.class, inspectionRoot.get("tripDate")), year));
        }
        // Combine all predicates with AND
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        // Select COUNT(DISTINCT CONCAT(tripNo, divisionId))
        Expression<String> uniqueTripExpression = criteriaBuilder.concat(
                criteriaBuilder.concat(
                        criteriaBuilder.literal(""),
                        inspectionRoot.get("tripNo").as(String.class)
                ),
                divisionJoin.get("id").as(String.class)
        );

        criteriaQuery.select(criteriaBuilder.countDistinct(uniqueTripExpression));

        // Execute query and return result
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    public long getUniqueEMUTripCountByZoneDivisionAndMonth(Long zoneId, Long divisionId, int month, Integer year) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Invalid month. Please provide a value between 1 and 12.");
        }

        // Create CriteriaBuilder
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        // Create CriteriaQuery for count
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        // Define Root for TcasBreakingInspection
        Root<TcasBreakingInspection> inspectionRoot = criteriaQuery.from(TcasBreakingInspection.class);

        // Join Division and Zone
        Join<TcasBreakingInspection, Loco> locoJoin = inspectionRoot.join("loco");
        Join<Loco, LocoType> locoTypeJoin = locoJoin.join("locoType");
        Join<TcasBreakingInspection, Division> divisionJoin = inspectionRoot.join("division");
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone");

        // Define predicates
        List<Predicate> predicates = new ArrayList<>();

        // Add predicates for zone and division if provided
        if (zoneId != null && zoneId != 0) {
            predicates.add(criteriaBuilder.equal(zoneJoin.get("id"), zoneId));
        }
        if (divisionId != null && divisionId != 0) {
            predicates.add(criteriaBuilder.equal(divisionJoin.get("id"), divisionId));
        }

        // Add predicate for locoTypeJoin.id = 7 or 9
        Predicate locoTypePredicate = criteriaBuilder.or(
                criteriaBuilder.equal(locoTypeJoin.get("id"), 7),
                criteriaBuilder.equal(locoTypeJoin.get("id"), 9)
        );
        predicates.add(locoTypePredicate);

        // Add predicate for the specified month
        predicates.add(criteriaBuilder.equal(criteriaBuilder.function("MONTH", Integer.class, inspectionRoot.get("tripDate")), month));
        // Add predicate for the specified year
        if (year != null) {
            predicates.add(criteriaBuilder.equal(criteriaBuilder.function("YEAR", Integer.class, inspectionRoot.get("tripDate")), year));
        }

        // Combine all predicates with AND
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        // Select COUNT(DISTINCT CONCAT(tripNo, divisionId))
        Expression<String> uniqueTripExpression = criteriaBuilder.concat(
                criteriaBuilder.concat(
                        criteriaBuilder.literal(""),
                        inspectionRoot.get("tripNo").as(String.class)
                ),
                divisionJoin.get("id").as(String.class)
        );

        criteriaQuery.select(criteriaBuilder.countDistinct(uniqueTripExpression));

        // Execute query and return result
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    public long getUniqueTripCountByZoneDivisionAndMonth(Long zoneId, Long divisionId, int month, Integer year) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Invalid month. Please provide a value between 1 and 12.");
        }

        // Create CriteriaBuilder
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        // Create CriteriaQuery for count
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        // Define Root for TcasBreakingInspection
        Root<TcasBreakingInspection> inspectionRoot = criteriaQuery.from(TcasBreakingInspection.class);

        // Join Division and Zone
        Join<TcasBreakingInspection, Division> divisionJoin = inspectionRoot.join("division");
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone");

        // Define predicates
        List<Predicate> predicates = new ArrayList<>();

        // Add predicates for zone and division if provided
        if (zoneId != null && zoneId != 0) {
            predicates.add(criteriaBuilder.equal(zoneJoin.get("id"), zoneId));
        }
        if (divisionId != null && divisionId != 0) {
            predicates.add(criteriaBuilder.equal(divisionJoin.get("id"), divisionId));
        }

        // Add predicate for the specified month
        predicates.add(criteriaBuilder.equal(criteriaBuilder.function("MONTH", Integer.class, inspectionRoot.get("tripDate")), month));

        // Add predicate for the specified year
        if (year != null) {
            predicates.add(criteriaBuilder.equal(criteriaBuilder.function("YEAR", Integer.class, inspectionRoot.get("tripDate")), year));
        }

        // Combine all predicates with AND
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        // Select COUNT(DISTINCT CONCAT(tripNo, divisionId))
        Expression<String> uniqueTripExpression = criteriaBuilder.concat(
                criteriaBuilder.concat(
                        criteriaBuilder.literal(""),
                        inspectionRoot.get("tripNo").as(String.class)
                ),
                divisionJoin.get("id").as(String.class)
        );

        criteriaQuery.select(criteriaBuilder.countDistinct(uniqueTripExpression));

        // Execute query and return result
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }


    public Integer getLargestTripNoInSeptember(Long zoneId, Long divisionId, int month) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Integer> query = cb.createQuery(Integer.class);

        Root<TcasBreakingInspection> root = query.from(TcasBreakingInspection.class);
        // Join division from Incident
        Join<TcasBreakingInspection, Division> divisionJoin = root.join("division");
        // Join zone from Division
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone");

        query.select(cb.max(root.get("tripNo")));

        // Predicates: Filters for issueCategory name and date range
        List<Predicate> predicates = new ArrayList<>();

        if (divisionId != null && divisionId != 0) {
            predicates.add(cb.equal(divisionJoin.get("id"), divisionId));
        }
        if (zoneId != null && zoneId != 0) {
            predicates.add(cb.equal(zoneJoin.get("id"), zoneId));
        }

        predicates.add(cb.equal(cb.function("MONTH", Integer.class, root.get("tripDate")), month));
        // Add predicates to the query
        query.where(cb.and(predicates.toArray(new Predicate[0])));

        return entityManager.createQuery(query).getSingleResult();
    }

    public Long getCountByMonthAndIssueCategoryName(Long zoneId, Long divisionId, int month, String issueCategoryName, Integer year) {
        // Initialize CriteriaBuilder
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);

        // Define the Root for the main table
        Root<TcasBreakingInspection> root = query.from(TcasBreakingInspection.class);

        // Define the Join with IssueCategory
        Join<TcasBreakingInspection, IssueCategory> issueCategoryJoin = root.join("issueCategory");
        // Join division from Incident
        Join<TcasBreakingInspection, Division> divisionJoin = root.join("division");
        // Join zone from Division
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone");

        // Select COUNT(t.id)
        query.select(cb.count(root.get("id")));

        // Define Predicates for filtering
        List<Predicate> predicates = new ArrayList<>();

        if (divisionId != null && divisionId != 0) {
            predicates.add(cb.equal(divisionJoin.get("id"), divisionId));
        }
        if (zoneId != null && zoneId != 0) {
            predicates.add(cb.equal(zoneJoin.get("id"), zoneId));
        }
        // Predicate for filtering by month
        Predicate monthPredicate = cb.equal(cb.function("MONTH", Integer.class, root.get("tripDate")), month);
        predicates.add(monthPredicate);
        // Add predicate for the specified year
        if (year != null) {
            Predicate yearPredicate = cb.equal(cb.function("YEAR", Integer.class, root.get("tripDate")), year);
            predicates.add(yearPredicate);
        }


        // Predicate for filtering by IssueCategory name
        Predicate issueCategoryNamePredicate = cb.equal(issueCategoryJoin.get("name"), issueCategoryName);
        predicates.add(issueCategoryNamePredicate);

        // Combine predicates with AND
        query.where(cb.and(predicates.toArray(new Predicate[0])));

        // Execute the query
        return entityManager.createQuery(query).getSingleResult();
    }


}
