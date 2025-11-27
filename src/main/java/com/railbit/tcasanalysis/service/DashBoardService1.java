package com.railbit.tcasanalysis.service;

import com.railbit.tcasanalysis.DTO.LastTripDateNoDTO;
import com.railbit.tcasanalysis.DTO.dashboard.OpenTicketWithOEMDTO;
import com.railbit.tcasanalysis.entity.*;
import com.railbit.tcasanalysis.entity.analysis.StringAndCount;
import com.railbit.tcasanalysis.entity.loco.Loco;
import com.railbit.tcasanalysis.repository.IncidentTicketRepo;
import com.railbit.tcasanalysis.util.HelpingHand;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DashBoardService1 {

    private static final Logger log = LogManager.getLogger(DashBoardService1.class);
    @Autowired
    EntityManager entityManager;
    private final IncidentTicketRepo incidentTicketRepo;
    private final DivisionService divisionService;

    public LocalDate getLastTripDateByZoneAndDivision(Long zoneId, Long divisionId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<LocalDate> query = cb.createQuery(LocalDate.class);
        Root<TcasBreakingInspection> root = query.from(TcasBreakingInspection.class);

        log.info("Division {}",divisionId);
        // Build predicates for zoneId and divisionId
        List<Predicate> predicates = new ArrayList<>();
        if (zoneId != null && zoneId != 0) {
            predicates.add(cb.equal(root.get("division").get("zone").get("id"), zoneId));
        }
        if (divisionId != null && divisionId != 0) {
            predicates.add(cb.equal(root.get("division").get("id"), divisionId));
        }

        // Cast tripDate to an Expression<LocalDate>
        Expression<LocalDate> tripDateExpression = root.get("tripDate").as(LocalDate.class);

        // Select the maximum tripDate
        query.select(cb.greatest(tripDateExpression));

        // Apply predicates
        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        // Execute query and handle no results
        try {
            return entityManager.createQuery(query).getSingleResult();
        } catch (NoResultException e) {
            return null; // Return null or handle as needed
        }
    }

    public List<StringAndCount> getDashboardCounts(Long zoneId, Long divisionId,Long firmId, LocalDate fromDate, LocalDate toDate, String roleName) {

        List<StringAndCount> dashboardCounts = new ArrayList<>();

        {
            StringAndCount stringAndCount = new StringAndCount();
            stringAndCount.setName("Total Incidents");
            stringAndCount.setCount(Math.toIntExact(getCountByDivisionAndZone(zoneId,divisionId,firmId,fromDate,toDate)));
            dashboardCounts.add(stringAndCount);
        }
        {

            StringAndCount stringAndCount = new StringAndCount();
            stringAndCount.setName("Incidents Without Attached Ticket");
            stringAndCount.setCount(Math.toIntExact(getCountByDivisionAndZoneAndWithoutTicket(zoneId,divisionId,firmId,fromDate,toDate)));
            dashboardCounts.add(stringAndCount);
        }
        {

            StringAndCount stringAndCount = new StringAndCount();
            stringAndCount.setName("Incidents With Attached Ticket");
            stringAndCount.setCount(Math.toIntExact(getCountByDivisionAndZoneAndTicketStatus(zoneId,divisionId,firmId,fromDate,toDate,null)));
            dashboardCounts.add(stringAndCount);
        }
//        {
//
//            StringAndCount stringAndCount = new StringAndCount();
//            stringAndCount.setName("Incidents Attached With Open Tickets");
//            stringAndCount.setCount(Math.toIntExact(getCountByDivisionAndZoneAndTicketStatus(zoneId,divisionId,firmId,fromDate,toDate,true)));
//            dashboardCounts.add(stringAndCount);
//        }
//        {
//
//            StringAndCount stringAndCount = new StringAndCount();
//            stringAndCount.setName("Incidents Attached With Closed Tickets");
//            stringAndCount.setCount(Math.toIntExact(getCountByDivisionAndZoneAndTicketStatus(zoneId,divisionId,firmId,fromDate,toDate,false)));
//            dashboardCounts.add(stringAndCount);
//        }
        {
            //Total Incident Counts
            StringAndCount stringAndCount = new StringAndCount();
            stringAndCount.setName("Total Unique Tickets Generated");
            stringAndCount.setCount(Math.toIntExact(getCountOfIncidentTicketsByZoneAndDivision(zoneId, divisionId, firmId, fromDate, toDate, null)));
            dashboardCounts.add(stringAndCount);
        }
        {
            //Total Open Incident Counts
            StringAndCount stringAndCount = new StringAndCount();
            stringAndCount.setName("Open Unique Tickets");
            stringAndCount.setCount(Math.toIntExact(getCountOfIncidentTicketsByZoneAndDivision(zoneId, divisionId, firmId, fromDate, toDate, true)));
            dashboardCounts.add(stringAndCount);
        }
        {
            //Total Closed Incident Counts
            StringAndCount stringAndCount = new StringAndCount();
            stringAndCount.setName("Closed Unique Tickets");
            stringAndCount.setCount(Math.toIntExact(getCountOfIncidentTicketsByZoneAndDivision(zoneId, divisionId, firmId, fromDate, toDate, false)));
            dashboardCounts.add(stringAndCount);
        }

        if (!roleName.equalsIgnoreCase("ROLE_OEM")){

//            {
//                StringAndCount stringAndCount = new StringAndCount();
//                stringAndCount.setName("Total Trips");
//                stringAndCount.setCount(Math.toIntExact(getUniqueTripCountByDivisionAndZone(zoneId, divisionId, fromDate, toDate,"")));
//                dashboardCounts.add(stringAndCount);
//            }
//            {
//                StringAndCount stringAndCount = new StringAndCount();
//                stringAndCount.setName("Trip With Issues");
//                stringAndCount.setCount(Math.toIntExact(getUniqueTripCountByDivisionAndZone(zoneId, divisionId, fromDate, toDate,"With Issue")));
//                dashboardCounts.add(stringAndCount);
//            }
//            {
//                StringAndCount stringAndCount = new StringAndCount();
//                stringAndCount.setName("Trip Without Issues");
//                stringAndCount.setCount(Math.toIntExact(getUniqueTripCountByDivisionAndZone(zoneId, divisionId, fromDate, toDate,"No Issue")));
//                dashboardCounts.add(stringAndCount);
//            }
        }

//        {
//
//            StringAndCount stringAndCount = new StringAndCount();
//            stringAndCount.setName("Total Users");
//            stringAndCount.setCount(Math.toIntExact(userRepo.count()));
//            dashboardCounts.add(stringAndCount);
//        }


        return dashboardCounts;
    }

    //Get Total incident count filter wise
    public long getCountByDivisionAndZone(
            Long zoneId,
            Long divisionId,
            Long firmId,
            LocalDate fromDate,
            LocalDate toDate) {
        // Create CriteriaBuilder
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        // Create CriteriaQuery
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        // Define Root for TcasBreakingInspection
        Root<TcasBreakingInspection> inspectionRoot = criteriaQuery.from(TcasBreakingInspection.class);

        // Join Division and Zone
        Join<TcasBreakingInspection, Division> divisionJoin = inspectionRoot.join("division");
        Join<TcasBreakingInspection, IssueCategory> issueCategoryJoin = inspectionRoot.join("issueCategory");
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone");
        Join<TcasBreakingInspection, PossibleRootCause> possibleRootCauseJoin = inspectionRoot.join("possibleRootCause", JoinType.LEFT);
        Join<TcasBreakingInspection, RootCauseSubCategory> rootCauseSubCategoryJoin = inspectionRoot.join("rootCauseSubCategory", JoinType.LEFT);

        // Select COUNT(t.id)
        criteriaQuery.select(criteriaBuilder.count(inspectionRoot.get("id")));

        // Build WHERE clause
        List<Predicate> predicates = new ArrayList<>();
        // Handle `notEqual` with `NULL` values
        Predicate rootCauseSubCategoryPredicate = criteriaBuilder.or(
                criteriaBuilder.notEqual(rootCauseSubCategoryJoin.get("name"), "ALTERATION WORK"),
                rootCauseSubCategoryJoin.get("name").isNull()
        );

        Predicate possibleRootCausePredicate = criteriaBuilder.or(
                criteriaBuilder.notEqual(possibleRootCauseJoin.get("name"), "Alterations"),
                possibleRootCauseJoin.get("name").isNull()
        );

        // Add predicates to the list
//        predicates.add(rootCauseSubCategoryPredicate);
//        predicates.add(possibleRootCausePredicate);

        // Add predicates conditionally
        if (divisionId != null && divisionId != 0) {
            predicates.add(criteriaBuilder.equal(divisionJoin.get("id"), divisionId));
        }
        if (zoneId != null && zoneId != 0) {
            predicates.add(criteriaBuilder.equal(zoneJoin.get("id"), zoneId));
        }

        // Firm filter
        if (firmId != null && firmId != 0) {
            Predicate predicate1 = criteriaBuilder.equal(inspectionRoot.get("loco").get("firm").get("id"), firmId);
            Predicate predicate2 = criteriaBuilder.equal(inspectionRoot.get("faultyStation").get("firm").get("id"), firmId);
            predicates.add(criteriaBuilder.or(predicate1, predicate2));
        }

        if (fromDate != null && toDate != null) {
            predicates.add(criteriaBuilder.between(inspectionRoot.get("tripDate"), fromDate, toDate));
        }

        // Add predicate for issueCategory.name != "No Issue"
        predicates.add(criteriaBuilder.notEqual(issueCategoryJoin.get("name"), "No Issue"));

        // Combine all predicates with AND
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        // Execute query
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    //get Total incidents with ticket status
    public long getCountByDivisionAndZoneAndTicketStatus(Long zoneId, Long divisionId,Long firmId, LocalDate fromDate, LocalDate toDate, Boolean ticketStatus) {
        // Create CriteriaBuilder
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        // Create CriteriaQuery
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        // Define Root for TcasBreakingInspection
        Root<TcasBreakingInspection> inspectionRoot = criteriaQuery.from(TcasBreakingInspection.class);

        // Join Division and Zone
        Join<TcasBreakingInspection, Division> divisionJoin = inspectionRoot.join("division");
        Join<TcasBreakingInspection, IncidentTicket> ticketJoin = inspectionRoot.join("incidentTicket");
        Join<TcasBreakingInspection, IssueCategory> issueCategoryJoin = inspectionRoot.join("issueCategory");
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone");
//        Join<IncidentTicket, Firm> firmJoin = ticketJoin.join("assignedFirms",JoinType.LEFT);
        Join<TcasBreakingInspection, PossibleRootCause> possibleRootCauseJoin = inspectionRoot.join("possibleRootCause", JoinType.LEFT);
        Join<TcasBreakingInspection, RootCauseSubCategory> rootCauseSubCategoryJoin = inspectionRoot.join("rootCauseSubCategory", JoinType.LEFT);

        // Select COUNT(t.id)
        criteriaQuery.select(criteriaBuilder.count(inspectionRoot.get("id")));

        // Build WHERE clause
        List<Predicate> predicates = new ArrayList<>();
        // Handle `notEqual` with `NULL` values
        Predicate rootCauseSubCategoryPredicate = criteriaBuilder.or(
                criteriaBuilder.notEqual(rootCauseSubCategoryJoin.get("name"), "ALTERATION WORK"),
                rootCauseSubCategoryJoin.get("name").isNull()
        );

        Predicate possibleRootCausePredicate = criteriaBuilder.or(
                criteriaBuilder.notEqual(possibleRootCauseJoin.get("name"), "Alterations"),
                possibleRootCauseJoin.get("name").isNull()
        );

        // Add predicates to the list
//        predicates.add(rootCauseSubCategoryPredicate);
//        predicates.add(possibleRootCausePredicate);

        predicates.add(criteriaBuilder.isNotNull(inspectionRoot.get("incidentTicket")));
        // Add predicates conditionally
        if (divisionId != null && divisionId != 0) {
            predicates.add(criteriaBuilder.equal(divisionJoin.get("id"), divisionId));
        }
        if (zoneId != null && zoneId != 0) {
            predicates.add(criteriaBuilder.equal(zoneJoin.get("id"), zoneId));
        }
        // Firm filter
        // Filter by firmId if provided
        if (firmId != null && firmId != 0) {
//            predicates.add(criteriaBuilder.equal(firmJoin.get("id"), firmId));
        }
        if (ticketStatus != null) {
            predicates.add(criteriaBuilder.equal(ticketJoin.get("status"), ticketStatus));
        }
        if (fromDate != null && toDate != null) {
            predicates.add(criteriaBuilder.between(inspectionRoot.get("tripDate"), fromDate, toDate));
        }

        // Add predicate for issueCategory.name != "No Issue"
        predicates.add(criteriaBuilder.notEqual(issueCategoryJoin.get("name"), "No Issue"));

        // Combine all predicates with AND
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        // Execute query
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    //get Total incidents without ticket
    public long getCountByDivisionAndZoneAndWithoutTicket(Long zoneId, Long divisionId,Long firmId, LocalDate fromDate, LocalDate toDate) {
        // Create CriteriaBuilder
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        // Create CriteriaQuery
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        // Define Root for TcasBreakingInspection
        Root<TcasBreakingInspection> inspectionRoot = criteriaQuery.from(TcasBreakingInspection.class);

        // Join Division and Zone
        Join<TcasBreakingInspection, Division> divisionJoin = inspectionRoot.join("division");
        Join<TcasBreakingInspection, IssueCategory> issueCategoryJoin = inspectionRoot.join("issueCategory");
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone");
        Join<TcasBreakingInspection, PossibleRootCause> possibleRootCauseJoin = inspectionRoot.join("possibleRootCause", JoinType.LEFT);
        Join<TcasBreakingInspection, RootCauseSubCategory> rootCauseSubCategoryJoin = inspectionRoot.join("rootCauseSubCategory", JoinType.LEFT);

        // Select COUNT(t.id)
        criteriaQuery.select(criteriaBuilder.count(inspectionRoot.get("id")));

        // Build WHERE clause
        List<Predicate> predicates = new ArrayList<>();

        // Handle `notEqual` with `NULL` values
        Predicate rootCauseSubCategoryPredicate = criteriaBuilder.or(
                criteriaBuilder.notEqual(rootCauseSubCategoryJoin.get("name"), "ALTERATION WORK"),
                rootCauseSubCategoryJoin.get("name").isNull()
        );

        Predicate possibleRootCausePredicate = criteriaBuilder.or(
                criteriaBuilder.notEqual(possibleRootCauseJoin.get("name"), "Alterations"),
                possibleRootCauseJoin.get("name").isNull()
        );

        // Add predicates to the list
//        predicates.add(rootCauseSubCategoryPredicate);
//        predicates.add(possibleRootCausePredicate);

        predicates.add(criteriaBuilder.isNull(inspectionRoot.get("incidentTicket")));

        // Add predicates conditionally
        if (divisionId != null && divisionId != 0) {
            predicates.add(criteriaBuilder.equal(divisionJoin.get("id"), divisionId));
        }
        if (zoneId != null && zoneId != 0) {
            predicates.add(criteriaBuilder.equal(zoneJoin.get("id"), zoneId));
        }
        // Firm filter
        if (firmId != null && firmId != 0) {
            Predicate predicate1 = criteriaBuilder.equal(inspectionRoot.get("loco").get("firm").get("id"), firmId);
            Predicate predicate2 = criteriaBuilder.equal(inspectionRoot.get("faultyStation").get("firm").get("id"), firmId);
            predicates.add(criteriaBuilder.or(predicate1, predicate2));
        }
        if (fromDate != null && toDate != null) {
            predicates.add(criteriaBuilder.between(inspectionRoot.get("tripDate"), fromDate, toDate));
        }

        // Add predicate for issueCategory.name != "No Issue"
        predicates.add(criteriaBuilder.notEqual(issueCategoryJoin.get("name"), "No Issue"));

        // Combine all predicates with AND
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        // Execute query
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    //Get Total incident With No Isse count filter wise
    public long getNoIssueCountByDivisionAndZone(Long zoneId, Long divisionId, LocalDate fromDate, LocalDate toDate) {
        // Create CriteriaBuilder
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        // Create CriteriaQuery
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        // Define Root for TcasBreakingInspection
        Root<TcasBreakingInspection> inspectionRoot = criteriaQuery.from(TcasBreakingInspection.class);

        // Join Division and Zone
        Join<TcasBreakingInspection, Division> divisionJoin = inspectionRoot.join("division");
        Join<TcasBreakingInspection, IssueCategory> issueCategoryJoin = inspectionRoot.join("issueCategory");
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone");

        // Select COUNT(t.id)
        criteriaQuery.select(criteriaBuilder.count(inspectionRoot.get("id")));

        // Build WHERE clause
        List<Predicate> predicates = new ArrayList<>();

        // Add predicates conditionally
        if (divisionId != null && divisionId != 0) {
            predicates.add(criteriaBuilder.equal(divisionJoin.get("id"), divisionId));
        }
        if (zoneId != null && zoneId != 0) {
            predicates.add(criteriaBuilder.equal(zoneJoin.get("id"), zoneId));
        }
        if (fromDate != null && toDate != null) {
            predicates.add(criteriaBuilder.between(inspectionRoot.get("tripDate"), fromDate, toDate));
        }

        // Add predicate for issueCategory.name != "No Issue"
        predicates.add(criteriaBuilder.equal(issueCategoryJoin.get("name"), "No Issue"));

        // Combine all predicates with AND
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        // Execute query
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    // Get Total Unique tripNo Count Between Dates
    // Get Total Unique tripNo Count By Division and Zone Between Dates
    public long getUniqueTripCountByDivisionAndZone(Long zoneId, Long divisionId, LocalDate fromDate, LocalDate toDate, String issueStatus) {
        // Ensure valid date range
        if (fromDate == null || toDate == null || fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("Invalid date range");
        }

        // Create CriteriaBuilder
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        // Create CriteriaQuery for count
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        // Define Root for TcasBreakingInspection
        Root<TcasBreakingInspection> inspectionRoot = criteriaQuery.from(TcasBreakingInspection.class);

        // Join Division and Zone
        Join<TcasBreakingInspection, IssueCategory> issueCategoryJoin = inspectionRoot.join("issueCategory");
        Join<TcasBreakingInspection, Division> divisionJoin = inspectionRoot.join("division");
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone");

        // Initialize total trips count
        long totalTrips = 0;

        // Iterate through months between fromDate and toDate
        LocalDate currentMonthStart = fromDate.withDayOfMonth(1);
        while (!currentMonthStart.isAfter(toDate)) {
            // Define the end of the current month or limit it to toDate
            LocalDate currentMonthEnd = currentMonthStart.withDayOfMonth(currentMonthStart.lengthOfMonth());
            if (currentMonthEnd.isAfter(toDate)) {
                currentMonthEnd = toDate;
            }

            // Build WHERE clause
            List<Predicate> predicates = new ArrayList<>();

            // Add predicates conditionally
            if (divisionId != null && divisionId != 0) {
                predicates.add(criteriaBuilder.equal(divisionJoin.get("id"), divisionId));
            }
            if (zoneId != null && zoneId != 0) {
                predicates.add(criteriaBuilder.equal(zoneJoin.get("id"), zoneId));
            }
            if (!StringUtils.isEmpty(issueStatus)) {
                if (issueStatus.equalsIgnoreCase("no issue")) {
                    predicates.add(criteriaBuilder.equal(issueCategoryJoin.get("name"), "No Issue"));
                } else {
                    predicates.add(criteriaBuilder.notEqual(issueCategoryJoin.get("name"), "No Issue"));
                }
            }
            // Add date range for the current month
            predicates.add(criteriaBuilder.between(inspectionRoot.get("tripDate"), currentMonthStart, currentMonthEnd));

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

            // Execute query and accumulate results
            totalTrips += entityManager.createQuery(criteriaQuery).getSingleResult();

            // Move to the next month
            currentMonthStart = currentMonthStart.plusMonths(1).withDayOfMonth(1);
        }

        return totalTrips;
    }

    public long getCountOfIncidentTicketsByZoneAndDivision(Long zoneId, Long divisionId, Long firmId, LocalDate fromDate, LocalDate toDate, Boolean ticketStatus) {
        // Create CriteriaBuilder
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        // Create CriteriaQuery
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        // Define Root for TcasBreakingInspection
        Root<TcasBreakingInspection> inspectionRoot = criteriaQuery.from(TcasBreakingInspection.class);

        // Join Division and Zone
        Join<TcasBreakingInspection, Division> divisionJoin = inspectionRoot.join("division");
        Join<TcasBreakingInspection, IncidentTicket> ticketJoin = inspectionRoot.join("incidentTicket");
        Join<TcasBreakingInspection, IssueCategory> issueCategoryJoin = inspectionRoot.join("issueCategory");
        Join<TcasBreakingInspection, PossibleRootCause> possibleRootCauseJoin = inspectionRoot.join("possibleRootCause", JoinType.LEFT);
        Join<TcasBreakingInspection, RootCauseSubCategory> rootCauseSubCategoryJoin = inspectionRoot.join("rootCauseSubCategory", JoinType.LEFT);
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone");

        // Select COUNT DISTINCT on IncidentTicket
        criteriaQuery.select(criteriaBuilder.countDistinct(ticketJoin.get("id")));

        // Build WHERE clause
        List<Predicate> predicates = new ArrayList<>();

        // Handle `notEqual` with `NULL` values
        Predicate rootCauseSubCategoryPredicate = criteriaBuilder.or(
                criteriaBuilder.notEqual(rootCauseSubCategoryJoin.get("name"), "ALTERATION WORK"),
                rootCauseSubCategoryJoin.get("name").isNull()
        );

        Predicate possibleRootCausePredicate = criteriaBuilder.or(
                criteriaBuilder.notEqual(possibleRootCauseJoin.get("name"), "Alterations"),
                possibleRootCauseJoin.get("name").isNull()
        );

        // Add predicates to the list
//        predicates.add(rootCauseSubCategoryPredicate);
//        predicates.add(possibleRootCausePredicate);

        // Add predicates conditionally
        if (divisionId != null && divisionId != 0) {
            predicates.add(criteriaBuilder.equal(divisionJoin.get("id"), divisionId));
        }
        if (zoneId != null && zoneId != 0) {
            predicates.add(criteriaBuilder.equal(zoneJoin.get("id"), zoneId));
        }
        // Firm filter
        // Filter by firmId if provided
        if (firmId != null && firmId != 0) {
//            predicates.add(criteriaBuilder.equal(firmJoin.get("id"), firmId));
        }
        if (ticketStatus != null) {
            predicates.add(criteriaBuilder.equal(ticketJoin.get("status"), ticketStatus));
        }
        if (fromDate != null && toDate != null) {
            predicates.add(criteriaBuilder.between(inspectionRoot.get("tripDate"), fromDate, toDate));
        }

        // Add predicate for issueCategory.name != "No Issue"
        predicates.add(criteriaBuilder.notEqual(issueCategoryJoin.get("name"), "No Issue"));

        // Combine all predicates with AND
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        // Execute query
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    public List<StringAndCount> findTopStationsWithMostIssuesBetweenDates(Long zoneId, Long divisionId,Long firmId, LocalDate fromDate, LocalDate toDate, Pageable pageable) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<TcasBreakingInspection> root = query.from(TcasBreakingInspection.class);

        // Join with related entities
        Join<TcasBreakingInspection, Station> faultyStationJoin = root.join("faultyStation", JoinType.INNER);
        Join<TcasBreakingInspection, IssueCategory> issueCategoryJoin = root.join("issueCategory", JoinType.INNER);
        Join<TcasBreakingInspection, PossibleRootCause> possibleRootCauseJoin = root.join("possibleRootCause", JoinType.LEFT);
        Join<TcasBreakingInspection, RootCauseSubCategory> rootCauseSubCategoryJoin = root.join("rootCauseSubCategory", JoinType.LEFT);

        // Join division from Incident
        Join<TcasBreakingInspection, Division> divisionJoin = root.join("division");
        // Join zone from Division
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone");

        // Selection: Select station code and count
        query.multiselect(
                faultyStationJoin.get("code").alias("name"),
                cb.count(root).alias("count")
        );

        // Predicates: Filters for issueCategory name and date range
        List<Predicate> predicates = new ArrayList<>();

        // Handle `notEqual` with `NULL` values
        Predicate rootCauseSubCategoryPredicate = cb.or(
                cb.notEqual(rootCauseSubCategoryJoin.get("name"), "ALTERATION WORK"),
                rootCauseSubCategoryJoin.get("name").isNull()
        );

        Predicate possibleRootCausePredicate = cb.or(
                cb.notEqual(possibleRootCauseJoin.get("name"), "Alterations"),
                possibleRootCauseJoin.get("name").isNull()
        );

        // Add predicates to the list
//        predicates.add(rootCauseSubCategoryPredicate);
//        predicates.add(possibleRootCausePredicate);

        predicates.add(cb.notEqual(issueCategoryJoin.get("name"), "No Issue"));

        predicates.add(cb.or(
                cb.equal(possibleRootCauseJoin.get("name"), "Station Kavach"),
                cb.equal(possibleRootCauseJoin.get("name"), "Kavach Station Related")
        ));

        if (divisionId != null && divisionId != 0) {
            predicates.add(cb.equal(divisionJoin.get("id"), divisionId));
        }
        if (zoneId != null && zoneId != 0) {
            predicates.add(cb.equal(zoneJoin.get("id"), zoneId));
        }
        // Firm filter
        if (firmId != null && firmId != 0) {
            Predicate predicate1 = cb.equal(root.get("loco").get("firm").get("id"), firmId);
            Predicate predicate2 = cb.equal(root.get("faultyStation").get("firm").get("id"), firmId);
            predicates.add(cb.or(predicate1, predicate2));
        }
        if (fromDate != null && toDate != null) {
            predicates.add(cb.between(root.get("tripDate"), fromDate, toDate));
        }

        // Add predicates to the query
        query.where(cb.and(predicates.toArray(new Predicate[0])));

        // Group by station code
        query.groupBy(faultyStationJoin.get("code"));

        // Order by total count descending
        query.orderBy(cb.desc(cb.count(root)));

        // Create and apply pagination
        TypedQuery<Tuple> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        // Fetch results and map to StringAndCount
        List<Tuple> tuples = typedQuery.getResultList();
//        log.info("Tuples : {}", tuples);
        List<StringAndCount> results = new ArrayList<>();
        long topStationsCount = 0;
        for (Tuple tuple : tuples) {
            String name = tuple.get("name", String.class);
            Long count = tuple.get("count", Long.class);
            topStationsCount += count;
//            log.info("Name {}", name);
//            log.info("Count {}", count);
            // Logic to determine colorCode (this is an example)
            String colorCode = HelpingHand.getRandomColor(); // Red if count > 100, else Green

            // Create and add StringAndCount instance
            results.add(new StringAndCount(name, count.intValue(), colorCode));

        }

        long totalIncidents = getTotalStationKavachIncidentsCount(zoneId, divisionId, firmId, fromDate, toDate);
        long othersCount = totalIncidents - topStationsCount;
        // Add "Others" to the result list
        if (othersCount > 0) {
//            results.add(new StringAndCount("Others", (int) othersCount, HelpingHand.getRandomColor()));
        }

        return results;
    }

    public long getTotalStationKavachIncidentsCount(Long zoneId, Long divisionId, Long firmId, LocalDate fromDate, LocalDate toDate) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<TcasBreakingInspection> root = query.from(TcasBreakingInspection.class);

        // Join with related entities
        Join<TcasBreakingInspection, Station> faultyStationJoin = root.join("faultyStation", JoinType.INNER);
        Join<TcasBreakingInspection, IssueCategory> issueCategoryJoin = root.join("issueCategory", JoinType.INNER);
        Join<TcasBreakingInspection, PossibleRootCause> possibleRootCauseJoin = root.join("possibleRootCause", JoinType.LEFT);
        Join<TcasBreakingInspection, RootCauseSubCategory> rootCauseSubCategoryJoin = root.join("rootCauseSubCategory", JoinType.LEFT);

        // Join division from Incident
        Join<TcasBreakingInspection, Division> divisionJoin = root.join("division");
        // Join zone from Division
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone");

        // Selection: Count total inspections
        query.select(cb.count(root));

        // Predicates: Filters for issueCategory name and date range
        List<Predicate> predicates = new ArrayList<>();

        // Handle `notEqual` with `NULL` values
        Predicate rootCauseSubCategoryPredicate = cb.or(
                cb.notEqual(rootCauseSubCategoryJoin.get("name"), "ALTERATION WORK"),
                rootCauseSubCategoryJoin.get("name").isNull()
        );

        Predicate possibleRootCausePredicate = cb.or(
                cb.notEqual(possibleRootCauseJoin.get("name"), "Alterations"),
                possibleRootCauseJoin.get("name").isNull()
        );

        // Add predicates to the list
        predicates.add(cb.notEqual(issueCategoryJoin.get("name"), "No Issue"));

        predicates.add(cb.or(
                cb.equal(possibleRootCauseJoin.get("name"), "Station Kavach"),
                cb.equal(possibleRootCauseJoin.get("name"), "Kavach Station Related")
        ));

        if (divisionId != null && divisionId != 0) {
            predicates.add(cb.equal(divisionJoin.get("id"), divisionId));
        }
        if (zoneId != null && zoneId != 0) {
            predicates.add(cb.equal(zoneJoin.get("id"), zoneId));
        }
        // Firm filter
        if (firmId != null && firmId != 0) {
            Predicate predicate1 = cb.equal(root.get("loco").get("firm").get("id"), firmId);
            Predicate predicate2 = cb.equal(root.get("faultyStation").get("firm").get("id"), firmId);
            predicates.add(cb.or(predicate1, predicate2));
        }
        if (fromDate != null && toDate != null) {
            predicates.add(cb.between(root.get("tripDate"), fromDate, toDate));
        }

        // Add predicates to the query
        query.where(cb.and(predicates.toArray(new Predicate[0])));

        // Execute query
        return entityManager.createQuery(query).getSingleResult();
    }

    public List<StringAndCount> findTopLocosWithMostIssuesBetweenDates(Long zoneId, Long divisionId, Long firmId, LocalDate fromDate, LocalDate toDate, Pageable pageable) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<TcasBreakingInspection> root = query.from(TcasBreakingInspection.class);

        // Join with related entities
        Join<TcasBreakingInspection, Loco> locoJoin = root.join("loco", JoinType.INNER);
        Join<TcasBreakingInspection, IssueCategory> issueCategoryJoin = root.join("issueCategory", JoinType.LEFT);
        Join<TcasBreakingInspection, PossibleRootCause> possibleRootCauseJoin = root.join("possibleRootCause", JoinType.LEFT);

        // Join division from Incident
        Join<TcasBreakingInspection, Division> divisionJoin = root.join("division");
        // Join zone from Division
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone");

        // Selection: Select station code and count
        query.multiselect(
                locoJoin.get("locoNo").alias("name"),
                cb.count(root).alias("count")
        );

        // Predicates: Filters for issueCategory name and date range
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.notEqual(issueCategoryJoin.get("name"), "No Issue"));

        predicates.add(cb.or(
                cb.equal(possibleRootCauseJoin.get("name"), "Onboard Kavach"),
                cb.equal(possibleRootCauseJoin.get("name"), "Kavach Loco Related")
        ));

        if (divisionId != null && divisionId != 0) {
            predicates.add(cb.equal(divisionJoin.get("id"), divisionId));
        }
        if (zoneId != null && zoneId != 0) {
            predicates.add(cb.equal(zoneJoin.get("id"), zoneId));
        }
        // Firm filter
        if (firmId != null && firmId != 0) {
            Predicate predicate1 = cb.equal(root.get("loco").get("firm").get("id"), firmId);
            Predicate predicate2 = cb.equal(root.get("faultyStation").get("firm").get("id"), firmId);
            predicates.add(cb.or(predicate1, predicate2));
        }
        if (fromDate != null && toDate != null) {
            predicates.add(cb.between(root.get("tripDate"), fromDate, toDate));
        }

        // Add predicates to the query
        query.where(cb.and(predicates.toArray(new Predicate[0])));

        // Group by station code
        query.groupBy(locoJoin.get("locoNo"));

        // Order by total count descending
        query.orderBy(cb.desc(cb.count(root)));

        // Create and apply pagination
        TypedQuery<Tuple> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        // Fetch results and map to StringAndCount
        List<Tuple> tuples = typedQuery.getResultList();
//        log.info("Tuples : {}", tuples);
        List<StringAndCount> results = new ArrayList<>();
        long topStationsCount = 0;
        for (Tuple tuple : tuples) {
            String name = tuple.get("name", String.class);
            Long count = tuple.get("count", Long.class);
            topStationsCount += count;

//            log.info("Name {}", name);
//            log.info("Count {}", count);
            // Logic to determine colorCode (this is an example)
            String colorCode = HelpingHand.getRandomColor(); // Red if count > 100, else Green

            // Create and add StringAndCount instance
            results.add(new StringAndCount(name, count.intValue(), colorCode));

        }

        long totalIncidents = getTotalOnboardKavachIncidentsCount(zoneId, divisionId, firmId, fromDate, toDate);
        long othersCount = totalIncidents - topStationsCount;
        // Add "Others" to the result list
        if (othersCount > 0) {
//            results.add(new StringAndCount("Others", (int) othersCount, HelpingHand.getRandomColor()));
        }

        return results;
    }

    public long getTotalOnboardKavachIncidentsCount(Long zoneId, Long divisionId, Long firmId, LocalDate fromDate, LocalDate toDate) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<TcasBreakingInspection> root = query.from(TcasBreakingInspection.class);

        // Join with related entities
        Join<TcasBreakingInspection, Loco> locoJoin = root.join("loco", JoinType.INNER);
        Join<TcasBreakingInspection, IssueCategory> issueCategoryJoin = root.join("issueCategory", JoinType.LEFT);
        Join<TcasBreakingInspection, PossibleRootCause> possibleRootCauseJoin = root.join("possibleRootCause", JoinType.LEFT);

        // Join division from Incident
        Join<TcasBreakingInspection, Division> divisionJoin = root.join("division");
        // Join zone from Division
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone");

        // Selection: Count total inspections
        query.select(cb.count(root));

        // Predicates: Filters for issueCategory name and date range
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.notEqual(issueCategoryJoin.get("name"), "No Issue"));

        predicates.add(cb.or(
                cb.equal(possibleRootCauseJoin.get("name"), "Onboard Kavach"),
                cb.equal(possibleRootCauseJoin.get("name"), "Kavach Loco Related")
        ));

        if (divisionId != null && divisionId != 0) {
            predicates.add(cb.equal(divisionJoin.get("id"), divisionId));
        }
        if (zoneId != null && zoneId != 0) {
            predicates.add(cb.equal(zoneJoin.get("id"), zoneId));
        }
        // Firm filter
        if (firmId != null && firmId != 0) {
            Predicate predicate1 = cb.equal(root.get("loco").get("firm").get("id"), firmId);
            Predicate predicate2 = cb.equal(root.get("faultyStation").get("firm").get("id"), firmId);
            predicates.add(cb.or(predicate1, predicate2));
        }
        if (fromDate != null && toDate != null) {
            predicates.add(cb.between(root.get("tripDate"), fromDate, toDate));
        }

        // Add predicates to the query
        query.where(cb.and(predicates.toArray(new Predicate[0])));

        // Execute query
        return entityManager.createQuery(query).getSingleResult();
    }

    public List<StringAndCount> findTopCausesWithMostIssuesBetweenDates(Long zoneId, Long divisionId, Long firmId, LocalDate fromDate, LocalDate toDate, Pageable pageable) {

//        log.info("Zone : {}",zoneId);
//        log.info("Division : {}",divisionId);
//        log.info("From Date : {}",fromDate);
//        log.info("To Date : {}",toDate);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<TcasBreakingInspection> root = query.from(TcasBreakingInspection.class);

        // Join with related entities
//        Join<TcasBreakingInspection, PossibleRootCause> rootCauseJoin = root.join("possibleRootCause", JoinType.INNER);
        Join<TcasBreakingInspection, IssueCategory> issueCategoryJoin = root.join("issueCategory", JoinType.INNER);
        Join<TcasBreakingInspection, RootCauseSubCategory> rootCauseSubCategoryJoin = root.join("rootCauseSubCategory", JoinType.INNER);
        Join<TcasBreakingInspection, PossibleRootCause> possibleRootCauseJoin = root.join("possibleRootCause", JoinType.LEFT);

        // Join division from Incident
        Join<TcasBreakingInspection, Division> divisionJoin = root.join("division");
        // Join zone from Division
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone");

        // Selection: Select station code and count
        query.multiselect(
                rootCauseSubCategoryJoin.get("name").alias("name"),
                cb.count(root).alias("count")
        );

        // Predicates: Filters for issueCategory name and date range
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.notEqual(issueCategoryJoin.get("name"), "No Issue"));
        predicates.add(cb.notEqual(rootCauseSubCategoryJoin.get("name"), "ALTERATION WORK"));
        // Handle `notEqual` with `NULL` values
//        Predicate rootCauseSubCategoryPredicate = cb.or(
//                cb.notEqual(rootCauseSubCategoryJoin.get("name"), "ALTERATION WORK"),
//                rootCauseSubCategoryJoin.get("name").isNull()
//        );

        Predicate possibleRootCausePredicate = cb.or(
                cb.notEqual(possibleRootCauseJoin.get("name"), "Alterations"),
                possibleRootCauseJoin.get("name").isNull()
        );

        // Add predicates to the list
//        predicates.add(possibleRootCausePredicate);

        if (divisionId != null && divisionId != 0) {
            predicates.add(cb.equal(divisionJoin.get("id"), divisionId));
        }
        if (zoneId != null && zoneId != 0) {
            predicates.add(cb.equal(zoneJoin.get("id"), zoneId));
        }

        // Firm filter
        if (firmId != null && firmId != 0) {
            Predicate predicate1 = cb.equal(root.get("loco").get("firm").get("id"), firmId);
            Predicate predicate2 = cb.equal(root.get("faultyStation").get("firm").get("id"), firmId);
            predicates.add(cb.or(predicate1, predicate2));
        }

        if (fromDate != null && toDate != null) {
            predicates.add(cb.between(root.get("tripDate"), fromDate, toDate));
        }

        // Add predicates to the query
        query.where(cb.and(predicates.toArray(new Predicate[0])));

        // Group by station code
        query.groupBy(rootCauseSubCategoryJoin.get("name"));

        // Order by total count descending
        query.orderBy(cb.desc(cb.count(root)));

        // Create and apply pagination
        TypedQuery<Tuple> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        // Fetch results and map to StringAndCount
        List<Tuple> tuples = typedQuery.getResultList();
//        log.info("Tuples : {}", tuples);
        List<StringAndCount> results = new ArrayList<>();
        long topStationsCount = 0;
        for (Tuple tuple : tuples) {
            String name = tuple.get("name", String.class);
            Long count = tuple.get("count", Long.class);
            topStationsCount += count;

//            log.info("Name {}", name);
//            log.info("Count {}", count);
            // Logic to determine colorCode (this is an example)
            String colorCode = HelpingHand.getRandomColor(); // Red if count > 100, else Green

            // Create and add StringAndCount instance
            results.add(new StringAndCount(name, count.intValue(), colorCode));

        }

        long totalIncidents = getCountByDivisionAndZone(zoneId, divisionId, firmId, fromDate, toDate);
        long othersCount = totalIncidents - topStationsCount;
        // Add "Others" to the result list
        if (othersCount > 0) {
//            results.add(new StringAndCount("Others", (int) othersCount, HelpingHand.getRandomColor()));
        }

        return results;
    }


    public List<StringAndCount> findTopDivisionWithMostIssuesBetweenDates(Long zoneId, Long divisionId, Long firmId, LocalDate fromDate, LocalDate toDate, Pageable pageable) {

//        log.info("Zone : {}",zoneId);
//        log.info("Division : {}",divisionId);
//        log.info("From Date : {}",fromDate);
//        log.info("To Date : {}",toDate);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<TcasBreakingInspection> root = query.from(TcasBreakingInspection.class);

        // Join with related entities
//        Join<TcasBreakingInspection, Station> faultyStationJoin = root.join("faultyStation", JoinType.INNER);
        Join<TcasBreakingInspection, IssueCategory> issueCategoryJoin = root.join("issueCategory", JoinType.INNER);
        Join<TcasBreakingInspection, RootCauseSubCategory> rootCauseSubCategoryJoin = root.join("rootCauseSubCategory", JoinType.LEFT);
        Join<TcasBreakingInspection, PossibleRootCause> possibleRootCauseJoin = root.join("possibleRootCause", JoinType.LEFT);

        // Join division from Incident
        Join<TcasBreakingInspection, Division> divisionJoin = root.join("division");
        // Join zone from Division
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone");

        // Selection: Select station code and count
        query.multiselect(
                divisionJoin.get("code").alias("name"),
                cb.count(root).alias("count")
        );

        // Predicates: Filters for issueCategory name and date range
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.notEqual(issueCategoryJoin.get("name"), "No Issue"));

        // Handle `notEqual` with `NULL` values
        Predicate rootCauseSubCategoryPredicate = cb.or(
                cb.notEqual(rootCauseSubCategoryJoin.get("name"), "ALTERATION WORK"),
                rootCauseSubCategoryJoin.get("name").isNull()
        );

        Predicate possibleRootCausePredicate = cb.or(
                cb.notEqual(possibleRootCauseJoin.get("name"), "Alterations"),
                possibleRootCauseJoin.get("name").isNull()
        );

        // Add predicates to the list
//        predicates.add(rootCauseSubCategoryPredicate);
//        predicates.add(possibleRootCausePredicate);

        if (divisionId != null && divisionId != 0) {
            predicates.add(cb.equal(divisionJoin.get("id"), divisionId));
        }
        if (zoneId != null && zoneId != 0) {
            predicates.add(cb.equal(zoneJoin.get("id"), zoneId));
        }
        // Firm filter
        if (firmId != null && firmId != 0) {
            Predicate predicate1 = cb.equal(root.get("loco").get("firm").get("id"), firmId);
            Predicate predicate2 = cb.equal(root.get("faultyStation").get("firm").get("id"), firmId);
            predicates.add(cb.or(predicate1, predicate2));
        }
        if (fromDate != null && toDate != null) {
            predicates.add(cb.between(root.get("tripDate"), fromDate, toDate));
        }

        // Add predicates to the query
        query.where(cb.and(predicates.toArray(new Predicate[0])));

        // Group by station code
        query.groupBy(divisionJoin.get("code"));

        // Order by total count descending
        query.orderBy(cb.desc(cb.count(root)));

        // Create and apply pagination
        TypedQuery<Tuple> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        // Fetch results and map to StringAndCount
        List<Tuple> tuples = typedQuery.getResultList();
//        log.info("Tuples : {}", tuples);
        List<StringAndCount> results = new ArrayList<>();
        for (Tuple tuple : tuples) {
            String name = tuple.get("name", String.class);
            Long count = tuple.get("count", Long.class);

//            log.info("Name {}", name);
//            log.info("Count {}", count);
            // Logic to determine colorCode (this is an example)
            String colorCode = HelpingHand.getRandomColor(); // Red if count > 100, else Green

            // Create and add StringAndCount instance
            results.add(new StringAndCount(name, count.intValue(), colorCode));

        }

        return results;
    }

    public LastTripDateNoDTO getLastTripDateAndTripNoByZoneAndDivision(Long zoneId, Long divisionId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<TcasBreakingInspection> root = query.from(TcasBreakingInspection.class);

//        log.info("Division {}", divisionId);

        // Build predicates for zoneId and divisionId
        List<Predicate> predicates = new ArrayList<>();
        if (zoneId != null && zoneId != 0) {
            predicates.add(cb.equal(root.get("division").get("zone").get("id"), zoneId));
        }
        if (divisionId != null && divisionId != 0) {
            predicates.add(cb.equal(root.get("division").get("id"), divisionId));
        }

        // Select tripDate and tripNo, sorted by tripDate DESC and tripNo DESC
        query.multiselect(root.get("tripDate"), root.get("tripNo"))
                .where(predicates.toArray(new Predicate[0]))
                .orderBy(cb.desc(root.get("tripDate")), cb.desc(root.get("tripNo")));

        // Execute query with LIMIT 1
        try {
            Tuple result = entityManager.createQuery(query).setMaxResults(1).getSingleResult();

            LastTripDateNoDTO dto = new LastTripDateNoDTO();
            dto.setLastTripDate(result.get(0, LocalDate.class)); // tripDate
            dto.setTripNo(result.get(1, Integer.class));         // tripNo

            return dto;
        } catch (NoResultException e) {
            return null; // Return null or handle as needed
        }
    }

    public List<OpenTicketWithOEMDTO> getOpenTicketsWithOEMDetails(Long zoneId, Long divisionId, List<Firm> firms, Boolean ticketStatus) {
        // Create CriteriaBuilder
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        // List to store results
        List<OpenTicketWithOEMDTO> resultList = new ArrayList<>();

        // Loop through each firm and build query
        for (Firm firm : firms) {
            // Create CriteriaQuery<Object>
            CriteriaQuery<Object> criteriaQuery = criteriaBuilder.createQuery(Object.class);

            // Define Root for TcasBreakingInspection
            Root<TcasBreakingInspection> inspectionRoot = criteriaQuery.from(TcasBreakingInspection.class);

            // Join Division and Zone
            Join<TcasBreakingInspection, Division> divisionJoin = inspectionRoot.join("division");
            Join<TcasBreakingInspection, IncidentTicket> ticketJoin = inspectionRoot.join("incidentTicket");
            Join<TcasBreakingInspection, IssueCategory> issueCategoryJoin = inspectionRoot.join("issueCategory");
            Join<Division, Zone> zoneJoin = divisionJoin.join("zone");
            Join<IncidentTicket, Firm> firmJoin = ticketJoin.join("assignedFirms");

            // Select distinct ticket numbers
            criteriaQuery.select(criteriaBuilder.construct(String.class, ticketJoin.get("ticketNo"))).distinct(true);

            // Build WHERE clause
            List<Predicate> predicates = new ArrayList<>();

            // Common predicates
            if (divisionId != null && divisionId != 0) {
                predicates.add(criteriaBuilder.equal(divisionJoin.get("id"), divisionId));
            }
            if (zoneId != null && zoneId != 0) {
                predicates.add(criteriaBuilder.equal(zoneJoin.get("id"), zoneId));
            }
            if (ticketStatus != null) {
                predicates.add(criteriaBuilder.equal(ticketJoin.get("status"), ticketStatus));
            }
            // Filter by firmId if provided
            if (firm.getId() != null && firm.getId() != 0) {
                predicates.add(criteriaBuilder.equal(firmJoin.get("id"), firm.getId()));
            }
            // Exclude issueCategory.name == "No Issue"
            predicates.add(criteriaBuilder.notEqual(issueCategoryJoin.get("name"), "No Issue"));

            // Apply predicates
            criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

            // Execute query
            List<Object> results = entityManager.createQuery(criteriaQuery).getResultList();

            // Build DTO for the current firm
            OpenTicketWithOEMDTO dto = new OpenTicketWithOEMDTO();
            dto.setFirm(firm.getName()); // Set firm name
            dto.setOpenTickets((long) results.size()); // Set count of tickets
            dto.setTicketNoList(results.stream().map(result -> (String) result).collect(Collectors.toList())); // Map ticket numbers

            // Add DTO to the result list
            resultList.add(dto);
        }

        //For Each Div
        List<Division> divisionList = new ArrayList<>();
        if (divisionId == null || divisionId < 1) {
            divisionList = divisionService.getAllDivision();
        } else {
            divisionList.add(divisionService.getDivisionById(Math.toIntExact(divisionId)));
        }

        for (Division division : divisionList) {
            // Create CriteriaQuery<Object>
            CriteriaQuery<Object> criteriaQuery = criteriaBuilder.createQuery(Object.class);

            // Define Root for TcasBreakingInspection
            Root<TcasBreakingInspection> inspectionRoot = criteriaQuery.from(TcasBreakingInspection.class);

            // Join Division and Zone
            Join<TcasBreakingInspection, Division> divisionJoin = inspectionRoot.join("division");
            Join<TcasBreakingInspection, IncidentTicket> ticketJoin = inspectionRoot.join("incidentTicket");
            Join<TcasBreakingInspection, IssueCategory> issueCategoryJoin = inspectionRoot.join("issueCategory");
            Join<Division, Zone> zoneJoin = divisionJoin.join("zone");

            // Select distinct ticket numbers
            criteriaQuery.select(criteriaBuilder.construct(String.class, ticketJoin.get("ticketNo"))).distinct(true);

            // Build WHERE clause
            List<Predicate> predicates = new ArrayList<>();

            // Common predicates
            if (division != null && division.getId() != 0) {
                predicates.add(criteriaBuilder.equal(divisionJoin.get("id"), division.getId()));
            }
            if (zoneId != null && zoneId != 0) {
                predicates.add(criteriaBuilder.equal(zoneJoin.get("id"), zoneId));
            }
            if (ticketStatus != null) {
                predicates.add(criteriaBuilder.equal(ticketJoin.get("status"), ticketStatus));
            }
            // Exclude issueCategory.name == "No Issue"
            predicates.add(criteriaBuilder.notEqual(issueCategoryJoin.get("name"), "No Issue"));

            // Add condition for IncidentTicket.assignTo == "DIV"
            predicates.add(criteriaBuilder.equal(ticketJoin.get("assignTo"), "DIV"));

            // Apply predicates
            criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

            // Execute query
            List<Object> results = entityManager.createQuery(criteriaQuery).getResultList();

            // Build DTO for the current firm
            OpenTicketWithOEMDTO dto = new OpenTicketWithOEMDTO();
            dto.setFirm((division != null && division.getCode() != null) ? division.getCode() + " DIV" : "DIV");
            dto.setOpenTickets((long) results.size()); // Set count of tickets
            dto.setTicketNoList(results.stream().map(result -> (String) result).collect(Collectors.toList())); // Map ticket numbers

            // Add DTO to the result list
            resultList.add(dto);

        }
        {
            // Create CriteriaQuery<Object>
            CriteriaQuery<Object> criteriaQuery = criteriaBuilder.createQuery(Object.class);

            // Define Root for TcasBreakingInspection
            Root<TcasBreakingInspection> inspectionRoot = criteriaQuery.from(TcasBreakingInspection.class);

            // Join Division and Zone
            Join<TcasBreakingInspection, Division> divisionJoin = inspectionRoot.join("division");
            Join<TcasBreakingInspection, IncidentTicket> ticketJoin = inspectionRoot.join("incidentTicket");
            Join<TcasBreakingInspection, IssueCategory> issueCategoryJoin = inspectionRoot.join("issueCategory");
            Join<Division, Zone> zoneJoin = divisionJoin.join("zone");

            // Perform a left join for assignedFirms to check if it's empty
            Join<IncidentTicket, Firm> firmJoin = ticketJoin.join("assignedFirms", JoinType.LEFT);

            // Select distinct ticket numbers
            criteriaQuery.select(criteriaBuilder.construct(String.class, ticketJoin.get("ticketNo"))).distinct(true);

            // Build WHERE clause
            List<Predicate> predicates = new ArrayList<>();

            // Common predicates
            if (divisionId != null && divisionId != 0) {
                predicates.add(criteriaBuilder.equal(divisionJoin.get("id"), divisionId));
            }
            if (zoneId != null && zoneId != 0) {
                predicates.add(criteriaBuilder.equal(zoneJoin.get("id"), zoneId));
            }
            if (ticketStatus != null) {
                predicates.add(criteriaBuilder.equal(ticketJoin.get("status"), ticketStatus));
            }
            // Exclude issueCategory.name == "No Issue"
            predicates.add(criteriaBuilder.notEqual(issueCategoryJoin.get("name"), "No Issue"));

            // Check if assignTo is null or empty
            Predicate assignToNullOrEmpty = criteriaBuilder.or(
                    criteriaBuilder.isNull(ticketJoin.get("assignTo")),
                    criteriaBuilder.equal(ticketJoin.get("assignTo"), "")
            );
            predicates.add(assignToNullOrEmpty);

            // Check if assignedFirms is empty
            Predicate assignedFirmsEmpty = criteriaBuilder.isNull(firmJoin.get("id"));
            predicates.add(assignedFirmsEmpty);

            // Apply predicates
            criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

            // Execute query
            List<Object> results = entityManager.createQuery(criteriaQuery).getResultList();

            // Build DTO for the current firm
            OpenTicketWithOEMDTO dto = new OpenTicketWithOEMDTO();
            dto.setFirm("Unassigned"); // Set firm name
            dto.setOpenTickets((long) results.size()); // Set count of tickets
            dto.setTicketNoList(results.stream().map(result -> (String) result).collect(Collectors.toList())); // Map ticket numbers

            // Add DTO to the result list
            resultList.add(dto);
        }

        return resultList;
    }

    public Map<String, Object> getAvgClosureTime(LocalDate startDate, LocalDate endDate) {
        String sql = """
        SELECT 
            AVG(ABS(TIMESTAMPDIFF(SECOND, 
                CAST(tripDate AS DATETIME),
                closureDateTime
            ))) AS avg_seconds
        FROM 
            incidentticket
        WHERE 
            status = 0
            AND tripDate BETWEEN :startDate AND :endDate
            AND closureDateTime IS NOT NULL
            AND tripDate IS NOT NULL
        """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("startDate", java.sql.Date.valueOf(startDate));
        query.setParameter("endDate", java.sql.Date.valueOf(endDate));

        Object result = query.getSingleResult();
        Map<String, Object> resultMap = new HashMap<>();

        if (result != null) {
            Number avgSeconds = (Number) result;
            if (avgSeconds != null) {
                long totalSeconds = avgSeconds.longValue();

                long days = totalSeconds / 86400;
                long hours = (totalSeconds % 86400) / 3600;
                long minutes = (totalSeconds % 3600) / 60;

                String formatted = days + " days " + hours + " hours " + minutes + " minutes";

                BigDecimal avgDaysDecimal = BigDecimal
                        .valueOf((double) totalSeconds / 86400)
                        .setScale(2, RoundingMode.HALF_UP);

                resultMap.put("formatted", formatted);
                resultMap.put("avgDays", avgDaysDecimal.doubleValue());
            }
        }

        return resultMap;
    }


}
