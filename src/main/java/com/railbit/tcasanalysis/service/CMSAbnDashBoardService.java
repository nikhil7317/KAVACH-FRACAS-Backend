package com.railbit.tcasanalysis.service;

import com.railbit.tcasanalysis.entity.*;
import com.railbit.tcasanalysis.entity.analysis.StringAndCount;
import com.railbit.tcasanalysis.entity.cmsabn.CMSAbn;
import com.railbit.tcasanalysis.entity.loco.Loco;
import com.railbit.tcasanalysis.util.HelpingHand;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class CMSAbnDashBoardService {

    @Autowired
    EntityManager entityManager;

    //Get Total incident count filter wise
    public long getCountByDivisionAndZone(Long zoneId, Long divisionId, LocalDate fromDate, LocalDate toDate) {
        // Create CriteriaBuilder
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        // Create CriteriaQuery
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        // Define Root for TcasBreakingInspection
        Root<CMSAbn> inspectionRoot = criteriaQuery.from(CMSAbn.class);

        // Join Division and Zone
        Join<CMSAbn, Division> divisionJoin = inspectionRoot.join("division");
//        Join<CMSAbn, IssueCategory> issueCategoryJoin = inspectionRoot.join("issueCategory");
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
            predicates.add(criteriaBuilder.between(inspectionRoot.get("abnDateTime"), fromDate, toDate));
        }

        // Combine all predicates with AND
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        // Execute query
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    //Get Total incident count filter wise
    public long getCountByDivisionAndZoneWithoutTicket(Long zoneId, Long divisionId, LocalDate fromDate, LocalDate toDate) {
        // Create CriteriaBuilder
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        // Create CriteriaQuery
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        // Define Root for TcasBreakingInspection
        Root<CMSAbn> inspectionRoot = criteriaQuery.from(CMSAbn.class);

        // Join Division and Zone
        Join<CMSAbn, Division> divisionJoin = inspectionRoot.join("division");
//        Join<CMSAbn, IssueCategory> issueCategoryJoin = inspectionRoot.join("issueCategory");
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone");

        // Select COUNT(t.id)
        criteriaQuery.select(criteriaBuilder.count(inspectionRoot.get("id")));

        // Build WHERE clause
        List<Predicate> predicates = new ArrayList<>();

        // Add predicates conditionally
        predicates.add(criteriaBuilder.equal(inspectionRoot.get("ticketNo"), ""));

        if (divisionId != null && divisionId != 0) {
            predicates.add(criteriaBuilder.equal(divisionJoin.get("id"), divisionId));
        }
        if (zoneId != null && zoneId != 0) {
            predicates.add(criteriaBuilder.equal(zoneJoin.get("id"), zoneId));
        }
        if (fromDate != null && toDate != null) {
            predicates.add(criteriaBuilder.between(inspectionRoot.get("abnDateTime"), fromDate, toDate));
        }

        // Combine all predicates with AND
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        // Execute query
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    //Get Total incident count filter wise
    public long getCountByDivisionAndZoneWithTicket(Long zoneId, Long divisionId, LocalDate fromDate, LocalDate toDate) {
        // Create CriteriaBuilder
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        // Create CriteriaQuery
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        // Define Root for TcasBreakingInspection
        Root<CMSAbn> inspectionRoot = criteriaQuery.from(CMSAbn.class);

        // Join Division and Zone
        Join<CMSAbn, Division> divisionJoin = inspectionRoot.join("division");
//        Join<CMSAbn, IssueCategory> issueCategoryJoin = inspectionRoot.join("issueCategory");
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone");

        // Select COUNT(t.id)
        criteriaQuery.select(criteriaBuilder.count(inspectionRoot.get("id")));

        // Build WHERE clause
        List<Predicate> predicates = new ArrayList<>();

        // Add predicates conditionally
        predicates.add(criteriaBuilder.notEqual(inspectionRoot.get("ticketNo"), ""));

        if (divisionId != null && divisionId != 0) {
            predicates.add(criteriaBuilder.equal(divisionJoin.get("id"), divisionId));
        }
        if (zoneId != null && zoneId != 0) {
            predicates.add(criteriaBuilder.equal(zoneJoin.get("id"), zoneId));
        }
        if (fromDate != null && toDate != null) {
            predicates.add(criteriaBuilder.between(inspectionRoot.get("abnDateTime"), fromDate, toDate));
        }

        // Combine all predicates with AND
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        // Execute query
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    //Get Total incident count filter wise
    public long getCountByDivisionAndZoneWithTicketAndStatus(Long zoneId, Long divisionId, LocalDate fromDate, LocalDate toDate, String status) {
        // Create CriteriaBuilder
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        // Create CriteriaQuery
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        // Define Root for TcasBreakingInspection
        Root<CMSAbn> inspectionRoot = criteriaQuery.from(CMSAbn.class);

        // Join Division and Zone
        Join<CMSAbn, Division> divisionJoin = inspectionRoot.join("division");
//        Join<CMSAbn, IssueCategory> issueCategoryJoin = inspectionRoot.join("issueCategory");
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone");

        // Select COUNT(t.id)
        criteriaQuery.select(criteriaBuilder.count(inspectionRoot.get("id")));

        // Build WHERE clause
        List<Predicate> predicates = new ArrayList<>();

        // Add predicates conditionally
        predicates.add(criteriaBuilder.notEqual(inspectionRoot.get("ticketNo"), ""));

        predicates.add(criteriaBuilder.equal(
                criteriaBuilder.lower(inspectionRoot.get("status")),
                status.toLowerCase()
        ));

        if (divisionId != null && divisionId != 0) {
            predicates.add(criteriaBuilder.equal(divisionJoin.get("id"), divisionId));
        }
        if (zoneId != null && zoneId != 0) {
            predicates.add(criteriaBuilder.equal(zoneJoin.get("id"), zoneId));
        }
        if (fromDate != null && toDate != null) {
            predicates.add(criteriaBuilder.between(inspectionRoot.get("abnDateTime"), fromDate, toDate));
        }

        // Combine all predicates with AND
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        // Execute query
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    //Get Total incident count filter wise
    public long getTicketCountByDivisionAndZone(Long zoneId, Long divisionId, LocalDate fromDate, LocalDate toDate) {
        // Create CriteriaBuilder
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        // Create CriteriaQuery
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        // Define Root for TcasBreakingInspection
        Root<CMSAbn> inspectionRoot = criteriaQuery.from(CMSAbn.class);

        // Join Division and Zone
        Join<CMSAbn, Division> divisionJoin = inspectionRoot.join("division");
//        Join<CMSAbn, IssueCategory> issueCategoryJoin = inspectionRoot.join("issueCategory");
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone");

        // Select COUNT(t.id)
        criteriaQuery.select(criteriaBuilder.countDistinct(inspectionRoot.get("ticketNo")));

        // Build WHERE clause
        List<Predicate> predicates = new ArrayList<>();

        // Add predicates conditionally
        predicates.add(criteriaBuilder.notEqual(inspectionRoot.get("ticketNo"), ""));

        if (divisionId != null && divisionId != 0) {
            predicates.add(criteriaBuilder.equal(divisionJoin.get("id"), divisionId));
        }
        if (zoneId != null && zoneId != 0) {
            predicates.add(criteriaBuilder.equal(zoneJoin.get("id"), zoneId));
        }
        if (fromDate != null && toDate != null) {
            predicates.add(criteriaBuilder.between(inspectionRoot.get("abnDateTime"), fromDate, toDate));
        }

        // Combine all predicates with AND
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        // Execute query
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    //Get Total incident count filter wise
    public long getTicketCountByDivisionAndZoneAndStatus(Long zoneId, Long divisionId, LocalDate fromDate, LocalDate toDate, String status) {
        // Create CriteriaBuilder
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        // Create CriteriaQuery
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        // Define Root for TcasBreakingInspection
        Root<CMSAbn> inspectionRoot = criteriaQuery.from(CMSAbn.class);

        // Join Division and Zone
        Join<CMSAbn, Division> divisionJoin = inspectionRoot.join("division");
//        Join<CMSAbn, IssueCategory> issueCategoryJoin = inspectionRoot.join("issueCategory");
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone");

        // Select COUNT(t.id)
        criteriaQuery.select(criteriaBuilder.countDistinct(inspectionRoot.get("ticketNo")));

        // Build WHERE clause
        List<Predicate> predicates = new ArrayList<>();

        // Add predicates conditionally
        predicates.add(criteriaBuilder.notEqual(inspectionRoot.get("ticketNo"), ""));

        predicates.add(criteriaBuilder.equal(
                criteriaBuilder.lower(inspectionRoot.get("status")),
                status.toLowerCase()
        ));

        if (divisionId != null && divisionId != 0) {
            predicates.add(criteriaBuilder.equal(divisionJoin.get("id"), divisionId));
        }
        if (zoneId != null && zoneId != 0) {
            predicates.add(criteriaBuilder.equal(zoneJoin.get("id"), zoneId));
        }
        if (fromDate != null && toDate != null) {
            predicates.add(criteriaBuilder.between(inspectionRoot.get("abnDateTime"), fromDate, toDate));
        }

        // Combine all predicates with AND
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        // Execute query
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    public List<StringAndCount> getDashboardCounts(Long zoneId, Long divisionId, LocalDate fromDate, LocalDate toDate ) {

        List<StringAndCount> dashboardCounts = new ArrayList<>();

        {
            StringAndCount stringAndCount = new StringAndCount();
            stringAndCount.setName("Total ABNs");
            stringAndCount.setCount(Math.toIntExact(getCountByDivisionAndZone(zoneId,divisionId,fromDate,toDate)));
            dashboardCounts.add(stringAndCount);
        }
        {

            StringAndCount stringAndCount = new StringAndCount();
            stringAndCount.setName("ABNs Without Attached Ticket");
            stringAndCount.setCount(Math.toIntExact(getCountByDivisionAndZoneWithoutTicket(zoneId,divisionId,fromDate,toDate)));
            dashboardCounts.add(stringAndCount);
        }
        {

            StringAndCount stringAndCount = new StringAndCount();
            stringAndCount.setName("ABNs With Attached Ticket");
            stringAndCount.setCount(Math.toIntExact(getCountByDivisionAndZoneWithTicket(zoneId,divisionId,fromDate,toDate)));
            dashboardCounts.add(stringAndCount);
        }
//        {
//
//            StringAndCount stringAndCount = new StringAndCount();
//            stringAndCount.setName("With Open Tickets");
//            stringAndCount.setCount(Math.toIntExact(getCountByDivisionAndZoneWithTicketAndStatus(zoneId,divisionId,fromDate,toDate,"open")));
//            dashboardCounts.add(stringAndCount);
//        }
//        {
//
//            StringAndCount stringAndCount = new StringAndCount();
//            stringAndCount.setName("With Closed Tickets");
//            stringAndCount.setCount(Math.toIntExact(getCountByDivisionAndZoneWithTicketAndStatus(zoneId,divisionId,fromDate,toDate,"close")));
//            dashboardCounts.add(stringAndCount);
//        }
        {
            //Total Ticket Counts
            StringAndCount stringAndCount = new StringAndCount();
            stringAndCount.setName("Total Unique Tickets Generated");
            stringAndCount.setCount(Math.toIntExact(getTicketCountByDivisionAndZone(zoneId, divisionId, fromDate, toDate)));
            dashboardCounts.add(stringAndCount);
        }
        {
            //Total Open Incident Counts
            StringAndCount stringAndCount = new StringAndCount();
            stringAndCount.setName("Open Unique Tickets");
            stringAndCount.setCount(Math.toIntExact(getTicketCountByDivisionAndZoneAndStatus(zoneId, divisionId, fromDate, toDate, "open")));
            dashboardCounts.add(stringAndCount);
        }
        {
            //Total Closed Incident Counts
            StringAndCount stringAndCount = new StringAndCount();
            stringAndCount.setName("Closed Unique Tickets");
            stringAndCount.setCount(Math.toIntExact(getTicketCountByDivisionAndZoneAndStatus(zoneId, divisionId, fromDate, toDate, "close")));
            dashboardCounts.add(stringAndCount);
        }
//        {
//
//            StringAndCount stringAndCount = new StringAndCount();
//            stringAndCount.setName("Trip Without Issues");
//            stringAndCount.setCount(Math.toIntExact(getNoIssueCountByDivisionAndZone(zoneId,divisionId,fromDate,toDate)));
//            dashboardCounts.add(stringAndCount);
//        }

//        {
//
//            StringAndCount stringAndCount = new StringAndCount();
//            stringAndCount.setName("Total Users");
//            stringAndCount.setCount(Math.toIntExact(userRepo.count()));
//            dashboardCounts.add(stringAndCount);
//        }


        return dashboardCounts;
    }

    public List<StringAndCount> findTopStationsWithMostIssuesBetweenDates(Long zoneId, Long divisionId, LocalDate fromDate, LocalDate toDate, Pageable pageable) {

//        log.info("Zone : {}",zoneId);
//        log.info("Division : {}",divisionId);
//        log.info("From Date : {}",fromDate);
//        log.info("To Date : {}",toDate);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<CMSAbn> root = query.from(CMSAbn.class);

        // Join with related entities
        Join<CMSAbn, Station> faultyStationJoin = root.join("faultyStation", JoinType.INNER);

        // Join division from Incident
        Join<CMSAbn, Division> divisionJoin = root.join("division");
        // Join zone from Division
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone");

        // Selection: Select station code and count
        query.multiselect(
                faultyStationJoin.get("code").alias("name"),
                cb.count(root).alias("count")
        );

        // Predicates: Filters for issueCategory name and date range
        List<Predicate> predicates = new ArrayList<>();

        if (divisionId != null && divisionId != 0) {
            predicates.add(cb.equal(divisionJoin.get("id"), divisionId));
        }
        if (zoneId != null && zoneId != 0) {
            predicates.add(cb.equal(zoneJoin.get("id"), zoneId));
        }

        if (fromDate != null && toDate != null) {
            predicates.add(cb.between(root.get("abnDateTime"), fromDate, toDate));
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
    public List<StringAndCount> findTopLocosWithMostIssuesBetweenDates(Long zoneId, Long divisionId, LocalDate fromDate, LocalDate toDate, Pageable pageable) {

//        log.info("Zone : {}",zoneId);
//        log.info("Division : {}",divisionId);
//        log.info("From Date : {}",fromDate);
//        log.info("To Date : {}",toDate);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<CMSAbn> root = query.from(CMSAbn.class);

        // Join with related entities
        Join<CMSAbn, Loco> locoJoin = root.join("loco", JoinType.INNER);

        // Join division from Incident
        Join<CMSAbn, Division> divisionJoin = root.join("division");
        // Join zone from Division
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone");

        // Selection: Select station code and count
        query.multiselect(
                locoJoin.get("locoNo").alias("name"),
                cb.count(root).alias("count")
        );

        // Predicates: Filters for issueCategory name and date range
        List<Predicate> predicates = new ArrayList<>();

        if (divisionId != null && divisionId != 0) {
            predicates.add(cb.equal(divisionJoin.get("id"), divisionId));
        }
        if (zoneId != null && zoneId != 0) {
            predicates.add(cb.equal(zoneJoin.get("id"), zoneId));
        }

        if (fromDate != null && toDate != null) {
            predicates.add(cb.between(root.get("abnDateTime"), fromDate, toDate));
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
    public List<StringAndCount> findTopCausesWithMostIssuesBetweenDates(Long zoneId, Long divisionId, LocalDate fromDate, LocalDate toDate, Pageable pageable) {

//        log.info("Zone : {}",zoneId);
//        log.info("Division : {}",divisionId);
//        log.info("From Date : {}",fromDate);
//        log.info("To Date : {}",toDate);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<CMSAbn> root = query.from(CMSAbn.class);

        // Join with related entities
        Join<CMSAbn, PossibleRootCause> rootCauseJoin = root.join("possibleRootCause", JoinType.INNER);

        // Join division from Incident
        Join<CMSAbn, Division> divisionJoin = root.join("division");
        // Join zone from Division
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone");

        // Selection: Select station code and count
        query.multiselect(
                rootCauseJoin.get("name").alias("name"),
                cb.count(root).alias("count")
        );

        // Predicates: Filters for issueCategory name and date range
        List<Predicate> predicates = new ArrayList<>();

        if (divisionId != null && divisionId != 0) {
            predicates.add(cb.equal(divisionJoin.get("id"), divisionId));
        }
        if (zoneId != null && zoneId != 0) {
            predicates.add(cb.equal(zoneJoin.get("id"), zoneId));
        }

        if (fromDate != null && toDate != null) {
            predicates.add(cb.between(root.get("abnDateTime"), fromDate, toDate));
        }

        // Add predicates to the query
        query.where(cb.and(predicates.toArray(new Predicate[0])));

        // Group by station code
        query.groupBy(rootCauseJoin.get("name"));

        // Order by total count descending
        query.orderBy(cb.desc(cb.count(root)));

        // Create and apply pagination
        TypedQuery<Tuple> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        // Fetch results and map to StringAndCount
        List<Tuple> tuples = typedQuery.getResultList();
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
    public List<StringAndCount> findTopDivisionsWithMostIssuesBetweenDates(Long zoneId, Long divisionId, LocalDate fromDate, LocalDate toDate, Pageable pageable) {

//        log.info("Zone : {}",zoneId);
//        log.info("Division : {}",divisionId);
//        log.info("From Date : {}",fromDate);
//        log.info("To Date : {}",toDate);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<CMSAbn> root = query.from(CMSAbn.class);

        // Join division from Incident
        Join<CMSAbn, Division> divisionJoin = root.join("division");
        // Join zone from Division
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone");

        // Selection: Select station code and count
        query.multiselect(
                divisionJoin.get("code").alias("name"),
                cb.count(root).alias("count")
        );

        // Predicates: Filters for issueCategory name and date range
        List<Predicate> predicates = new ArrayList<>();

        if (divisionId != null && divisionId != 0) {
            predicates.add(cb.equal(divisionJoin.get("id"), divisionId));
        }
        if (zoneId != null && zoneId != 0) {
            predicates.add(cb.equal(zoneJoin.get("id"), zoneId));
        }

        if (fromDate != null && toDate != null) {
            predicates.add(cb.between(root.get("abnDateTime"), fromDate, toDate));
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

}
