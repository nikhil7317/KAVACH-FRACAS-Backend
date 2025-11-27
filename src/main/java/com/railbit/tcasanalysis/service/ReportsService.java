package com.railbit.tcasanalysis.service;

import com.railbit.tcasanalysis.DTO.*;
import com.railbit.tcasanalysis.DTO.incident.NMSIncidentDTO;
import com.railbit.tcasanalysis.DTO.reports.*;
import com.railbit.tcasanalysis.entity.*;
import com.railbit.tcasanalysis.entity.analysis.BarGraphDataSet;
import com.railbit.tcasanalysis.entity.analysis.PieChartData;
import com.railbit.tcasanalysis.entity.analysis.StringAndCount;
import com.railbit.tcasanalysis.entity.loco.Loco;
import com.railbit.tcasanalysis.repository.IncidentReportsRepo;
import com.railbit.tcasanalysis.repository.IncidentTicketRepo;
import com.railbit.tcasanalysis.repository.TcasBreakingInspectionRepo;
import com.railbit.tcasanalysis.service.serviceImpl.EmailServiceImpl;
import com.railbit.tcasanalysis.service.serviceImpl.LocoMovementAnalytics;
import com.railbit.tcasanalysis.util.HelpingHand;
import com.railbit.tcasanalysis.util.excel.ExcelUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static com.railbit.tcasanalysis.util.HelpingHand.generateColorCode;
import static com.railbit.tcasanalysis.util.HelpingHand.generateRandomColor;

@Service
@AllArgsConstructor
public class ReportsService {

    private static final Logger log = LoggerFactory.getLogger(ReportsService.class);

    @Autowired
    IncidentTicketRepo incidentTicketRepo;

    @Autowired
    IncidentReportsRepo incidentReportsRepo;

    private final IncidentTicketTrackService incidentTicketTrackService;

    @Autowired
    TcasBreakingInspectionRepo tcasBreakingInspectionRepo;

    @Autowired
    EntityManager entityManager;

    private final UserService userService;
    private final DivisionService divisionService;
    @Autowired
    private EmailServiceImpl emailServiceImpl;
    @Autowired
    private LocoMovementAnalytics locoMovementAnalytics;

    // Repeated Incidents Report

    public Page<TcasBreakingInspection> getClosureIncidentsReport(Long userId, Integer zoneId, Integer divisionId, Integer faultyStationId, LocalDate fromDate, LocalDate toDate,String searchQuery, Pageable pageable) {

        Integer filterZoneId = zoneId;
        Integer filterDivisionId = divisionId;
        // User role filter
        if (userId != null && userId > 0) {
            User user = userService.getUserByUserId(userId);
            String role = user.getRole().getName();

            if (role.equalsIgnoreCase("ROLE_DIVISION") || role.equalsIgnoreCase("ROLE_OEM")) {
                filterDivisionId = user.getDivision().getId();
                filterZoneId = user.getDivision().getZone().getId();
            } else if (role.equalsIgnoreCase("ROLE_ZONE")){
                filterZoneId = user.getZone().getId();
            }
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<TcasBreakingInspection> query = cb.createQuery(TcasBreakingInspection.class);
        Root<TcasBreakingInspection> root = query.from(TcasBreakingInspection.class);

        Join<TcasBreakingInspection, IssueCategory> issueCategory               = root.join("issueCategory", JoinType.LEFT);
        Join<TcasBreakingInspection, PossibleIssue> possibleIssue               = root.join("possibleIssue", JoinType.LEFT);
        Join<TcasBreakingInspection, PossibleRootCause> possibleRootCause       = root.join("possibleRootCause", JoinType.LEFT);
        Join<TcasBreakingInspection, RootCauseSubCategory> rootCauseSubCategory = root.join("rootCauseSubCategory", JoinType.LEFT);
        Join<TcasBreakingInspection, IncidentTicket> incidentTicket             = root.join("incidentTicket", JoinType.LEFT);
        Join<TcasBreakingInspection, Station> faultyStation                     = root.join("faultyStation", JoinType.LEFT);
        Join<TcasBreakingInspection, Division> division                         = root.join("division", JoinType.LEFT);
        Join<Division, Zone> zone                                               = division.join("zone", JoinType.LEFT);

        // Where clause
        List<Predicate> predicates = new ArrayList<>();
        if (faultyStationId != null && faultyStationId != 0) {
            predicates.add(cb.equal(faultyStation.get("id"), faultyStationId));
        }
        if (filterDivisionId != null && filterDivisionId != 0) {
            predicates.add(cb.equal(division.get("id"), filterDivisionId));
        }
        if (filterZoneId != null && filterZoneId != 0) {
            predicates.add(cb.equal(zone.get("id"), filterZoneId));
        }
        if (fromDate != null && toDate != null) {
            predicates.add(cb.between(root.get("tripDate"), fromDate, toDate));
        }

        //for closed incidents
        predicates.add(cb.equal(incidentTicket.get("status"),false));

        //For Search Item
        if (searchQuery != null && !searchQuery.isEmpty()) {
            Predicate searchPredicate = cb.or(
                    cb.like(cb.lower(root.get("rootCauseDescription")), "%" + searchQuery.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("briefDescription")), "%" + searchQuery.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("remark")), "%" + searchQuery + "%"),
                    cb.like(cb.lower(issueCategory.get("name")), "%" + searchQuery.toLowerCase() + "%"),
                    cb.like(cb.lower(possibleIssue.get("name")), "%" + searchQuery.toLowerCase() + "%"),
                    cb.like(cb.lower(possibleRootCause.get("name")), "%" + searchQuery.toLowerCase() + "%"),
                    cb.like(cb.lower(rootCauseSubCategory.get("name")), "%" + searchQuery.toLowerCase() + "%")
            );
            predicates.add(searchPredicate);
        }

        query.where(predicates.toArray(new Predicate[0]));

        // Execute the query
        TypedQuery<TcasBreakingInspection> typedQuery = entityManager.createQuery(query);

        // Apply pagination
        int totalRows = typedQuery.getResultList().size();
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<TcasBreakingInspection> results = typedQuery.getResultList();

        // Return as Page
        return new PageImpl<>(results, pageable, totalRows);
    }

    public Page<CausesWiseRepeatedIncidentsReportDTO> getRepeatedIncidentsReport(
            Long userId,Integer faultyStationId, Integer divisionId, Integer zoneId,LocalDate fromDate, LocalDate toDate,String searchQuery, Pageable pageable) {

        Integer filterZoneId = zoneId;
        Integer filterDivisionId = divisionId;
        // User role filter
        if (userId != null && userId > 0) {
            User user = userService.getUserByUserId(userId);
            String role = user.getRole().getName();

            if (role.equalsIgnoreCase("ROLE_DIVISION") || role.equalsIgnoreCase("ROLE_OEM")) {
                filterDivisionId = user.getDivision().getId();
                filterZoneId = user.getDivision().getZone().getId();
            } else if (role.equalsIgnoreCase("ROLE_ZONE")){
                filterZoneId = user.getZone().getId();
            }
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<CausesWiseRepeatedIncidentsReportDTO> query = cb.createQuery(CausesWiseRepeatedIncidentsReportDTO.class);
        Root<TcasBreakingInspection> root = query.from(TcasBreakingInspection.class);

        // Join tables
        Join<TcasBreakingInspection, IssueCategory> issueCategory               = root.join("issueCategory");
//        Join<TcasBreakingInspection, PossibleIssue> possibleIssue               = root.join("possibleIssue");
        Join<TcasBreakingInspection, PossibleRootCause> possibleRootCause       = root.join("possibleRootCause",JoinType.LEFT);
        Join<TcasBreakingInspection, RootCauseSubCategory> rootCauseSubCategory = root.join("rootCauseSubCategory",JoinType.LEFT);
        Join<TcasBreakingInspection, Station> faultyStation                     = root.join("faultyStation", JoinType.LEFT);
        Join<TcasBreakingInspection, Division> division                         = root.join("division", JoinType.LEFT);
        Join<Division, Zone> zone                                               = division.join("zone", JoinType.LEFT);
        Join<TcasBreakingInspection, Loco> loco = root.join("loco", JoinType.LEFT);

        // Select clause
        query.select(cb.construct(
                CausesWiseRepeatedIncidentsReportDTO.class,
                issueCategory.get("name"),
                possibleRootCause.get("name"),
                rootCauseSubCategory.get("name"),
                cb.count(root),
                faultyStation.get("code"),
                loco.get("locoNo")

        ));

        // Where clause
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.notEqual(rootCauseSubCategory.get("name"), "ALTERATION WORK"));
        predicates.add(cb.notEqual(possibleRootCause.get("name"), "Alterations"));
        predicates.add(cb.notEqual(issueCategory.get("name"), "No Issue"));
        predicates.add(cb.notEqual(issueCategory.get("name"), "Trial"));
        predicates.add(cb.notEqual(issueCategory.get("name"), "Desirable Braking"));

        if (faultyStationId != null && faultyStationId != 0) {
            predicates.add(cb.equal(faultyStation.get("id"), faultyStationId));
        }
        if (filterDivisionId != null && filterDivisionId != 0) {
            predicates.add(cb.equal(division.get("id"), filterDivisionId));
        }
        if (filterZoneId != null && filterZoneId != 0) {
            predicates.add(cb.equal(zone.get("id"), filterZoneId));
        }
        if (fromDate != null && toDate != null) {
            predicates.add(cb.between(root.get("tripDate"), fromDate, toDate));
        }

        //For Search Item
        if (searchQuery != null && !searchQuery.isEmpty()) {
            Predicate searchPredicate = cb.or(
                    cb.like(cb.lower(issueCategory.get("name")), "%" + searchQuery.toLowerCase() + "%"),
                    cb.like(cb.lower(possibleRootCause.get("name")), "%" + searchQuery.toLowerCase() + "%"),
                    cb.like(cb.lower(rootCauseSubCategory.get("name")), "%" + searchQuery.toLowerCase() + "%"),
                    cb.like(cb.lower(faultyStation.get("code")), "%" + searchQuery.toLowerCase() + "%"),
                    cb.like(cb.lower(loco.get("locoNo")), "%" + searchQuery.toLowerCase() + "%")
            );
            predicates.add(searchPredicate);
        }


        query.where(predicates.toArray(new Predicate[0]));

        // Group by and Order by
        query.groupBy(
                issueCategory.get("id"),
                possibleRootCause.get("id"),
                rootCauseSubCategory.get("id"),
                faultyStation.get("id"),
                loco.get("id")
        );
        query.orderBy(cb.desc(cb.count(root)));

        // Execute the query
        TypedQuery<CausesWiseRepeatedIncidentsReportDTO> typedQuery = entityManager.createQuery(query);

        // Apply pagination
        int totalRows = typedQuery.getResultList().size();
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<CausesWiseRepeatedIncidentsReportDTO> results = typedQuery.getResultList();

        // Return as Page
        return new PageImpl<>(results, pageable, totalRows);
    }



    public RepeatedIncidentAnalysisDTO getRepeatedIncidentAnalysis(
            Long userId, Integer stationId, Integer divisionId, Integer zoneId,
            LocalDate fromDate, LocalDate toDate,
            String issueCategory, String possibleRootCause, String rootCauseSubCategory) {

        /* role filter */
        Integer filterZoneId = zoneId;
        Integer filterDivisionId = divisionId;
        if (userId != null && userId > 0) {
            User user = userService.getUserByUserId(userId);
            String role = user.getRole().getName();
            if (role.equalsIgnoreCase("ROLE_DIVISION") || role.equalsIgnoreCase("ROLE_OEM")) {
                filterDivisionId = user.getDivision().getId();
                filterZoneId = user.getDivision().getZone().getId();
            } else if (role.equalsIgnoreCase("ROLE_ZONE")) {
                filterZoneId = user.getZone().getId();
            }
        }
        /* 2. COMMON PREDICATE BUILDER – re-use everywhere */
        BiFunction<CriteriaBuilder, Root<TcasBreakingInspection>, Predicate[]> buildPredicates =
                (cb, root) -> {
                    List<Predicate> p = new ArrayList<>();
                    Join<TcasBreakingInspection, IssueCategory> ic = root.join("issueCategory");
                    Join<TcasBreakingInspection, PossibleRootCause> prc = root.join("possibleRootCause", JoinType.LEFT);
                    Join<TcasBreakingInspection, RootCauseSubCategory> rsc = root.join("rootCauseSubCategory", JoinType.LEFT);
                    Join<TcasBreakingInspection, Division> d = root.join("division", JoinType.LEFT);
                    Join<Division, Zone> z = d.join("zone", JoinType.LEFT);
                    Join<TcasBreakingInspection, Station> s = root.join("faultyStation", JoinType.LEFT);

                    p.add(cb.equal(ic.get("name"), issueCategory));
                    p.add(cb.equal(prc.get("name"), possibleRootCause));
                    p.add(cb.equal(rsc.get("name"), rootCauseSubCategory));
                    p.add(cb.notEqual(rsc.get("name"), "ALTERATION WORK"));
                    p.add(cb.notEqual(prc.get("name"), "Alterations"));
                    p.add(cb.not(cb.lower(ic.get("name")).in("no issue", "trial", "desirable braking")));

                    if (stationId != null && stationId != 0) p.add(cb.equal(s.get("id"), stationId));
                    if (divisionId != null && divisionId != 0) p.add(cb.equal(d.get("id"), divisionId));
                    if (zoneId != null && zoneId != 0) p.add(cb.equal(z.get("id"), zoneId));
                    if (fromDate != null && toDate != null) p.add(cb.between(root.get("tripDate"), fromDate, toDate));

                    return p.toArray(new Predicate[0]);
                };

        /* 3. ORIGINAL TOTALS QUERY (unchanged) */
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> q = cb.createTupleQuery();
        Root<TcasBreakingInspection> root = q.from(TcasBreakingInspection.class);
        Join<TcasBreakingInspection, IncidentTicket> it = root.join("incidentTicket", JoinType.LEFT);

        q.multiselect(
                cb.count(root).alias("totalTickets"),
                cb.countDistinct(cb.selectCase()
                        .when(cb.isTrue(it.get("status")), it.get("id"))
                        .otherwise(cb.nullLiteral(Long.class))).alias("uniqueOpenTickets"),
                cb.countDistinct(cb.selectCase()
                        .when(cb.isFalse(it.get("status")), it.get("id"))
                        .otherwise(cb.nullLiteral(Long.class))).alias("uniqueClosedTickets"),
                cb.countDistinct(it.get("id")).alias("totalUniqueTickets")
        );
        q.where(buildPredicates.apply(cb, root));
        Tuple totals = entityManager.createQuery(q).getSingleResult();


        /* 6. BUILD & RETURN */
        return RepeatedIncidentAnalysisDTO.builder()
                .totalTickets(       ((Number) totals.get("totalTickets")).longValue())
                .uniqueOpenTickets(  ((Number) totals.get("uniqueOpenTickets")).longValue())
                .uniqueClosedTickets(((Number) totals.get("uniqueClosedTickets")).longValue())
                .totalUniqueTickets( ((Number) totals.get("totalUniqueTickets")).longValue())
                .build();
    }


    public Page<LocoRepeatedIncidentReportDTO> getLocoBasedRepeatedIncidentsReport(
            Long userId,Integer faultyStationId, Integer divisionId, Integer zoneId,Integer locoId, LocalDate fromDate, LocalDate toDate,String searchQuery, Pageable pageable) {

        Integer filterZoneId = zoneId;
        Integer filterDivisionId = divisionId;
        // User role filter
        if (userId != null && userId > 0) {
            User user = userService.getUserByUserId(userId);
            String role = user.getRole().getName();

            if (role.equalsIgnoreCase("ROLE_DIVISION") || role.equalsIgnoreCase("ROLE_OEM")) {
                filterDivisionId = user.getDivision().getId();
                filterZoneId = user.getDivision().getZone().getId();
            } else if (role.equalsIgnoreCase("ROLE_ZONE")){
                filterZoneId = user.getZone().getId();
            }
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<LocoRepeatedIncidentReportDTO> query = cb.createQuery(LocoRepeatedIncidentReportDTO.class);
        Root<TcasBreakingInspection> root = query.from(TcasBreakingInspection.class);

        // Join tables
        Join<TcasBreakingInspection, IssueCategory> issueCategory               = root.join("issueCategory");
//        Join<TcasBreakingInspection, PossibleIssue> possibleIssue               = root.join("possibleIssue");
        Join<TcasBreakingInspection, PossibleRootCause> possibleRootCause       = root.join("possibleRootCause",JoinType.LEFT);
        Join<TcasBreakingInspection, RootCauseSubCategory> rootCauseSubCategory = root.join("rootCauseSubCategory",JoinType.LEFT);
        Join<TcasBreakingInspection, Station> faultyStation                     = root.join("faultyStation", JoinType.LEFT);
        Join<TcasBreakingInspection, Loco> loco                                 = root.join("loco");
        Join<Loco, Firm> ltcasOem                                               = loco.join("firm");
        Join<TcasBreakingInspection, Division> division                         = root.join("division", JoinType.LEFT);
        Join<Division, Zone> zone                                               = division.join("zone", JoinType.LEFT);

        // Select clause
        query.select(cb.construct(
                LocoRepeatedIncidentReportDTO.class,
                zone.get("code"),
                division.get("code"),
                loco.get("locoNo"),
                loco.get("id"),
                ltcasOem.get("name"),
                issueCategory.get("name"),
                possibleRootCause.get("name"),
                rootCauseSubCategory.get("name"),
                cb.count(root)
        ));

        // Where clause
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(possibleRootCause.get("name"), "Onboard Kavach"));

        if (faultyStationId != null && faultyStationId != 0) {
            predicates.add(cb.equal(faultyStation.get("id"), faultyStationId));
        }
        if (filterDivisionId != null && filterDivisionId != 0) {
            predicates.add(cb.equal(division.get("id"), filterDivisionId));
        }
        if (filterZoneId != null && filterZoneId != 0) {
            predicates.add(cb.equal(zone.get("id"), filterZoneId));
        }
        if (locoId != null && locoId != 0) {
            predicates.add(cb.equal(loco.get("id"), locoId));
        }
        if (fromDate != null && toDate != null) {
            predicates.add(cb.between(root.get("tripDate"), fromDate, toDate));
        }

        //For Search Item
        if (searchQuery != null && !searchQuery.isEmpty()) {
            Predicate searchPredicate = cb.or(
                    cb.like(cb.lower(ltcasOem.get("name")), "%" + searchQuery.toLowerCase() + "%"),
                    cb.like(cb.lower(loco.get("locoNo")), "%" + searchQuery.toLowerCase() + "%"),
                    cb.like(cb.lower(issueCategory.get("name")), "%" + searchQuery.toLowerCase() + "%"),
                    cb.like(cb.lower(possibleRootCause.get("name")), "%" + searchQuery.toLowerCase() + "%")
            );
            predicates.add(searchPredicate);
        }


        query.where(predicates.toArray(new Predicate[0]));

        // Group by and Order by
        query.groupBy(
                loco.get("id"),
                issueCategory.get("id"),
                division.get("id"),
                possibleRootCause.get("id"),
                rootCauseSubCategory.get("id")
        );

        query.orderBy(cb.desc(cb.count(root)));

        // Execute the query
        TypedQuery<LocoRepeatedIncidentReportDTO> typedQuery = entityManager.createQuery(query);

        // Apply pagination
        int totalRows = typedQuery.getResultList().size();
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<LocoRepeatedIncidentReportDTO> results = typedQuery.getResultList();

        // Return as Page
        return new PageImpl<>(results, pageable, totalRows);
    }

    public RepeatedIncidentAnalysisDTO getLocoRepeatedIncidentAnalysis(
            Long userId, Integer stationId, Integer divisionId, Integer zoneId,
            LocalDate fromDate, LocalDate toDate,
            String issueCategory, String possibleRootCause, String rootCauseSubCategory,
            Integer locoId) {

        // Step 1: Role-based filtering
        Integer filterZoneId = zoneId;
        Integer filterDivisionId = divisionId;
        if (userId != null && userId > 0) {
            User user = userService.getUserByUserId(userId);
            String role = user.getRole().getName();
            if (role.equalsIgnoreCase("ROLE_DIVISION") || role.equalsIgnoreCase("ROLE_OEM")) {
                filterDivisionId = user.getDivision().getId();
                filterZoneId = user.getDivision().getZone().getId();
            } else if (role.equalsIgnoreCase("ROLE_ZONE")) {
                filterZoneId = user.getZone().getId();
            }
        }

        // Step 2: Predicate Builder
        BiFunction<CriteriaBuilder, Root<TcasBreakingInspection>, Predicate[]> buildPredicates =
                (cb, root) -> {
                    List<Predicate> p = new ArrayList<>();
                    Join<TcasBreakingInspection, IssueCategory> ic = root.join("issueCategory");
                    Join<TcasBreakingInspection, PossibleRootCause> prc = root.join("possibleRootCause", JoinType.LEFT);
                    Join<TcasBreakingInspection, RootCauseSubCategory> rsc = root.join("rootCauseSubCategory", JoinType.LEFT);
                    Join<TcasBreakingInspection, Division> d = root.join("division", JoinType.LEFT);
                    Join<Division, Zone> z = d.join("zone", JoinType.LEFT);
                    Join<TcasBreakingInspection, Station> s = root.join("faultyStation", JoinType.LEFT);

                    p.add(cb.equal(ic.get("name"), issueCategory));
                    p.add(cb.equal(prc.get("name"), possibleRootCause));
                    p.add(cb.equal(rsc.get("name"), rootCauseSubCategory));
                    p.add(cb.notEqual(rsc.get("name"), "ALTERATION WORK"));
                    p.add(cb.notEqual(prc.get("name"), "Alterations"));
                    p.add(cb.not(cb.lower(ic.get("name")).in("no issue", "trial", "desirable braking")));

                    if (stationId != null && stationId != 0) p.add(cb.equal(s.get("id"), stationId));
                    if (divisionId != null && divisionId != 0) p.add(cb.equal(d.get("id"), divisionId));
                    if (zoneId != null && zoneId != 0) p.add(cb.equal(z.get("id"), zoneId));
                    if (fromDate != null && toDate != null) p.add(cb.between(root.get("tripDate"), fromDate, toDate));
                    if (locoId != null && locoId > 0) p.add(cb.equal(root.get("loco").get("id"), locoId));

                    return p.toArray(new Predicate[0]);
                };

        // Step 3: Main Query
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> q = cb.createTupleQuery();
        Root<TcasBreakingInspection> root = q.from(TcasBreakingInspection.class);
        Join<TcasBreakingInspection, IncidentTicket> it = root.join("incidentTicket", JoinType.LEFT);

        q.multiselect(
                cb.count(root).alias("totalTickets"),
                cb.countDistinct(cb.selectCase()
                        .when(cb.isTrue(it.get("status")), it.get("id"))
                        .otherwise(cb.nullLiteral(Long.class))).alias("uniqueOpenTickets"),
                cb.countDistinct(cb.selectCase()
                        .when(cb.isFalse(it.get("status")), it.get("id"))
                        .otherwise(cb.nullLiteral(Long.class))).alias("uniqueClosedTickets"),
                cb.countDistinct(it.get("id")).alias("totalUniqueTickets")
        );

        q.where(buildPredicates.apply(cb, root));
        Tuple totals = entityManager.createQuery(q).getSingleResult();

        // Step 4: Build and return response
        return RepeatedIncidentAnalysisDTO.builder()
                .totalTickets(((Number) totals.get("totalTickets")).longValue())
                .uniqueOpenTickets(((Number) totals.get("uniqueOpenTickets")).longValue())
                .uniqueClosedTickets(((Number) totals.get("uniqueClosedTickets")).longValue())
                .totalUniqueTickets(((Number) totals.get("totalUniqueTickets")).longValue())
                .build();
    }

    public RepeatedIncidentAnalysisDTO getStationRepeatedIncidentAnalysis(
            Long userId,
            Integer stationId,
            Integer divisionId,
            LocalDate fromDate,
            LocalDate toDate,
            String issueCategory,
            String possibleRootCause,
            String rootCauseSubCategory
    ) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<TcasBreakingInspection> root = query.from(TcasBreakingInspection.class);

        Join<TcasBreakingInspection, IssueCategory> ic = root.join("issueCategory");
        Join<TcasBreakingInspection, PossibleRootCause> prc = root.join("possibleRootCause", JoinType.LEFT);
        Join<TcasBreakingInspection, RootCauseSubCategory> rsc = root.join("rootCauseSubCategory", JoinType.LEFT);
        Join<TcasBreakingInspection, Station> stationJoin = root.join("faultyStation", JoinType.LEFT);
        Join<TcasBreakingInspection, Division> divisionJoin = root.join("division", JoinType.LEFT);
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone", JoinType.LEFT);
        Join<TcasBreakingInspection, IncidentTicket> ticketJoin = root.join("incidentTicket", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();

        // 🔐 Role-based filtering
        if (userId != null && userId > 0) {
            User user = userService.getUserByUserId(userId);
            String role = user.getRole().getName();
            if ("ROLE_DIVISION".equalsIgnoreCase(role) || "ROLE_OEM".equalsIgnoreCase(role)) {
                predicates.add(cb.equal(divisionJoin.get("id"), user.getDivision().getId()));
            } else if ("ROLE_ZONE".equalsIgnoreCase(role)) {
                predicates.add(cb.equal(zoneJoin.get("id"), user.getZone().getId()));
            }
        }

        // ✅ Apply dynamic filters from query params
        if (issueCategory != null && !issueCategory.isEmpty()) {
            predicates.add(cb.equal(ic.get("name"), issueCategory));
        }
        if (possibleRootCause != null && !possibleRootCause.isEmpty()) {
            predicates.add(cb.equal(prc.get("name"), possibleRootCause));
        }
        if (rootCauseSubCategory != null && !rootCauseSubCategory.isEmpty()) {
            predicates.add(cb.equal(rsc.get("name"), rootCauseSubCategory));
        }
        if (stationId != null && stationId != 0) {
            predicates.add(cb.equal(stationJoin.get("id"), stationId));
        }
        if (divisionId != null && divisionId != 0) {
            predicates.add(cb.equal(divisionJoin.get("id"), divisionId));
        }

        if (fromDate != null && toDate != null) {
            predicates.add(cb.between(root.get("tripDate"), fromDate, toDate));
        }

        // 🚫 Clean out invalid root cause types
        predicates.add(cb.notEqual(rsc.get("name"), "ALTERATION WORK"));
        predicates.add(cb.notEqual(prc.get("name"), "Alterations"));
        predicates.add(cb.not(cb.lower(ic.get("name")).in("no issue", "trial", "desirable braking")));

        query.multiselect(
                cb.count(root).alias("totalTickets"),
                cb.countDistinct(cb.selectCase()
                        .when(cb.isTrue(ticketJoin.get("status")), ticketJoin.get("id"))
                        .otherwise(cb.nullLiteral(Long.class))).alias("uniqueOpenTickets"),
                cb.countDistinct(cb.selectCase()
                        .when(cb.isFalse(ticketJoin.get("status")), ticketJoin.get("id"))
                        .otherwise(cb.nullLiteral(Long.class))).alias("uniqueClosedTickets"),
                cb.countDistinct(ticketJoin.get("id")).alias("totalUniqueTickets")
        );


        query.where(cb.and(predicates.toArray(new Predicate[0])));
        Tuple result = entityManager.createQuery(query).getSingleResult();

        return RepeatedIncidentAnalysisDTO.builder()
                .totalTickets(((Number) result.get("totalTickets")).longValue())
                .uniqueOpenTickets(((Number) result.get("uniqueOpenTickets")).longValue())
                .uniqueClosedTickets(((Number) result.get("uniqueClosedTickets")).longValue())
                .totalUniqueTickets(((Number) result.get("totalUniqueTickets")).longValue())
                .build();
    }





    public Page<OEMRepeatedIncidentReportDTO> getOEMBasedRepeatedIncidentsReport(
            Long userId,Integer faultyStationId, Integer divisionId, Integer zoneId,LocalDate fromDate, LocalDate toDate,String searchQuery, Pageable pageable) {

        Integer filterZoneId = zoneId;
        Integer filterDivisionId = divisionId;
        // User role filter
        if (userId != null && userId > 0) {
            User user = userService.getUserByUserId(userId);
            String role = user.getRole().getName();

            if (role.equalsIgnoreCase("ROLE_DIVISION") || role.equalsIgnoreCase("ROLE_OEM")) {
                filterDivisionId = user.getDivision().getId();
                filterZoneId = user.getDivision().getZone().getId();
            } else if (role.equalsIgnoreCase("ROLE_ZONE")){
                filterZoneId = user.getZone().getId();
            }
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<OEMRepeatedIncidentReportDTO> query = cb.createQuery(OEMRepeatedIncidentReportDTO.class);
        Root<TcasBreakingInspection> root = query.from(TcasBreakingInspection.class);

        // Join tables
        Join<TcasBreakingInspection, IssueCategory> issueCategory               = root.join("issueCategory");
//        Join<TcasBreakingInspection, PossibleIssue> possibleIssue               = root.join("possibleIssue");
        Join<TcasBreakingInspection, PossibleRootCause> possibleRootCause       = root.join("possibleRootCause");
//        Join<TcasBreakingInspection, RootCauseSubCategory> rootCauseSubCategory = root.join("rootCauseSubCategory");
        Join<TcasBreakingInspection, Station> faultyStation                     = root.join("faultyStation");
        Join<Station, Firm> stcasOEM                                            = faultyStation.join("firm");
        Join<TcasBreakingInspection, Loco> loco                                 = root.join("loco");
        Join<Loco, Firm> ltcasOEM                                               = loco.join("firm");
        Join<TcasBreakingInspection, Division> division                         = root.join("division");
        Join<Division, Zone> zone                                               = division.join("zone");

        // Select clause
        query.select(cb.construct(
                OEMRepeatedIncidentReportDTO.class,
                zone.get("code"),
                division.get("code"),
                ltcasOEM.get("name"),
                stcasOEM.get("name"),
                issueCategory.get("name"),
                possibleRootCause.get("name"),
                cb.count(root)
        ));

        // Where clause
        List<Predicate> predicates = new ArrayList<>();
        if (faultyStationId != null && faultyStationId != 0) {
            predicates.add(cb.equal(faultyStation.get("id"), faultyStationId));
        }
        if (filterDivisionId != null && filterDivisionId != 0) {
            predicates.add(cb.equal(division.get("id"), filterDivisionId));
        }
        if (filterZoneId != null && filterZoneId != 0) {
            predicates.add(cb.equal(zone.get("id"), filterZoneId));
        }
        if (fromDate != null && toDate != null) {
            predicates.add(cb.between(root.get("tripDate"), fromDate, toDate));
        }

        //For Search Item
        if (searchQuery != null && !searchQuery.isEmpty()) {
            Predicate searchPredicate = cb.or(
                    cb.like(cb.lower(ltcasOEM.get("name")), "%" + searchQuery.toLowerCase() + "%"),
                    cb.like(cb.lower(stcasOEM.get("name")), "%" + searchQuery.toLowerCase() + "%"),
                    cb.like(cb.lower(issueCategory.get("name")), "%" + searchQuery.toLowerCase() + "%"),
//                    cb.like(cb.lower(possibleIssue.get("name")), "%" + searchQuery.toLowerCase() + "%"),
                    cb.like(cb.lower(possibleRootCause.get("name")), "%" + searchQuery.toLowerCase() + "%")
//                    cb.like(cb.lower(rootCauseSubCategory.get("name")), "%" + searchQuery.toLowerCase() + "%")
            );
            predicates.add(searchPredicate);
        }


        query.where(predicates.toArray(new Predicate[0]));

        // Group by and Order by
        query.groupBy(
                ltcasOEM.get("id"),
                stcasOEM.get("id"),
                issueCategory.get("id"),
                division.get("id"),
                possibleRootCause.get("id")
        );

        query.orderBy(cb.desc(cb.count(root)));

        // Execute the query
        TypedQuery<OEMRepeatedIncidentReportDTO> typedQuery = entityManager.createQuery(query);

        // Apply pagination
        int totalRows = typedQuery.getResultList().size();
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<OEMRepeatedIncidentReportDTO> results = typedQuery.getResultList();

        // Return as Page
        return new PageImpl<>(results, pageable, totalRows);
    }



    public List<TicketDetailsDTO> getIncidentTicketDetails(
            Long userId,
            String issueCategory, String possibleRootCause,
            String rootCauseSubCategory, LocalDate fromDate, LocalDate toDate,
            Integer zoneId, Integer divisionId, Integer stationId) {

        Integer filterZoneId = zoneId;
        Integer filterDivisionId = divisionId;
        Integer filterStationId = stationId;

        /* ---------- role based filtering ---------- */
        if (userId != null && userId > 0) {
            User user = userService.getUserByUserId(userId);
            String role = user.getRole().getName();
            if (role.equalsIgnoreCase("ROLE_DIVISION") || role.equalsIgnoreCase("ROLE_OEM")) {
                filterDivisionId = user.getDivision().getId();
                filterZoneId = user.getDivision().getZone().getId();
            } else if (role.equalsIgnoreCase("ROLE_ZONE")) {
                filterZoneId = user.getZone().getId();
            }
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createTupleQuery();

        Root<IncidentTicket> ticketRoot = cq.from(IncidentTicket.class);

        /* --- joins --- */
        Join<IncidentTicket, Division> divisionJoin = ticketRoot.join("division", JoinType.LEFT);
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone", JoinType.LEFT);

        /* TcasBreakingInspection is the link to loco/firm/station/date */
        Join<IncidentTicket, TcasBreakingInspection> tbiJoin = ticketRoot.join("inspections", JoinType.INNER);

        Join<TcasBreakingInspection, IssueCategory> icJoin = tbiJoin.join("issueCategory", JoinType.LEFT);
        Join<TcasBreakingInspection, PossibleRootCause> prcJoin = tbiJoin.join("possibleRootCause", JoinType.LEFT);
        Join<TcasBreakingInspection, RootCauseSubCategory> rscJoin = tbiJoin.join("rootCauseSubCategory", JoinType.LEFT);
        Join<TcasBreakingInspection, Loco> locoJoin = tbiJoin.join("loco", JoinType.LEFT);
        Join<TcasBreakingInspection, Station> stationJoin = tbiJoin.join("faultyStation", JoinType.LEFT);

        /* IncidentTicketFirm -> Firm */
        Join<IncidentTicket, IncidentTicketFirm> itfJoin = ticketRoot.join("incidentTicketFirms", JoinType.LEFT);
        Join<IncidentTicketFirm, Firm> firmJoin = itfJoin.join("firm", JoinType.LEFT);

        /* --- select --- */
        cq.multiselect(
                ticketRoot.get("id").alias("ticketId"),
                ticketRoot.get("ticketNo").alias("ticketNo"),
                ticketRoot.get("status").alias("status"),
                ticketRoot.get("assignTo").alias("assignTo"),
                ticketRoot.get("description").alias("description"),
                divisionJoin.get("code").alias("divisionCode"),
                divisionJoin.get("name").alias("divisionName"),
                zoneJoin.get("code").alias("zoneCode"),
                zoneJoin.get("name").alias("zoneName"),
                ticketRoot.get("closureDateTime").alias("closureDateTime"),
                tbiJoin.get("tripDate").alias("tripDate"),
                locoJoin.get("locoNo").alias("locoNo"),
                firmJoin.get("name").alias("firmName")
        );

        /* --- predicates --- */
        List<Predicate> preds = new ArrayList<>();

        if (issueCategory != null && !issueCategory.isEmpty())
            preds.add(cb.equal(icJoin.get("name"), issueCategory));
        if (possibleRootCause != null && !possibleRootCause.isEmpty())
            preds.add(cb.equal(prcJoin.get("name"), possibleRootCause));
        if (rootCauseSubCategory != null && !rootCauseSubCategory.isEmpty())
            preds.add(cb.equal(rscJoin.get("name"), rootCauseSubCategory));

        if (fromDate != null && toDate != null)
            preds.add(cb.between(tbiJoin.get("tripDate"), fromDate, toDate));

        if (filterStationId != null && filterStationId != 0)
            preds.add(cb.equal(stationJoin.get("id"), filterStationId));
        if (filterDivisionId != null && filterDivisionId != 0)
            preds.add(cb.equal(divisionJoin.get("id"), filterDivisionId));
        if (filterZoneId != null && filterZoneId != 0)
            preds.add(cb.equal(zoneJoin.get("id"), filterZoneId));

        cq.where(preds.toArray(new Predicate[0]));

        List<Tuple> rows = entityManager.createQuery(cq).getResultList();

        /* --- group by IncidentTicket.id --- */
        Map<Long, TicketDetailsDTO> map = new LinkedHashMap<>();

        for (Tuple r : rows) {
            Long ticketId = r.get("ticketId", Long.class);
            TicketDetailsDTO dto = map.computeIfAbsent(ticketId, id -> {
                TicketDetailsDTO d = new TicketDetailsDTO();
                d.setTicketNo(r.get("ticketNo", String.class));
                d.setStatus(r.get("status", Boolean.class));
                d.setAssignTo(r.get("assignTo", String.class));
                d.setDescription(r.get("description", String.class));
                d.setDivision(new TicketDetailsDTO.DivisionDTO(
                        r.get("divisionCode", String.class),
                        r.get("divisionName", String.class)));
                d.setZone(new TicketDetailsDTO.ZoneDTO(
                        r.get("zoneCode", String.class),
                        r.get("zoneName", String.class)));
                d.setCloseDateTime(r.get("closureDateTime", LocalDateTime.class));
                d.setTripDate(r.get("tripDate", LocalDate.class));
                d.setLocoNo(new ArrayList<>());
                d.setName("");
                return d;
            });

            /* collect locos */
            String loco = r.get("locoNo", String.class);
            if (loco != null && !dto.getLocoNo().contains(loco))
                dto.getLocoNo().add(loco);

            /* collect firms */
            String firm = r.get("firmName", String.class);
            if (firm != null && !dto.getName().contains(firm)) {
                dto.setName(dto.getName().isEmpty() ? firm : dto.getName() + ", " + firm);
            }
        }

        return new ArrayList<>(map.values());
    }
    public List<TicketDetailsDTO> getLocoIncidentTicketDetails(
            Long userId,
            String issueCategory, String possibleRootCause,
            String rootCauseSubCategory, LocalDate fromDate, LocalDate toDate,
            Integer zoneId, Integer divisionId, Integer stationId,
            Integer locoId // ✅ New Param
    ) {
        Integer filterZoneId = zoneId;
        Integer filterDivisionId = divisionId;
        Integer filterStationId = stationId;

        if (userId != null && userId > 0) {
            User user = userService.getUserByUserId(userId);
            String role = user.getRole().getName();

            if (role.equalsIgnoreCase("ROLE_DIVISION") || role.equalsIgnoreCase("ROLE_OEM")) {
                filterDivisionId = user.getDivision().getId();
                filterZoneId = user.getDivision().getZone().getId();
            } else if (role.equalsIgnoreCase("ROLE_ZONE")) {
                filterZoneId = user.getZone().getId();
            }
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<TcasBreakingInspection> root = query.from(TcasBreakingInspection.class);

        Join<TcasBreakingInspection, IssueCategory> issueCategoryJoin = root.join("issueCategory", JoinType.LEFT);
        Join<TcasBreakingInspection, PossibleRootCause> possibleRootCauseJoin = root.join("possibleRootCause", JoinType.LEFT);
        Join<TcasBreakingInspection, RootCauseSubCategory> rootCauseSubCategoryJoin = root.join("rootCauseSubCategory", JoinType.LEFT);
        Join<TcasBreakingInspection, Loco> locoNoJoin = root.join("loco", JoinType.LEFT);
        Join<TcasBreakingInspection, IncidentTicket> incidentTicketJoin = root.join("incidentTicket", JoinType.INNER);

        Join<IncidentTicket, Division> divisionJoin = incidentTicketJoin.join("division", JoinType.LEFT);
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone", JoinType.LEFT);
        Join<TcasBreakingInspection, Station> stationJoin = root.join("faultyStation", JoinType.LEFT);

        Root<IncidentTicketFirm> incidentTicketFirmRoot = query.from(IncidentTicketFirm.class);
        Predicate firmJoinPredicate = cb.equal(incidentTicketJoin.get("id"), incidentTicketFirmRoot.get("incidentTicket").get("id"));
        Join<IncidentTicketFirm, Firm> firmJoin = incidentTicketFirmRoot.join("firm", JoinType.LEFT);

        query.multiselect(
                incidentTicketJoin.get("ticketNo").alias("ticketNo"),
                incidentTicketJoin.get("status").alias("status"),
                incidentTicketJoin.get("assignTo").alias("assignTo"),
                incidentTicketJoin.get("description").alias("description"),
                divisionJoin.get("name").alias("divisionName"),
                divisionJoin.get("code").alias("divisionCode"),
                zoneJoin.get("name").alias("zoneName"),
                zoneJoin.get("code").alias("zoneCode"),
                incidentTicketJoin.get("closureDateTime").alias("closureDateTime"),
                incidentTicketJoin.get("tripDate").alias("tripDate"),
                locoNoJoin.get("locoNo").alias("locoNo"),
                firmJoin.get("name").alias("firmName")
        );

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(firmJoinPredicate);

        if (issueCategory != null && !issueCategory.isEmpty()) {
            predicates.add(cb.equal(issueCategoryJoin.get("name"), issueCategory));
        }
        if (possibleRootCause != null && !possibleRootCause.isEmpty()) {
            predicates.add(cb.equal(possibleRootCauseJoin.get("name"), possibleRootCause));
        }
        if (rootCauseSubCategory != null && !rootCauseSubCategory.isEmpty()) {
            predicates.add(cb.equal(rootCauseSubCategoryJoin.get("name"), rootCauseSubCategory));
        }
        if (fromDate != null && toDate != null) {
            predicates.add(cb.between(root.get("tripDate"), fromDate, toDate));
        }

        // ✅ Loco filter added
        if (locoId != null && locoId > 0) {
            predicates.add(cb.equal(locoNoJoin.get("id"), locoId));
        }

        // Zone, division, station filters
        if (filterStationId != null && filterStationId != 0) {
            predicates.add(cb.equal(stationJoin.get("id"), filterStationId));
        }
        if (filterDivisionId != null && filterDivisionId != 0) {
            predicates.add(cb.equal(divisionJoin.get("id"), filterDivisionId));
        }
        if (filterZoneId != null && filterZoneId != 0) {
            predicates.add(cb.equal(zoneJoin.get("id"), filterZoneId));
        }

        query.where(predicates.toArray(new Predicate[0]));

        List<Tuple> tuples = entityManager.createQuery(query).getResultList();

        Map<String, TicketDetailsDTO> groupedTickets = new LinkedHashMap<>();

        for (Tuple tuple : tuples) {
            String ticketNo = tuple.get("ticketNo", String.class);
            String firmName = tuple.get("firmName", String.class);
            String locoNo = tuple.get("locoNo", String.class);
            if (!groupedTickets.containsKey(ticketNo)) {
                TicketDetailsDTO dto = new TicketDetailsDTO(
                        ticketNo,
                        tuple.get("status", Boolean.class),
                        tuple.get("assignTo", String.class),
                        tuple.get("description", String.class),
                        new TicketDetailsDTO.DivisionDTO(
                                tuple.get("divisionCode", String.class),
                                tuple.get("divisionName", String.class)
                        ),
                        new TicketDetailsDTO.ZoneDTO(
                                tuple.get("zoneCode", String.class),
                                tuple.get("zoneName", String.class)
                        ),
                        tuple.get("closureDateTime", LocalDateTime.class),
                        tuple.get("tripDate", LocalDate.class),
                        new ArrayList<>(locoNo != null ? List.of(locoNo) : List.of()),
                        firmName
                );
                groupedTickets.put(ticketNo, dto);
            } else {
                TicketDetailsDTO dto = groupedTickets.get(ticketNo);
                if (!dto.getName().contains(firmName)) {
                    dto.setName(dto.getName() + ", " + firmName);
                }
            }
        }

        return new ArrayList<>(groupedTickets.values());
    }

    public List<TicketDetailsDTO> getStationIncidentTicketDetails(
            Long userId,
            String issueCategory,
            String possibleRootCause,
            String rootCauseSubCategory,
            Integer divisionId,
            Integer stationId,
            LocalDate fromDate,
            LocalDate toDate
    ) {
        Integer filterDivisionId = divisionId;
        Integer filterStationId = stationId;
        Integer filterZoneId = null;

        if (userId != null && userId > 0) {
            User user = userService.getUserByUserId(userId);
            String role = user.getRole().getName();

            if (role.equalsIgnoreCase("ROLE_DIVISION") || role.equalsIgnoreCase("ROLE_OEM")) {
                filterDivisionId = user.getDivision().getId();
                filterZoneId = user.getDivision().getZone().getId();
            } else if (role.equalsIgnoreCase("ROLE_ZONE")) {
                filterZoneId = user.getZone().getId();
            }
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<TcasBreakingInspection> root = query.from(TcasBreakingInspection.class);

        Join<TcasBreakingInspection, IssueCategory> issueCategoryJoin = root.join("issueCategory", JoinType.LEFT);
        Join<TcasBreakingInspection, PossibleRootCause> possibleRootCauseJoin = root.join("possibleRootCause", JoinType.LEFT);
        Join<TcasBreakingInspection, RootCauseSubCategory> rootCauseSubCategoryJoin = root.join("rootCauseSubCategory", JoinType.LEFT);
        Join<TcasBreakingInspection, Loco> locoNoJoin = root.join("loco", JoinType.LEFT); // Still used to get locoNo
        Join<TcasBreakingInspection, IncidentTicket> incidentTicketJoin = root.join("incidentTicket", JoinType.INNER);
        Join<IncidentTicket, Division> divisionJoin = incidentTicketJoin.join("division", JoinType.LEFT);
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone", JoinType.LEFT);
        Join<TcasBreakingInspection, Station> stationJoin = root.join("faultyStation", JoinType.LEFT);

        Root<IncidentTicketFirm> incidentTicketFirmRoot = query.from(IncidentTicketFirm.class);
        Predicate firmJoinPredicate = cb.equal(incidentTicketJoin.get("id"), incidentTicketFirmRoot.get("incidentTicket").get("id"));
        Join<IncidentTicketFirm, Firm> firmJoin = incidentTicketFirmRoot.join("firm", JoinType.LEFT);

        query.multiselect(
                incidentTicketJoin.get("ticketNo").alias("ticketNo"),
                incidentTicketJoin.get("status").alias("status"),
                incidentTicketJoin.get("assignTo").alias("assignTo"),
                incidentTicketJoin.get("description").alias("description"),
                divisionJoin.get("name").alias("divisionName"),
                divisionJoin.get("code").alias("divisionCode"),
                zoneJoin.get("name").alias("zoneName"),
                zoneJoin.get("code").alias("zoneCode"),
                incidentTicketJoin.get("closureDateTime").alias("closureDateTime"),
                incidentTicketJoin.get("tripDate").alias("tripDate"),
                locoNoJoin.get("locoNo").alias("locoNo"),
                firmJoin.get("name").alias("firmName")
        );

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(firmJoinPredicate);

        if (issueCategory != null && !issueCategory.isEmpty()) {
            predicates.add(cb.equal(issueCategoryJoin.get("name"), issueCategory));
        }
        if (possibleRootCause != null && !possibleRootCause.isEmpty()) {
            predicates.add(cb.equal(possibleRootCauseJoin.get("name"), possibleRootCause));
        }
        if (rootCauseSubCategory != null && !rootCauseSubCategory.isEmpty()) {
            predicates.add(cb.equal(rootCauseSubCategoryJoin.get("name"), rootCauseSubCategory));
        }
        if (fromDate != null && toDate != null) {
            predicates.add(cb.between(root.get("tripDate"), fromDate, toDate));
        }

        if (filterStationId != null && filterStationId != 0) {
            predicates.add(cb.equal(stationJoin.get("id"), filterStationId));
        }
        if (filterDivisionId != null && filterDivisionId != 0) {
            predicates.add(cb.equal(divisionJoin.get("id"), filterDivisionId));
        }
        if (filterZoneId != null && filterZoneId != 0) {
            predicates.add(cb.equal(zoneJoin.get("id"), filterZoneId));
        }

        query.where(predicates.toArray(new Predicate[0]));

        List<Tuple> tuples = entityManager.createQuery(query).getResultList();
        Map<String, TicketDetailsDTO> groupedTickets = new LinkedHashMap<>();

        for (Tuple tuple : tuples) {
            String ticketNo = tuple.get("ticketNo", String.class);
            String firmName = tuple.get("firmName", String.class);
            String locoNo = tuple.get("locoNo", String.class);
            if (!groupedTickets.containsKey(ticketNo)) {
                TicketDetailsDTO dto = new TicketDetailsDTO(
                        ticketNo,
                        tuple.get("status", Boolean.class),
                        tuple.get("assignTo", String.class),
                        tuple.get("description", String.class),
                        new TicketDetailsDTO.DivisionDTO(
                                tuple.get("divisionCode", String.class),
                                tuple.get("divisionName", String.class)
                        ),
                        new TicketDetailsDTO.ZoneDTO(
                                tuple.get("zoneCode", String.class),
                                tuple.get("zoneName", String.class)
                        ),
                        tuple.get("closureDateTime", LocalDateTime.class),
                        tuple.get("tripDate", LocalDate.class),
                        new ArrayList<>(locoNo != null ? List.of(locoNo) : List.of()),
                        firmName
                );
                groupedTickets.put(ticketNo, dto);
            } else {
                TicketDetailsDTO dto = groupedTickets.get(ticketNo);
                if (!dto.getName().contains(firmName)) {
                    dto.setName(dto.getName() + ", " + firmName);
                }
            }
        }

        return new ArrayList<>(groupedTickets.values());
    }


    public Page<StationRepeatedIncidentReportDTO> getStationBasedRepeatedIncidentsReport(
            Long userId,Integer faultyStationId, Integer divisionId, Integer zoneId,LocalDate fromDate, LocalDate toDate,String searchQuery, Pageable pageable) {

        Integer filterZoneId = zoneId;
        Integer filterDivisionId = divisionId;
        // User role filter
        if (userId != null && userId > 0) {
            User user = userService.getUserByUserId(userId);
            String role = user.getRole().getName();

            if (role.equalsIgnoreCase("ROLE_DIVISION") || role.equalsIgnoreCase("ROLE_OEM")) {
                filterDivisionId = user.getDivision().getId();
                filterZoneId = user.getDivision().getZone().getId();
            } else if (role.equalsIgnoreCase("ROLE_ZONE")){
                filterZoneId = user.getZone().getId();
            }
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<StationRepeatedIncidentReportDTO> query = cb.createQuery(StationRepeatedIncidentReportDTO.class);
        Root<TcasBreakingInspection> root = query.from(TcasBreakingInspection.class);

        // Join tables
        Join<TcasBreakingInspection, IssueCategory> issueCategory               = root.join("issueCategory");
//        Join<TcasBreakingInspection, PossibleIssue> possibleIssue               = root.join("possibleIssue");
        Join<TcasBreakingInspection, PossibleRootCause> possibleRootCause       = root.join("possibleRootCause",JoinType.LEFT);
        Join<TcasBreakingInspection, RootCauseSubCategory> rootCauseSubCategory = root.join("rootCauseSubCategory",JoinType.LEFT);
        Join<TcasBreakingInspection, Station> faultyStation                     = root.join("faultyStation");
        Join<Station, Firm> stcasOEM                                            = faultyStation.join("firm");
//        Join<TcasBreakingInspection, Loco> loco                                 = root.join("loco");
//        Join<Loco, Firm> ltcasOEM                                               = loco.join("firm");
        Join<TcasBreakingInspection, Division> division                         = root.join("division");
        Join<Division, Zone> zone                                               = division.join("zone");

        // Select clause
        query.select(cb.construct(
                StationRepeatedIncidentReportDTO.class,
                zone.get("code"),
                division.get("code"),
                division.get("id"),
                faultyStation.get("code"),
                faultyStation.get("id"),
                stcasOEM.get("name"),
                issueCategory.get("name"),
                possibleRootCause.get("name"),
                rootCauseSubCategory.get("name"),
                cb.count(root)
        ));

        // Where clause
        List<Predicate> predicates = new ArrayList<>();
//        predicates.add(cb.notEqual(rootCauseSubCategory.get("name"), "ALTERATION WORK"));
        predicates.add(cb.notEqual(possibleRootCause.get("name"), "Alterations"));
        predicates.add(cb.equal(possibleRootCause.get("name"), "Station Kavach"));
        predicates.add(cb.notEqual(issueCategory.get("name"), "No Issue"));
        predicates.add(cb.notEqual(issueCategory.get("name"), "Trial"));
        predicates.add(cb.notEqual(issueCategory.get("name"), "Desirable Braking"));

        if (faultyStationId != null && faultyStationId != 0) {
            predicates.add(cb.equal(faultyStation.get("id"), faultyStationId));
        }
        if (filterDivisionId != null && filterDivisionId != 0) {
            predicates.add(cb.equal(division.get("id"), filterDivisionId));
        }
        if (filterZoneId != null && filterZoneId != 0) {
            predicates.add(cb.equal(zone.get("id"), filterZoneId));
        }
        if (fromDate != null && toDate != null) {
            predicates.add(cb.between(root.get("tripDate"), fromDate, toDate));
        }

        //For Search Item
        if (searchQuery != null && !searchQuery.isEmpty()) {
            Predicate searchPredicate = cb.or(
//                    cb.like(cb.lower(loco.get("locoNo")), "%" + searchQuery.toLowerCase() + "%"),
                    cb.like(cb.lower(faultyStation.get("code")), "%" + searchQuery.toLowerCase() + "%"),
                    cb.like(cb.lower(issueCategory.get("name")), "%" + searchQuery.toLowerCase() + "%"),
//                    cb.like(cb.lower(possibleIssue.get("name")), "%" + searchQuery.toLowerCase() + "%"),
//                    cb.like(cb.lower(rootCauseSubCategory.get("name")), "%" + searchQuery.toLowerCase() + "%")
                    cb.like(cb.lower(possibleRootCause.get("name")), "%" + searchQuery.toLowerCase() + "%")
            );
            predicates.add(searchPredicate);
        }

        query.where(predicates.toArray(new Predicate[0]));

        // Group by and Order by
        query.groupBy(
                division.get("id"),
                faultyStation.get("id"),
                issueCategory.get("id"),
                possibleRootCause.get("id"),
                rootCauseSubCategory.get("id")
        );

        query.orderBy(cb.desc(cb.count(root)));

        // Execute the query
        TypedQuery<StationRepeatedIncidentReportDTO> typedQuery = entityManager.createQuery(query);

        // Apply pagination
        int totalRows = typedQuery.getResultList().size();
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<StationRepeatedIncidentReportDTO> results = typedQuery.getResultList();

        // Return as Page
        return new PageImpl<>(results, pageable, totalRows);
    }

    public Page<RootCausesWiseIncidentsReportDTO> getRootCauseWiseIncidentsReport(
            Long userId,Integer faultyStationId, Integer divisionId, Integer zoneId,LocalDate fromDate, LocalDate toDate,String searchQuery, Pageable pageable) {

        Integer filterZoneId = zoneId;
        Integer filterDivisionId = divisionId;
        // User role filter
        if (userId != null && userId > 0) {
            User user = userService.getUserByUserId(userId);
            String role = user.getRole().getName();

            if (role.equalsIgnoreCase("ROLE_DIVISION") || role.equalsIgnoreCase("ROLE_OEM")) {
                filterDivisionId = user.getDivision().getId();
                filterZoneId = user.getDivision().getZone().getId();
            } else if (role.equalsIgnoreCase("ROLE_ZONE")){
                filterZoneId = user.getZone().getId();
            }
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<RootCausesWiseIncidentsReportDTO> query = cb.createQuery(RootCausesWiseIncidentsReportDTO.class);
        Root<TcasBreakingInspection> root = query.from(TcasBreakingInspection.class);

        // Join tables
        Join<TcasBreakingInspection, IssueCategory> issueCategory               = root.join("issueCategory");
//        Join<TcasBreakingInspection, PossibleIssue> possibleIssue             = root.join("possibleIssue");
        Join<TcasBreakingInspection, PossibleRootCause> possibleRootCause       = root.join("possibleRootCause");
        Join<TcasBreakingInspection, RootCauseSubCategory> rootCauseSubCategory = root.join("rootCauseSubCategory");
        Join<TcasBreakingInspection, Station> faultyStation                     = root.join("faultyStation");
        Join<Station, Firm> stcasOEM                                            = faultyStation.join("firm");
        Join<TcasBreakingInspection, Loco> loco                                 = root.join("loco");
        Join<Loco, Firm> ltcasOEM                                               = loco.join("firm");
        Join<TcasBreakingInspection, Division> division                         = root.join("division");
        Join<Division, Zone> zone                                               = division.join("zone");

        // Select clause
        query.select(cb.construct(
                RootCausesWiseIncidentsReportDTO.class,
                zone.get("code"),
                division.get("code"),
                possibleRootCause.get("name"),
                issueCategory.get("name"),
                rootCauseSubCategory.get("name"),
                cb.count(root)
        ));

        // Where clause
        List<Predicate> predicates = new ArrayList<>();
        if (faultyStationId != null && faultyStationId != 0) {
            predicates.add(cb.equal(faultyStation.get("id"), faultyStationId));
        }
        if (filterDivisionId != null && filterDivisionId != 0) {
            predicates.add(cb.equal(division.get("id"), filterDivisionId));
        }
        if (filterZoneId != null && filterZoneId != 0) {
            predicates.add(cb.equal(zone.get("id"), filterZoneId));
        }
        if (fromDate != null && toDate != null) {
            predicates.add(cb.between(root.get("tripDate"), fromDate, toDate));
        }

        //For Search Item
        if (searchQuery != null && !searchQuery.isEmpty()) {
            Predicate searchPredicate = cb.or(
                    cb.like(cb.lower(loco.get("locoNo")), "%" + searchQuery.toLowerCase() + "%"),
                    cb.like(cb.lower(faultyStation.get("code")), "%" + searchQuery.toLowerCase() + "%"),
                    cb.like(cb.lower(issueCategory.get("name")), "%" + searchQuery.toLowerCase() + "%"),
//                    cb.like(cb.lower(possibleIssue.get("name")), "%" + searchQuery.toLowerCase() + "%"),
                    cb.like(cb.lower(rootCauseSubCategory.get("name")), "%" + searchQuery.toLowerCase() + "%"),
                    cb.like(cb.lower(possibleRootCause.get("name")), "%" + searchQuery.toLowerCase() + "%")
            );
            predicates.add(searchPredicate);
        }

        query.where(predicates.toArray(new Predicate[0]));

        // Group by and Order by
        query.groupBy(
                division.get("id"),
                possibleRootCause.get("id"),
                issueCategory.get("id"),
                rootCauseSubCategory.get("id")
        );

        query.orderBy(cb.desc(cb.count(root)));

        // Execute the query
        TypedQuery<RootCausesWiseIncidentsReportDTO> typedQuery = entityManager.createQuery(query);

        // Apply pagination
        int totalRows = typedQuery.getResultList().size();
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<RootCausesWiseIncidentsReportDTO> results = typedQuery.getResultList();

        // Return as Page
        return new PageImpl<>(results, pageable, totalRows);
    }

    @Transactional
    public Page<OpenTicketReportDTO> getOpenTicketReport(Pageable pageable, Long zoneId, Long divisionId, Long firmId,Long issueCategoryId,Long rootCauseCategoryId,Long rootCauseSubCategoryId,Boolean ticketStatus, String searchQuery) {
        // Create CriteriaBuilder
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        // Create CriteriaQuery
        CriteriaQuery<OpenTicketReportDTO> criteriaQuery = criteriaBuilder.createQuery(OpenTicketReportDTO.class);

        // Define Root for IncidentTicket
        Root<IncidentTicket> ticketRoot = criteriaQuery.from(IncidentTicket.class);

        // Join Division and Zone
        Join<IncidentTicket, Division> divisionJoin = ticketRoot.join("division");
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone");
        Join<IncidentTicket, TcasBreakingInspection> inspectionJoin = ticketRoot.join("inspections", JoinType.LEFT);
        Join<TcasBreakingInspection, IssueCategory> issueCategoryJoin = inspectionJoin.join("issueCategory", JoinType.LEFT);
        Join<TcasBreakingInspection, PossibleRootCause> rootCauseJoin = inspectionJoin.join("possibleRootCause", JoinType.LEFT);
        Join<TcasBreakingInspection, RootCauseSubCategory> rootCauseSubCategoryJoin = inspectionJoin.join("rootCauseSubCategory", JoinType.LEFT);
        Join<IncidentTicket, Firm> firmJoin = ticketRoot.join("assignedFirms", JoinType.LEFT);

        // Define selections
        criteriaQuery.select(criteriaBuilder.construct(
                OpenTicketReportDTO.class,
                criteriaBuilder.min(inspectionJoin.get("id")).alias("incidentId"),
                zoneJoin.get("code").alias("zoneCode"),
                divisionJoin.get("code").alias("divisionCode"),
                ticketRoot.get("ticketNo"),
                criteriaBuilder.count(inspectionJoin).alias("incidentCount"),
                ticketRoot.get("description"),
                ticketRoot.get("description"),
                criteriaBuilder.min(inspectionJoin.get("tripDate")).alias("firstIncidentDate"), // Calculate the earliest tripDate
                criteriaBuilder.selectCase()
                        .when(criteriaBuilder.equal(ticketRoot.get("assignTo"), "DIV"), ticketRoot.get("assignTo"))
                        .otherwise(criteriaBuilder.function(
                                "GROUP_CONCAT",
                                String.class,
                                criteriaBuilder.function("DISTINCT", String.class, firmJoin.get("name"))
                        )).alias("assignTo"),
                ticketRoot.get("targetDate"),
                criteriaBuilder.function("datediff", Long.class, // Use the min tripDate in the calculation
                        criteriaBuilder.currentDate(),
                        criteriaBuilder.min(inspectionJoin.get("tripDate"))).alias("daysPending")
        ));

        // Build WHERE clause
        List<Predicate> predicates = new ArrayList<>();

        if (zoneId != null && zoneId > 0) {
            predicates.add(criteriaBuilder.equal(zoneJoin.get("id"), zoneId));
        }
        if (divisionId != null && divisionId > 0) {
            predicates.add(criteriaBuilder.equal(divisionJoin.get("id"), divisionId));
        }
        if (firmId != null && firmId > 0) {
            predicates.add(criteriaBuilder.equal(firmJoin.get("id"), firmId));
        }
        if (issueCategoryId != null && issueCategoryId > 0) {
            predicates.add(criteriaBuilder.equal(issueCategoryJoin.get("id"), issueCategoryId));
        }
        if (rootCauseCategoryId != null && rootCauseCategoryId > 0) {
            predicates.add(criteriaBuilder.equal(rootCauseJoin.get("id"), rootCauseCategoryId));
        }
        if (rootCauseSubCategoryId != null && rootCauseSubCategoryId > 0) {
            predicates.add(criteriaBuilder.equal(rootCauseSubCategoryJoin.get("id"), rootCauseSubCategoryId));
        }
        if (ticketStatus != null) {
            predicates.add(criteriaBuilder.equal(ticketRoot.get("status"), ticketStatus));
        }

        // Add searchQuery logic (reuse from your other method)
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            String likeQuery = "%" + searchQuery.toLowerCase() + "%";
            predicates.add(
                    criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(ticketRoot.get("ticketNo")), likeQuery),
//                            criteriaBuilder.like(criteriaBuilder.lower(ticketRoot.get("description")), likeQuery),
                            criteriaBuilder.like(criteriaBuilder.lower(firmJoin.get("name")), likeQuery),
                            criteriaBuilder.like(criteriaBuilder.lower(divisionJoin.get("code")), likeQuery),
                            criteriaBuilder.like(criteriaBuilder.lower(zoneJoin.get("code")), likeQuery)
                    )
            );
        }

        // Exclude tickets with zero inspections
        predicates.add(criteriaBuilder.greaterThan(criteriaBuilder.size(ticketRoot.get("inspections")), 0));

        // Combine predicates
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        // Group by ticketNo
        criteriaQuery.groupBy(ticketRoot.get("ticketNo"));

        // Add pagination
        TypedQuery<OpenTicketReportDTO> query = entityManager.createQuery(criteriaQuery);
        int totalRows = query.getResultList().size();
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        // Fetch result list
        List<OpenTicketReportDTO> results = query.getResultList();

        for (OpenTicketReportDTO dto : results) {
            TcasBreakingInspection incident = tcasBreakingInspectionRepo.findById(dto.getIncidentId()).orElse(null);
//            log.info("Incident {}",incident);
            if (incident != null) {
                // Set issue name or empty string if null
                dto.setIssue(incident.getIssueCategory());

                // Set possible root cause name or empty string if null
                dto.setRootCause(incident.getPossibleRootCause());

                // Set root cause subcategory name or empty string if null
                dto.setRootCauseSubCategory(incident.getRootCauseSubCategory());

                // Set brief description or empty string if null
                dto.setIncidentDescription(incident.getBriefDescription() != null ? incident.getBriefDescription() : "");
            }
        }

        return new PageImpl<>(results, pageable, totalRows);
    }

    public List<MonthWiseData> getAvgClosingTimeDataByMonth(Long zoneId, Long divisionId, Integer year) {
        // Create CriteriaBuilder and initialize query
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> criteriaQuery = criteriaBuilder.createQuery(Object[].class);

        // Define root for TcasBreakingInspection
        Root<TcasBreakingInspection> inspectionRoot = criteriaQuery.from(TcasBreakingInspection.class);

        // Join relationships
        Join<TcasBreakingInspection, IncidentTicket> ticketJoin = inspectionRoot.join("incidentTicket");
        Join<IncidentTicket, Firm> firmJoin = ticketJoin.join("assignedFirms");
        Join<TcasBreakingInspection, Division> divisionJoin = inspectionRoot.join("division");
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone");

        // Subquery to find the minimum id of TcasBreakingInspection for each IncidentTicket
        Subquery<Long> subquery = criteriaQuery.subquery(Long.class);
        Root<TcasBreakingInspection> subqueryRoot = subquery.from(TcasBreakingInspection.class);
        subquery.select(criteriaBuilder.min(subqueryRoot.get("id")));
        subquery.where(criteriaBuilder.equal(subqueryRoot.get("incidentTicket"), ticketJoin));

        // Restrict to only the first TcasBreakingInspection
        criteriaQuery.where(criteriaBuilder.equal(inspectionRoot.get("id"), subquery));

        // Add filtering by zoneId and divisionId
        List<Predicate> predicates = new ArrayList<>();
        if (zoneId != null && zoneId > 0) {
            predicates.add(criteriaBuilder.equal(zoneJoin.get("id"), zoneId));
        }
        if (divisionId != null && divisionId > 0) {
            predicates.add(criteriaBuilder.equal(divisionJoin.get("id"), divisionId));
        }
        // Filter by year
        if (year != null && year > 0) {
            Expression<Integer> yearExpression = criteriaBuilder.function("YEAR", Integer.class, inspectionRoot.get("tripDate"));
            predicates.add(criteriaBuilder.equal(yearExpression, year));
        }
        criteriaQuery.where(predicates.toArray(new Predicate[0]));

        // Extract the month from tripDate
        Expression<Integer> monthExpression = criteriaBuilder.function("MONTH", Integer.class, inspectionRoot.get("tripDate"));

        // Calculate the average closing time (in days)
        Expression<Double> avgClosingTime = criteriaBuilder.avg(
                criteriaBuilder.function(
                        "DATEDIFF", Double.class,
                        ticketJoin.get("closureDateTime"),
                        inspectionRoot.get("tripDate")
                )
        );

        // Select month, firm name, and average closing time
        criteriaQuery.multiselect(
                monthExpression,
                firmJoin.get("name"),
                avgClosingTime
        );

        // Group by month and firm
        criteriaQuery.groupBy(monthExpression, firmJoin.get("name"));

        // Execute query
        List<Object[]> resultList = entityManager.createQuery(criteriaQuery).getResultList();

        // Process results into DTO format
        Map<Integer, MonthWiseData> monthWiseDataMap = new LinkedHashMap<>();
        for (Object[] result : resultList) {
            int month = (Integer) result[0];
            String firmName = (String) result[1];
            double avgTime = result[2] != null ? (Double) result[2] : 0.0;

            // Convert days into Days, Hours, and Minutes
            int days = (int) avgTime;
            double fractionalDays = avgTime - days;
            int hours = (int) (fractionalDays * 24);
            int minutes = (int) ((fractionalDays * 24 - hours) * 60);

            // Format as "X Days Y Hours Z Minutes"
            String avgTimeFormatted = String.format("%d Days %d Hours %d Minutes", days, hours, minutes);

            // Generate a random color
            String randomColor = generateRandomColor();

            // Create or retrieve MonthWiseData for the current month
            MonthWiseData monthWiseData = monthWiseDataMap.computeIfAbsent(month, m -> {
                MonthWiseData newMonthWiseData = new MonthWiseData();
                newMonthWiseData.setMonth(Month.of(m).name()); // Convert month number to name
                newMonthWiseData.setBarGraphDataSetList(new ArrayList<>());
                return newMonthWiseData;
            });

            // Add the firm's data to the month
            monthWiseData.getBarGraphDataSetList().add(new BarGraphDataSet(firmName,avgTime,avgTimeFormatted,randomColor));
        }

        // Return the list of MonthWiseData
        return new ArrayList<>(monthWiseDataMap.values());
    }

    public List<MonthWiseData> getMonthWiseTicketData(Long zoneId, Long divisionId, Integer year) {
        // Define the date range
        LocalDate startDate = (year == null || year == 0)
                ? LocalDate.now().minusMonths(12).withDayOfMonth(1)
                : LocalDate.of(year, 1, 1);
        LocalDate endDate = (year == null || year == 0)
                ? LocalDate.now()
                : LocalDate.of(year, 12, 31);

        // Get total tickets by tripDate
        List<Object[]> totalTicketsResult = getMonthlyCount("tripDate", zoneId, divisionId, startDate, endDate);

        // Get closed tickets by closureDateTime
//        List<Object[]> closedTicketsResult = getMonthlyCount("closureDateTime", zoneId, divisionId, startDate, endDate);
        List<Object[]> closedTicketsResult = new ArrayList<>();

        // Combine results into MonthWiseData
        Map<Integer, MonthWiseData> monthDataMap = new LinkedHashMap<>();

        // Process total tickets
        for (Object[] result : totalTicketsResult) {
            int month = (Integer) result[0];
            Long totalTickets = (Long) result[1];

            log.info("Month : {}",Month.of(month).name());
            log.info("Total : {}", totalTickets);
            // Initialize MonthWiseData if not already present
            MonthWiseData monthWiseData = monthDataMap.computeIfAbsent(month, k -> {
                MonthWiseData data = new MonthWiseData();
                data.setMonth(Month.of(k).name());
                data.setBarGraphDataSetList(new ArrayList<>()); // Initialize the list
                return data;
            });

            // Add total tickets data to the bar graph list
            monthWiseData.getBarGraphDataSetList().add(
                    new BarGraphDataSet("Total Tickets", totalTickets.doubleValue(), totalTickets.toString(), "#ff6384")
            );
        }

// Process closed tickets
        for (Object[] result : closedTicketsResult) {
            int month = (Integer) result[0];
            Long closedTickets = (Long) result[1];

            // Initialize MonthWiseData if not already present
            MonthWiseData monthWiseData = monthDataMap.computeIfAbsent(month, k -> {
                MonthWiseData data = new MonthWiseData();
                data.setMonth(Month.of(k).name());
                data.setBarGraphDataSetList(new ArrayList<>()); // Initialize the list
                return data;
            });

            // Add closed tickets data to the bar graph list
            monthWiseData.getBarGraphDataSetList().add(
                    new BarGraphDataSet("Closed Tickets", closedTickets.doubleValue(), closedTickets.toString(), "#4bc0c0")
            );
        }



        return new ArrayList<>(monthDataMap.values());
    }

    // Helper method for monthly count based on a date column
    private List<Object[]> getMonthlyCount(String dateColumn, Long zoneId, Long divisionId, LocalDate startDate, LocalDate endDate) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);

        Root<IncidentTicket> ticketRoot = cq.from(IncidentTicket.class);

        // Group by the month of the given date column
        Expression<Integer> monthExpression = cb.function("MONTH", Integer.class, ticketRoot.get(dateColumn));
        Expression<Long> countExpression = cb.count(ticketRoot);

        // Add selection: Month and Count
        cq.multiselect(monthExpression, countExpression);

        // Apply filters: zoneId, divisionId, and date range
        List<Predicate> filters = new ArrayList<>();
        if (zoneId != null && zoneId > 0) {
            log.info("zone : {}",zoneId);
            filters.add(cb.equal(ticketRoot.get("division").get("zone").get("id"), zoneId));
        }
        if (divisionId != null && divisionId > 0) {
            filters.add(cb.equal(ticketRoot.get("division").get("id"), divisionId));
        }
        filters.add(cb.between(ticketRoot.get(dateColumn), startDate, endDate));
        cq.where(filters.toArray(new Predicate[0]));

        // Group by and order by month
        cq.groupBy(monthExpression);
//        cq.orderBy(cb.asc(monthExpression));

        return entityManager.createQuery(cq).getResultList();
    }

    public List<PieChartData> getStationRelatedAndLocoRelatedOpenTicket(Long zoneId, Long divisionId, Long firmId, Boolean ticketStatus) {

        List<PieChartData> pieChartDataList = new ArrayList<>();

        // Create CriteriaBuilder
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        // Create CriteriaQuery
        CriteriaQuery<OpenTicketReportDTO> criteriaQuery = criteriaBuilder.createQuery(OpenTicketReportDTO.class);

        // Define Root for IncidentTicket
        Root<IncidentTicket> ticketRoot = criteriaQuery.from(IncidentTicket.class);

        // Join Division and Zone
        Join<IncidentTicket, Division> divisionJoin = ticketRoot.join("division");
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone");
        Join<IncidentTicket, TcasBreakingInspection> inspectionJoin = ticketRoot.join("inspections", JoinType.LEFT);
        Join<IncidentTicket, Firm> firmJoin = ticketRoot.join("assignedFirms", JoinType.LEFT);

        // Define selections
        criteriaQuery.select(criteriaBuilder.construct(
                OpenTicketReportDTO.class,
                criteriaBuilder.min(inspectionJoin.get("id")).alias("incidentId"),
                zoneJoin.get("code").alias("zoneCode"),
                divisionJoin.get("code").alias("divisionCode"),
                ticketRoot.get("ticketNo"),
                criteriaBuilder.count(inspectionJoin).alias("incidentCount"),
                ticketRoot.get("description"),
                ticketRoot.get("description"),
                criteriaBuilder.min(inspectionJoin.get("tripDate")).alias("firstIncidentDate"), // Calculate the earliest tripDate
                criteriaBuilder.selectCase()
                        .when(criteriaBuilder.equal(ticketRoot.get("assignTo"), "DIV"), ticketRoot.get("assignTo"))
                        .otherwise(criteriaBuilder.function(
                                "GROUP_CONCAT",
                                String.class,
                                criteriaBuilder.function("DISTINCT", String.class, firmJoin.get("name"))
                        )).alias("assignTo"),
                ticketRoot.get("targetDate"),
                criteriaBuilder.function("datediff", Long.class, // Use the min tripDate in the calculation
                        criteriaBuilder.currentDate(),
                        criteriaBuilder.min(inspectionJoin.get("tripDate"))).alias("daysPending")
        ));

        // Build WHERE clause
        List<Predicate> predicates = new ArrayList<>();

        if (zoneId != null && zoneId > 0) {
            predicates.add(criteriaBuilder.equal(zoneJoin.get("id"), zoneId));
        }
        if (divisionId != null && divisionId > 0) {
            predicates.add(criteriaBuilder.equal(divisionJoin.get("id"), divisionId));
        }
        if (firmId != null && firmId > 0) {
            predicates.add(criteriaBuilder.equal(firmJoin.get("id"), firmId));
        }
        if (ticketStatus != null) {
            predicates.add(criteriaBuilder.equal(ticketRoot.get("status"), ticketStatus));
        }

        // Exclude tickets with zero inspections
        predicates.add(criteriaBuilder.greaterThan(criteriaBuilder.size(ticketRoot.get("inspections")), 0));

        // Combine predicates
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        // Group by ticketNo
        criteriaQuery.groupBy(ticketRoot.get("ticketNo"));

        // Add pagination
        TypedQuery<OpenTicketReportDTO> query = entityManager.createQuery(criteriaQuery);

        // Fetch result list
        List<OpenTicketReportDTO> results = query.getResultList();

        List<TcasBreakingInspection> tcasBreakingInspectionList = new ArrayList<>();
        for (OpenTicketReportDTO dto : results) {
            TcasBreakingInspection incident = tcasBreakingInspectionRepo.findById(dto.getIncidentId()).orElse(null);

            tcasBreakingInspectionList.add(incident);
        }


        pieChartDataList.add(new PieChartData(("Station Kavach"),getStringAndCountListForStationRelated(tcasBreakingInspectionList,"Station Kavach")));
        pieChartDataList.add(new PieChartData(("Onboard Kavach"),getStringAndCountListForStationRelated(tcasBreakingInspectionList,"Onboard Kavach")));

        return pieChartDataList;
    }

    public List<StringAndCount> getStringAndCountListForStationRelated(List<TcasBreakingInspection> inspections,String issue) {
        // Define a Map to count occurrences of each root cause subcategory
        Map<String, Integer> countMap = new HashMap<>();

        // Iterate over inspections
        for (TcasBreakingInspection inspection : inspections) {
            if (inspection.getPossibleRootCause() != null &&
                    issue.equalsIgnoreCase(inspection.getPossibleRootCause().getName()) &&
                    inspection.getRootCauseSubCategory() != null) {

                String rootCauseSubCategoryName = inspection.getRootCauseSubCategory().getName();
                countMap.put(rootCauseSubCategoryName, countMap.getOrDefault(rootCauseSubCategoryName, 0) + 1);
            }
        }

        // Convert the map into a list of StringAndCount objects
        return countMap.entrySet().stream()
                .map(entry -> new StringAndCount(entry.getKey(), entry.getValue(), generateColorCode(entry.getKey())))
                .collect(Collectors.toList());
    }

    public List<OEMIncidentsAnalysisDTO> getOEMAnalysisReport(Long zoneId, Long divisionId, LocalDate startDate, LocalDate endDate) {

        List<OEMIncidentsAnalysisDTO> resultList = new ArrayList<>();

        List<Firm> firmList = findFirmsByZoneDivisionAndTripDate(zoneId,divisionId,startDate,endDate);

        for (Firm locoKavachFirm : firmList) {

            for (Firm stationKavachFirm : firmList) {
                OEMIncidentsAnalysisDTO oemIncidentsAnalysisDTO = new OEMIncidentsAnalysisDTO();
                oemIncidentsAnalysisDTO.setLocoKavachOem(locoKavachFirm.getName());
                oemIncidentsAnalysisDTO.setStationKavachOem(stationKavachFirm.getName());

                Map<String, Long> countsMap = getIssueCategoryCounts(stationKavachFirm.getId(),locoKavachFirm.getId(), zoneId, divisionId, startDate, endDate);
                oemIncidentsAnalysisDTO.setTotal(0L);
                // Iterating over the countsMap
                for (Map.Entry<String, Long> entry : countsMap.entrySet()) {
                    String issueCategory = entry.getKey();
                    Long count = entry.getValue();

                    if (issueCategory.equalsIgnoreCase("Mode Change")) {
                        oemIncidentsAnalysisDTO.setModeChange(count);
                    } else if (issueCategory.equalsIgnoreCase("Undesirable Braking")) {
                        oemIncidentsAnalysisDTO.setUndesirableBraking(count);
                    } else if (issueCategory.equalsIgnoreCase("Desirable Braking")) {
                        oemIncidentsAnalysisDTO.setDesirableBraking(count);
                    }
                    oemIncidentsAnalysisDTO.setTotal(oemIncidentsAnalysisDTO.getTotal() + count);
                }


                resultList.add(oemIncidentsAnalysisDTO);
            }
        }

        // Create the summary object
        OEMIncidentsAnalysisDTO summaryDTO = new OEMIncidentsAnalysisDTO();
        summaryDTO.setLocoKavachOem("Total");
        summaryDTO.setStationKavachOem("Summary");

        summaryDTO.setModeChange(0L);
        summaryDTO.setUndesirableBraking(0L);
        summaryDTO.setDesirableBraking(0L);
        summaryDTO.setTotal(0L);

        // Calculate the sums
        for (OEMIncidentsAnalysisDTO dto : resultList) {
            summaryDTO.setModeChange(summaryDTO.getModeChange() + (dto.getModeChange() != null ? dto.getModeChange() : 0L));
            summaryDTO.setUndesirableBraking(summaryDTO.getUndesirableBraking() + (dto.getUndesirableBraking() != null ? dto.getUndesirableBraking() : 0L));
            summaryDTO.setDesirableBraking(summaryDTO.getDesirableBraking() + (dto.getDesirableBraking() != null ? dto.getDesirableBraking() : 0L));
            summaryDTO.setTotal(summaryDTO.getTotal() + dto.getTotal());
        }

        // Add the summary object to the resultList
        resultList.add(summaryDTO);

        return resultList;
    }

    //for finding the firms present in incidents
    public List<Firm> findFirmsByZoneDivisionAndTripDate(Long zoneId, Long divisionId, LocalDate startDate, LocalDate endDate) {
        // Create CriteriaBuilder and CriteriaQuery
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Firm> query = criteriaBuilder.createQuery(Firm.class);

        // Root for TcasBreakingInspection
        Root<TcasBreakingInspection> root = query.from(TcasBreakingInspection.class);

        // Join with Division and Zone
        Join<TcasBreakingInspection, Division> divisionJoin = root.join("division");
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone");

        // Join with Station (faultyStation) and Loco
        Join<TcasBreakingInspection, Station> stationJoin = root.join("faultyStation");
        Join<TcasBreakingInspection, Loco> locoJoin = root.join("loco");

        // Select DISTINCT firms from both Station and Loco
        query.select(stationJoin.get("firm")).distinct(true);
        query.select(locoJoin.get("firm")).distinct(true);

        List<Predicate> predicates = new ArrayList<>();
        // Define predicates
        if (zoneId != null && zoneId > 0) {
            predicates.add(criteriaBuilder.equal(zoneJoin.get("id"), zoneId));
        }
        if (divisionId != null && divisionId > 0) {
            predicates.add(criteriaBuilder.equal(divisionJoin.get("id"), divisionId));
        }
//        Predicate datePredicate = criteriaBuilder.between(root.get("tripDate"), startDate, endDate);
        predicates.add(criteriaBuilder.between(root.get("tripDate"), startDate, endDate));

        // Combine predicates
        query.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        // Execute query and return result
        return entityManager.createQuery(query).getResultList();
    }

    public Map<String, Long> getIssueCategoryCounts(Integer stationKavachFirmId, Integer locoKavachFirmId, Long zoneId, Long divisionId, LocalDate startDate, LocalDate endDate) {
        // Create CriteriaBuilder
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Root for TcasBreakingInspection
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<TcasBreakingInspection> root = query.from(TcasBreakingInspection.class);

        // Joins for zone, division, faultyStation, and loco
        Join<TcasBreakingInspection, Division> divisionJoin = root.join("division");
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone");
        Join<TcasBreakingInspection, Station> stationJoin = root.join("faultyStation");
        Join<TcasBreakingInspection, Loco> locoJoin = root.join("loco");

        // List to hold dynamic predicates
        List<Predicate> predicates = new ArrayList<>();

        // Add predicates conditionally
        if (zoneId != null && zoneId > 0) {
            predicates.add(cb.equal(zoneJoin.get("id"), zoneId));
        }
        if (divisionId != null && divisionId > 0) {
            predicates.add(cb.equal(divisionJoin.get("id"), divisionId));
        }
        if (startDate != null && endDate != null) {
            predicates.add(cb.between(root.get("tripDate"), startDate, endDate));
        }
        predicates.add(cb.equal(stationJoin.get("firm").get("id"), stationKavachFirmId));
        predicates.add(cb.equal(locoJoin.get("firm").get("id"), locoKavachFirmId));

        // Initialize counts map
        Map<String, Long> counts = new HashMap<>();

        // Query for each issue category
        String[] issueCategories = {"Mode Change", "Desirable Braking", "Undesirable Braking"};

        for (String category : issueCategories) {
            // Create a fresh query for each issue category
            query.select(cb.count(root));

            // Add condition for specific issue category
            Predicate issueCategoryPredicate = cb.equal(root.get("issueCategory").get("name"), category);

            // Combine common predicates with the issue category predicate
            predicates.add(issueCategoryPredicate);
            query.where(predicates.toArray(new Predicate[0]));

            // Execute query and store count in the map
            Long count = entityManager.createQuery(query).getSingleResult();
            counts.put(category, count);

            // Remove the issueCategoryPredicate for the next iteration
            predicates.remove(issueCategoryPredicate);
        }

        return counts;
    }



    //    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
//    @Scheduled(cron = "0 0 9 * * ?")
    @Transactional
    public void emailOpenTicketReport() {
        List<String> ccEmailList = new ArrayList<>();
        List<String> bccEmailList = new ArrayList<>();
        List<Division> divisionList = divisionService.getAllDivision();
        for (Division division : divisionList) {

            List<OpenTicketReportDTO> openTicketReportDTOList = getOpenTicketReport(
                    PageRequest.of(0, 500),
                    Long.valueOf(division.getZone().getId()),
                    Long.valueOf(division.getId()),
                    null,
                    null,
                    null,
                    null,
                    true,
                    null
            ).getContent();

            if (!openTicketReportDTOList.isEmpty()) {
                try {
                    File dummyExcel = ExcelUtil.createOpenTicketReportExcel(openTicketReportDTOList);
                    List<File> attachments = Collections.singletonList(dummyExcel);
                    List<String> emailList = userService.getEmailsByZoneDivisionAndRole(division.getZone().getId(),division.getId(),"ROLE_DIVISION");
                    emailList.add("rohitkumar.railbit@gmail.com");
                    emailList.add("scrhqkavach@gmail.com");
                    for (String email : emailList) {
//                    log.info("Email : {} Division : {}", email, division.getName());
                        emailServiceImpl.mailService("FRACAS Open Tickets Report - " + division.getCode(), "Dear Sir, please find attachment", Collections.singletonList(email), ccEmailList, bccEmailList, attachments);
                    }

                } catch (IOException e) {
                    log.error("Failed to create dummy Excel file", e);
                }
            }

        }

    }

    //    0 0 0,6,12,18 * * ? - This part of the cron expression schedules the task to run at 12 AM, 6 AM, 12 PM, and 6 PM every day.
//    35 12 * * ? - This part of the cron expression schedules the task to run at 12:35 PM every day.
//    The @Scheduled annotation allows you to define a method to be executed at specific intervals or times using cron expressions. The @Transactional annotation ensures that the method runs within a transactional context.
//    @Scheduled(cron = "0 0 6,11,18 * * ?")
//    @Scheduled(cron = "0 59 23 * * ?")
    @Transactional
    public void emailNmsIncidentsReport() {
        List<String> ccEmailList = new ArrayList<>();
        List<String> bccEmailList = new ArrayList<>();

        LocalDate today = LocalDate.now();
        LocalDateTime startOfToday = today.atStartOfDay();
        LocalDateTime endOfToday = today.atTime(LocalTime.MAX);
        List<Division> divisionList = divisionService.getAllDivision();
        for (Division division : divisionList) {
            List<NMSIncidentDTO> nmsIncidentDTOList = locoMovementAnalytics.fetchLocoIncidents(Long.valueOf(division.getZone().getId()), Long.valueOf(division.getId()), null, null, null, startOfToday, endOfToday, "", PageRequest.of(0, 500)).getContent();

            if (!nmsIncidentDTOList.isEmpty()) {
                try {
                    File dummyExcel = ExcelUtil.createNmsIncidentReportExcel(nmsIncidentDTOList);
                    List<File> attachments = Collections.singletonList(dummyExcel);
                    List<String> emailList = userService.getEmailsByZoneDivisionAndRole(division.getZone().getId(),division.getId(),"ROLE_DIVISION");
                    emailList.add("rohitkumar.railbit@gmail.com");
                    emailList.add("scrhqkavach@gmail.com");
                    for (String email : emailList) {
                        log.info("Email : {} Division : {}", email, division.getName());
                        emailServiceImpl.mailService("FRACAS NMS Incidents - " + division.getCode(), "Dear Sir, please find attachment", emailList, ccEmailList, bccEmailList, attachments);
                    }
                } catch (IOException e) {
                    log.error("Failed to create dummy Excel file", e);
                }
            }
        }

    }

    public List<MonthWiseData> getMonthlyTripCounts() {
        String sql = """
        SELECT 
            DATE_FORMAT(inner_result.month_start, '%M %Y') AS month_year,
            SUM(inner_result.max_trip_no) AS total_trip_count
        FROM (
            SELECT 
                DATE_FORMAT(incidentDateTime, '%Y-%m-01') AS month_start,
                division_id,
                MAX(tripNo) AS max_trip_no
            FROM tcasbreakinginspection
            WHERE incidentDateTime >= DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 12 MONTH), '%Y-%m-01')
            GROUP BY 
                DATE_FORMAT(incidentDateTime, '%Y-%m-01'),
                division_id
        ) AS inner_result
        GROUP BY 
            inner_result.month_start
        ORDER BY 
            inner_result.month_start
        """;

        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> resultList = query.getResultList();

        List<MonthWiseData> response = new ArrayList<>();
        for (Object[] row : resultList) {
            String month = (String) row[0];
            Number value = (Number) row[1];

            String randomColor = HelpingHand.generateRandomColor();
            BarGraphDataSet dataSet = new BarGraphDataSet("Total Trips", value.doubleValue(), String.valueOf(value), randomColor);

            List<BarGraphDataSet> dataSetList = new ArrayList<>();
            dataSetList.add(dataSet);

            MonthWiseData monthWiseData = new MonthWiseData(month, dataSetList);
            response.add(monthWiseData);
        }

        return response;
    }

    public List<MonthWiseData> getMonthlyTicketCounts() {
        String sql = """
        SELECT 
            DATE_FORMAT(STR_TO_DATE(CONCAT(year, '-', month, '-01'), '%Y-%m-%d'), '%M %Y') AS month_year,
            COUNT(*) AS ticket_count
        FROM (
            SELECT 
                YEAR(tripDate) AS year,
                MONTH(tripDate) AS month
            FROM 
                incidentticket
            WHERE 
                tripDate >= DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 12 MONTH), '%Y-%m-01')
        ) AS sub
        GROUP BY 
            year, month
        ORDER BY 
            year, month
        """;


        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> resultList = query.getResultList();

        List<MonthWiseData> response = new ArrayList<>();
        for (Object[] row : resultList) {
            String month = (String) row[0];
            Number count = (Number) row[1];

            String color = HelpingHand.generateRandomColor();
            BarGraphDataSet dataSet = new BarGraphDataSet(
                    "Total Tickets",
                    count.doubleValue(),
                    String.valueOf(count),
                    color
            );

            response.add(new MonthWiseData(month, List.of(dataSet)));
        }

        return response;
    }


}