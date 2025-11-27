package com.railbit.tcasanalysis.service.serviceImpl;


import com.railbit.tcasanalysis.entity.*;
import com.railbit.tcasanalysis.entity.analysis.*;
import com.railbit.tcasanalysis.entity.loco.Loco;
import com.railbit.tcasanalysis.repository.AssignedIncidentRepo;
import com.railbit.tcasanalysis.repository.IncidentNoRepo;
import com.railbit.tcasanalysis.repository.TcasBreakingInspectionRepo;
import com.railbit.tcasanalysis.service.*;
import com.railbit.tcasanalysis.util.HelpingHand;
import jakarta.persistence.Transient;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TcasBreakingInspectionServiceImpl implements TcasBreakingInspectionService {

    private final TcasBreakingInspectionRepo tcasBreakingInspectionRepo;
    private final IncidentTicketTrackService incidentTicketTrackService;
    private final IncidentTicketService incidentTicketService;
    private final LocoService locoService;
    private final FirmService firmService;
    private final TcasService tcasService;
    private final StationService stationService;
    private final PossibleIssueService possibleIssueService;
    private final IssueCategoryService issueCategoryService;
    private final DivisionService divisionService;
    private final PossibleRootCauseService possibleRootCauseService;
    private final RootCauseSubCategoryService rootCauseSubCategoryService;
    private final TcasBreakingInspectionStatusService tcasBreakingInspectionStatusService;
    private final NotificationService notificationService;
    private final FCMNotificationService fcmNotificationService;
    private final FCMTokenService fcmTokenService;
    private final UserService userService;
    private final EmailService emailService;
    private final OtpService otpService;
    private final IncidentNoService incidentNoService;
    private final IncidentNoRepo incidentNoRepo;
    private final AssignedIncidentRepo assignedIncidentRepo;

    private final Logger logger = LoggerFactory.getLogger(TcasBreakingInspectionServiceImpl.class);

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<TcasBreakingInspection> getAllTcasBreakingInspection() {
        return findAndSetLastAssignedUserStatusInIncidentList(tcasBreakingInspectionRepo.findAll());
    }

    @Override
    public List<TcasBreakingInspection> getAllTcasBreakingInspectionByLatestTripDate() {
        return findAndSetLastAssignedUserStatusInIncidentList(tcasBreakingInspectionRepo.findByLatestTripDate());
    }

    @Override
    public List<TcasBreakingInspection> getAllIncidentsByLatestTripDate() {
//        Pageable pageable = PageRequest.of(0, 2000, Sort.by("tripDate").descending());
//        List<TcasBreakingInspection> incidentList = tcasBreakingInspectionRepo.findAll((root, query, criteriaBuilder) -> {
//            List<Predicate> predicates = new ArrayList<>();
//
//            IssueCategory dsrblIssueCat = issueCategoryService.getIssueCategoryByName("Desirable Braking");
//            predicates.add(criteriaBuilder.notEqual(root.get("issueCategory").get("id"), dsrblIssueCat.getId()));
//
//            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
//
//        },pageable);

        List<TcasBreakingInspection> incidentList = new ArrayList<>();

        return findAndSetLastAssignedUserStatusInIncidentList(incidentList);
    }

    @Override
    public List<TcasBreakingInspection> getAllTcasBreakingInspectionByDivisionId(int divisionID) {
        return findAndSetLastAssignedUserStatusInIncidentList(tcasBreakingInspectionRepo.findByDivisionIdOrderByTripDateDesc(divisionID));
    }

    @Override
    public TcasBreakingInspection getTcasBreakingInspectionByIncidentTag(String incidentTag) {
        return tcasBreakingInspectionRepo.findFirstByIncidentTagOrderByIdDesc(incidentTag);
    }

    @Override
    public List<TcasBreakingInspection> getAllByMonth(int month) {
        return findAndSetLastAssignedUserStatusInIncidentList(tcasBreakingInspectionRepo.getAllByMonth(month));
    }

    @Override
    public List<TcasBreakingInspection> getAllByUserId(Long userId) {
        return findAndSetLastAssignedUserStatusInIncidentList(tcasBreakingInspectionRepo.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (userId != null && userId != 0) {
                predicates.add(criteriaBuilder.equal(root.get("user").get("id"), userId));
            }
            // Adding ORDER BY clause
            query.orderBy(criteriaBuilder.desc(root.get("tripDate")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        }));
    }

    @Override
    public List<TcasBreakingInspection> getAllByAssignedToUser(Long userId) {
        return findAndSetLastAssignedUserStatusInIncidentList(assignedIncidentRepo.findByAssignedToUserGrouped(userId));
    }

    public List<TcasBreakingInspection> findAndSetLastAssignedUserStatusInIncidentList(List<TcasBreakingInspection> inspectionList) {
        List<TcasBreakingInspection> resultList = new ArrayList<>();
        for (TcasBreakingInspection inspection : inspectionList) {

            resultList.add(tcasBreakingInspectionStatusService.findAndSetLastAssignedUserStatusInIncidentListInIncident(inspection));

        }

        logger.info("Count : {}", resultList.size());

        return resultList;
    }

    @Override
    public List<TcasBreakingInspection> getAllByFirmId(Integer firmId) {
        return findAndSetLastAssignedUserStatusInIncidentList(tcasBreakingInspectionRepo.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (firmId != null && firmId != 0) {
                // Adding condition locoFirm.id = firmId or firmSection.id = firmId
                Predicate predicate1 = criteriaBuilder.equal(root.get("loco").get("firm").get("id"), firmId);
                Predicate predicate2 = criteriaBuilder.equal(root.get("faultyStation").get("firm").get("id"), firmId);
                predicates.add(criteriaBuilder.or(predicate1, predicate2));
            }
            // Adding ORDER BY clause
            query.orderBy(criteriaBuilder.desc(root.get("tripDate")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        }));
    }

    @Override
    public Page<TcasBreakingInspection> getFilteredInspections(
            LocalDateTime fromDate, LocalDateTime toDate,
            String status, Integer issueCategoryId,
            Integer possibleIssueId, Integer possibleRootCauseId, Integer rootCauseSubCategoryId,
            Integer stationId,Integer zoneId, Integer divisionId,String withIssue,Integer assignFirmId,
            Integer firmId, Integer locoId, Integer locoTypeId, String locoVersion, String condemned, String searchQuery, Pageable pageable) {
        logger.info("From Date : {}", fromDate);
        logger.info("To Date : {}", toDate);
        logger.info("Ticket Status : {}", status);
        // Call the repository method with Specification and Pageable
        return tcasBreakingInspectionRepo.findAll(
                (root, query, criteriaBuilder) -> {

                    List<Predicate> predicates = new ArrayList<>();
//                    Boolean statusBool;
                    Boolean statusBool = null;

                    if (!StringUtils.isEmpty(status) && !status.equalsIgnoreCase("all")) {

                        if (!status.equalsIgnoreCase("incidents_with_attached_ticket") && !status.equalsIgnoreCase("incidents_without_attached_ticket")) {
                            // Subquery to fetch one `TcasBreakingInspection` per `IncidentTicket`
                            Subquery<Long> subquery = query.subquery(Long.class);
                            Root<TcasBreakingInspection> subRoot = subquery.from(TcasBreakingInspection.class);
                            subquery.select(criteriaBuilder.max(subRoot.get("id"))); // Fetch the minimum ID (or customize as needed)
                            subquery.where(criteriaBuilder.equal(subRoot.get("incidentTicket"), root.get("incidentTicket")));

                            // Add predicate to match only one record per `IncidentTicket`
                            predicates.add(criteriaBuilder.equal(root.get("id"), subquery));

                            String statusLower = status.toLowerCase();

                            if ("open".equals(statusLower)) {
                                statusBool = true;
                            } else if ("close".equals(statusLower)) {
                                statusBool = false;
                            }

                            // Add a condition to ensure IncidentTicket is not null
                            predicates.add(criteriaBuilder.isNotNull(root.get("incidentTicket")));
                            // Status filter for IncidentTicket
                            if (statusBool != null) {
                                predicates.add(criteriaBuilder.equal(root.get("incidentTicket").get("status"), statusBool));
                            }
                        } else if (status.equalsIgnoreCase("incidents_without_attached_ticket")) {
                            // Add a condition to ensure IncidentTicket is not null
                            predicates.add(criteriaBuilder.isNull(root.get("incidentTicket")));
                        } else if (status.equalsIgnoreCase("incidents_with_attached_ticket")) {
                            // Add a condition to ensure IncidentTicket is not null
                            predicates.add(criteriaBuilder.isNotNull(root.get("incidentTicket")));
                        }

                    }

                    // Date range filter
                    if (fromDate != null && toDate != null) {
                        predicates.add(criteriaBuilder.between(root.get("tripDate"), fromDate, toDate));
                    }

                    // Issue Category filter
                    if (issueCategoryId != null && issueCategoryId > 0) {
                        predicates.add(criteriaBuilder.equal(root.get("issueCategory").get("id"), issueCategoryId));
                    }

                    if (!StringUtils.isEmpty(withIssue) && !withIssue.equalsIgnoreCase("all")) {
                        if (withIssue.equalsIgnoreCase("with_issue")) {
                            predicates.add(criteriaBuilder.notEqual(root.get("issueCategory").get("name"), "No Issue"));
                        } else if (withIssue.equalsIgnoreCase("without_issue")) {
                            predicates.add(criteriaBuilder.equal(root.get("issueCategory").get("name"), "No Issue"));
                        }
                    }

                    // Possible Issue filter
                    if (possibleIssueId != null && possibleIssueId > 0) {
                        predicates.add(criteriaBuilder.equal(root.get("possibleIssue").get("id"), possibleIssueId));
                    }

                    // Possible Root Cause filter
                    if (possibleRootCauseId != null && possibleRootCauseId > 0) {
                        predicates.add(criteriaBuilder.equal(root.get("possibleRootCause").get("id"), possibleRootCauseId));
                    }

                    // Root Cause SubCategory filter
                    if (rootCauseSubCategoryId != null && rootCauseSubCategoryId > 0) {
                        predicates.add(criteriaBuilder.equal(root.get("rootCauseSubCategory").get("id"), rootCauseSubCategoryId));
                    }

                    // Assuming these joins are already created
                    Join<TcasBreakingInspection, RootCauseSubCategory> rootCauseSubCategoryJoin = root.join("rootCauseSubCategory", JoinType.LEFT);
                    Join<TcasBreakingInspection, PossibleRootCause> possibleRootCauseJoin = root.join("possibleRootCause", JoinType.LEFT);

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
//                    predicates.add(rootCauseSubCategoryPredicate);
//                    predicates.add(possibleRootCausePredicate);

                    // Zone filter
                    if (zoneId != null && zoneId > 0) {
                        predicates.add(criteriaBuilder.equal(root.get("division").get("zone").get("id"), zoneId));
                    }

                    // Division filter
                    if (divisionId != null && divisionId > 0) {
                        predicates.add(criteriaBuilder.equal(root.get("division").get("id"), divisionId));
                    }

                    // Station filter
                    if (stationId != null && stationId > 0) {
                        predicates.add(criteriaBuilder.equal(root.get("faultyStation").get("id"), stationId));
                    }

                    // Loco filter
                    if (locoId != null && locoId > 0) {
                        predicates.add(criteriaBuilder.equal(root.get("loco").get("id"), locoId));
                    }

                    // Loco Type filter
                    if (locoTypeId != null && locoTypeId > 0) {
                        predicates.add(criteriaBuilder.equal(root.get("loco").get("locoType").get("id"), locoTypeId));
                    }

                    // Loco Version filter
                    if (locoVersion != null && !StringUtils.isEmpty(locoVersion) && !locoVersion.equalsIgnoreCase("all")) {
                        predicates.add(criteriaBuilder.equal(root.get("loco").get("version"), locoVersion));
                    }

                    // Condemned filter
                    if (condemned != null && StringUtils.isEmpty(condemned) && !condemned.equalsIgnoreCase("all")) {
                        predicates.add(criteriaBuilder.equal(root.get("loco").get("condemned"), condemned));
                    }

                    // Firm filter
                    if (firmId != null && firmId > 0) {
                        Predicate predicate1 = criteriaBuilder.equal(root.get("loco").get("firm").get("id"), firmId);
                        Predicate predicate2 = criteriaBuilder.equal(root.get("faultyStation").get("firm").get("id"), firmId);
                        predicates.add(criteriaBuilder.or(predicate1, predicate2));
                    }

                    //Assigned Firms Filter
                    if (assignFirmId != null && assignFirmId > 0) {
                        predicates.add(criteriaBuilder.equal(root.get("incidentTicket").get("assignedFirms").get("id"), assignFirmId));
                    }

                    //For Search Item
                    if (searchQuery != null && !searchQuery.isEmpty()) {
                        predicates.add(criteriaBuilder.or(
                                criteriaBuilder.like(root.get("briefDescription"), "%" + searchQuery + "%"),
                                criteriaBuilder.like(root.get("rootCauseDescription"), "%" + searchQuery + "%"),
                                criteriaBuilder.like(root.get("loco").get("locoNo"), "%" + searchQuery + "%"),
                                criteriaBuilder.like(root.get("remark"), "%" + searchQuery + "%"),
                                criteriaBuilder.like(root.get("incidentTicket").get("description"), "%" + searchQuery + "%"),
                                criteriaBuilder.like(root.get("incidentTicket").get("ticketNo"), "%" + searchQuery + "%")
                        ));
                    }

                    // Sorting Mechanism
                    // Create expressions for YEAR and MONTH of the tripDate
                    Expression<Integer> yearExpr = criteriaBuilder.function("YEAR", Integer.class, root.get("tripDate"));
                    Expression<Integer> monthExpr = criteriaBuilder.function("MONTH", Integer.class, root.get("tripDate"));

                    // Apply ordering
                    query.orderBy(
                            criteriaBuilder.asc(root.get("division").get("id")),
                            criteriaBuilder.desc(yearExpr),   // ORDER BY YEAR(t.tripDate) DESC
                            criteriaBuilder.desc(monthExpr),  // ORDER BY MONTH(t.tripDate) DESC
                            criteriaBuilder.asc(root.get("tripNo"))  // ORDER BY tripNo DESC
                    );


                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                },
                pageable // Apply pageable directly to enforce pagination and sorting
        );
    }

    @Override
    public List<TcasBreakingInspection> downloadFilteredInspections(
            LocalDateTime fromDate, LocalDateTime toDate,
            String status, Integer issueCategoryId,
            Integer possibleIssueId, Integer possibleRootCauseId, Integer rootCauseSubCategoryId,
            Integer stationId,Integer zoneId, Integer divisionId,String withIssue,Integer assignFirmId,
            Integer firmId, Integer locoId, Integer locoTypeId, String locoVersion, String condemned, String searchQuery) {
        logger.info("From Date : {}", fromDate);
        logger.info("To Date : {}", toDate);
        logger.info("Ticket Status : {}", status);
        // Call the repository method with Specification and Pageable
        return tcasBreakingInspectionRepo.findAll(
                (root, query, criteriaBuilder) -> {

                    List<Predicate> predicates = new ArrayList<>();
//                    Boolean statusBool;
                    Boolean statusBool = null;

                    if (!StringUtils.isEmpty(status) && !status.equalsIgnoreCase("all")) {

                        if (!status.equalsIgnoreCase("incidents_with_attached_ticket") && !status.equalsIgnoreCase("incidents_without_attached_ticket")) {
                            // Subquery to fetch one `TcasBreakingInspection` per `IncidentTicket`
                            Subquery<Long> subquery = query.subquery(Long.class);
                            Root<TcasBreakingInspection> subRoot = subquery.from(TcasBreakingInspection.class);
                            subquery.select(criteriaBuilder.min(subRoot.get("id"))); // Fetch the minimum ID (or customize as needed)
                            subquery.where(criteriaBuilder.equal(subRoot.get("incidentTicket"), root.get("incidentTicket")));

                            // Add predicate to match only one record per `IncidentTicket`
                            predicates.add(criteriaBuilder.equal(root.get("id"), subquery));

                            String statusLower = status.toLowerCase();

                            if ("open".equals(statusLower)) {
                                statusBool = true;
                            } else if ("close".equals(statusLower)) {
                                statusBool = false;
                            }

                            // Add a condition to ensure IncidentTicket is not null
                            predicates.add(criteriaBuilder.isNotNull(root.get("incidentTicket")));
                            // Status filter for IncidentTicket
                            if (statusBool != null) {
                                predicates.add(criteriaBuilder.equal(root.get("incidentTicket").get("status"), statusBool));
                            }
                        } else if (status.equalsIgnoreCase("incidents_without_attached_ticket")) {
                            // Add a condition to ensure IncidentTicket is not null
                            predicates.add(criteriaBuilder.isNull(root.get("incidentTicket")));
                        } else if (status.equalsIgnoreCase("incidents_with_attached_ticket")) {
                            // Add a condition to ensure IncidentTicket is not null
                            predicates.add(criteriaBuilder.isNotNull(root.get("incidentTicket")));
                        }

                    }

                    // Date range filter
                    if (fromDate != null && toDate != null) {
                        predicates.add(criteriaBuilder.between(root.get("tripDate"), fromDate, toDate));
                    }

                    // Issue Category filter
                    if (issueCategoryId != null && issueCategoryId > 0) {
                        predicates.add(criteriaBuilder.equal(root.get("issueCategory").get("id"), issueCategoryId));
                    }

                    if (!StringUtils.isEmpty(withIssue) && !withIssue.equalsIgnoreCase("all")) {
                        if (withIssue.equalsIgnoreCase("with_issue")) {
                            predicates.add(criteriaBuilder.notEqual(root.get("issueCategory").get("name"), "No Issue"));
                        } else if (withIssue.equalsIgnoreCase("without_issue")) {
                            predicates.add(criteriaBuilder.equal(root.get("issueCategory").get("name"), "No Issue"));
                        }
                    }

                    // Possible Issue filter
                    if (possibleIssueId != null && possibleIssueId > 0) {
                        predicates.add(criteriaBuilder.equal(root.get("possibleIssue").get("id"), possibleIssueId));
                    }

                    // Possible Root Cause filter
                    if (possibleRootCauseId != null && possibleRootCauseId > 0) {
                        predicates.add(criteriaBuilder.equal(root.get("possibleRootCause").get("id"), possibleRootCauseId));
                    }

                    // Root Cause SubCategory filter
                    if (rootCauseSubCategoryId != null && rootCauseSubCategoryId > 0) {
                        predicates.add(criteriaBuilder.equal(root.get("rootCauseSubCategory").get("id"), rootCauseSubCategoryId));
                    }

                    // Zone filter
                    if (zoneId != null && zoneId > 0) {
                        predicates.add(criteriaBuilder.equal(root.get("division").get("zone").get("id"), zoneId));
                    }

                    // Division filter
                    if (divisionId != null && divisionId > 0) {
                        predicates.add(criteriaBuilder.equal(root.get("division").get("id"), divisionId));
                    }

                    // Station filter
                    if (stationId != null && stationId > 0) {
                        predicates.add(criteriaBuilder.equal(root.get("faultyStation").get("id"), stationId));
                    }

                    // Loco filter
                    if (locoId != null && locoId > 0) {
                        predicates.add(criteriaBuilder.equal(root.get("loco").get("id"), locoId));
                    }

                    // Loco Type filter
                    if (locoTypeId != null && locoTypeId > 0) {
                        predicates.add(criteriaBuilder.equal(root.get("loco").get("locoType").get("id"), locoTypeId));
                    }

                    // Loco Version filter
                    if (locoVersion != null && !StringUtils.isEmpty(locoVersion) && !locoVersion.equalsIgnoreCase("all")) {
                        predicates.add(criteriaBuilder.equal(root.get("loco").get("version"), locoVersion));
                    }

                    // Condemned filter
                    if (condemned != null && StringUtils.isEmpty(condemned) && !condemned.equalsIgnoreCase("all")) {
                        predicates.add(criteriaBuilder.equal(root.get("loco").get("condemned"), condemned));
                    }

                    // Firm filter
                    if (firmId != null && firmId > 0) {
                        Predicate predicate1 = criteriaBuilder.equal(root.get("loco").get("firm").get("id"), firmId);
                        Predicate predicate2 = criteriaBuilder.equal(root.get("faultyStation").get("firm").get("id"), firmId);
                        predicates.add(criteriaBuilder.or(predicate1, predicate2));
                    }

                    //Assigned Firms Filter
                    if (assignFirmId != null && assignFirmId > 0) {
                        predicates.add(criteriaBuilder.equal(root.get("incidentTicket").get("assignedFirms").get("id"), assignFirmId));
                    }

                    //For Search Item
                    if (searchQuery != null && !searchQuery.isEmpty()) {
                        predicates.add(criteriaBuilder.or(
                                criteriaBuilder.like(root.get("briefDescription"), "%" + searchQuery + "%"),
                                criteriaBuilder.like(root.get("rootCauseDescription"), "%" + searchQuery + "%"),
                                criteriaBuilder.like(root.get("loco").get("locoNo"), "%" + searchQuery + "%"),
                                criteriaBuilder.like(root.get("remark"), "%" + searchQuery + "%"),
                                criteriaBuilder.like(root.get("incidentTicket").get("description"), "%" + searchQuery + "%"),
                                criteriaBuilder.like(root.get("incidentTicket").get("ticketNo"), "%" + searchQuery + "%")
                        ));
                    }

                    // Sorting Mechanism
                    // Create expressions for YEAR and MONTH of the tripDate
                    Expression<Integer> yearExpr = criteriaBuilder.function("YEAR", Integer.class, root.get("tripDate"));
                    Expression<Integer> monthExpr = criteriaBuilder.function("MONTH", Integer.class, root.get("tripDate"));

                    // Apply ordering
                    query.orderBy(
                            criteriaBuilder.desc(yearExpr),   // ORDER BY YEAR(t.tripDate) DESC
                            criteriaBuilder.desc(monthExpr),  // ORDER BY MONTH(t.tripDate) DESC
                            criteriaBuilder.asc(root.get("tripNo"))  // ORDER BY tripNo DESC
                    );


                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
        );
    }

    @Override
    public TcasBreakingInspection getTcasBreakingInspectionById(Long id) {
        Optional<TcasBreakingInspection> data = tcasBreakingInspectionRepo.findById(id);
        if (data.isEmpty())
            throw new NoSuchElementException("Inspection Not Found");
        return data.get();
    }

    @Override
    public void updateTcasBreakingInspection(TcasBreakingInspection tcasBreakingInspection) throws Exception {

        User user = new User();
        user.setId(HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication()));
        tcasBreakingInspection.setUser(user);
        logger.info("Update Incident : {}", tcasBreakingInspection);

        TcasBreakingInspection existingEntity = tcasBreakingInspectionRepo.findById(tcasBreakingInspection.getId())
                .orElseThrow(() -> new Exception("TcasBreakingInspection not found"));

        if (tcasBreakingInspection.getIncidentTicket() != null) {
            IncidentTicket incidentTicket = tcasBreakingInspection.getIncidentTicket();
            incidentTicket.setTripDate(tcasBreakingInspection.getTripDate());
            incidentTicket.setDivision(tcasBreakingInspection.getDivision());
            incidentTicket.setUser(tcasBreakingInspection.getUser());
            if (incidentTicket.getId() == null) {
                incidentTicketService.addIncidentTicket(incidentTicket);
            } else {
                incidentTicketService.updateIncidentTicket(incidentTicket);
            }

            user = userService.getUserByUserId(user.getId());
            if (user.getRole().getName().equalsIgnoreCase("ROLE_OEM")) {
                String rootCause = tcasBreakingInspection.getRootCauseDescription();
                String existingRootCause = existingEntity.getRootCauseDescription();

                if (!rootCause.equalsIgnoreCase(existingRootCause)) {
                    IncidentTicketTrack incidentTicketTrack = new IncidentTicketTrack();
                    incidentTicketTrack.setIncidentTicket(incidentTicket);
                    incidentTicketTrack.setStatus("Root Cause Description Added");
                    incidentTicketTrack.setRemarks(rootCause);
                    incidentTicketTrack.setUser(incidentTicket.getUser());

                    incidentTicketTrackService.postIncidentTicketTrack(incidentTicketTrack);
                }

            }

        }

        // Map DTO properties to existing entity
        modelMapper.map(tcasBreakingInspection, existingEntity);

        // Save the updated entity
        TcasBreakingInspection savedEntity = tcasBreakingInspectionRepo.save(existingEntity);
    }

    @Override
    public Long addIncident(TcasBreakingInspection tcasBreakingInspection) throws Exception {

        User user = new User();
        user.setId(HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication()));
        tcasBreakingInspection.setUser(user);
        logger.info("Add Incident : {}", tcasBreakingInspection);

        if (tcasBreakingInspection.getIncidentTicket() != null) {
            IncidentTicket incidentTicket = tcasBreakingInspection.getIncidentTicket();
            if (incidentTicket.getId() == null) {
                incidentTicket.setTripDate(tcasBreakingInspection.getTripDate());
                incidentTicket.setDivision(tcasBreakingInspection.getDivision());
                incidentTicket.setUser(tcasBreakingInspection.getUser());
                incidentTicketService.addIncidentTicket(incidentTicket);
            } else {
                incidentTicketService.updateIncidentTicket(incidentTicket);
            }
        }

        TcasBreakingInspection incident = tcasBreakingInspectionRepo.save(tcasBreakingInspection);

        if (incident.getId() != null) {
            TcasBreakingInspection newTcasBreakingInspection = tcasBreakingInspectionRepo.findById(incident.getId()).orElse(null);

            logger.info("Incident Added : {}", newTcasBreakingInspection);
            if (newTcasBreakingInspection != null) {

                Loco loco = null;
                if (newTcasBreakingInspection.getLoco() != null) {
                    loco = locoService.getLocoById(newTcasBreakingInspection.getLoco().getId());
                }
                Station station = null;
                Division division = null;
                if (newTcasBreakingInspection.getFaultyStation() != null) {
                    station = stationService.getStationById(newTcasBreakingInspection.getFaultyStation().getId());
                    division = station.getDivision();
                }

                IssueCategory issueCategory = null;
                if (newTcasBreakingInspection.getIssueCategory() != null) {
                    issueCategory = issueCategoryService.getIssueCategoryById(newTcasBreakingInspection.getIssueCategory().getId());
                    if (issueCategory.getName().equalsIgnoreCase("no issue")) {
                        return newTcasBreakingInspection.getId();
                    }
                }
                PossibleIssue possibleIssue = null;
                if (newTcasBreakingInspection.getPossibleIssue() != null) {
                    possibleIssue = possibleIssueService.getPossibleIssueById(newTcasBreakingInspection.getPossibleIssue().getId());
                }
                PossibleRootCause possibleRootCause = null;
                if (newTcasBreakingInspection.getPossibleRootCause() != null) {
                    possibleRootCause = possibleRootCauseService.getPossibleRootCauseById(newTcasBreakingInspection.getPossibleRootCause().getId());
                }


                //                        "Reported By: " + user.getName() + "\n" +
                //                        "Designation: " + (user.getDesignation() == null ? "" : user.getDesignation().getName()) + "\n" +
                //                        "Date & Time of Incident Reporting: " + HelpingHand.dateFormatter.format(newTcasBreakingInspection.getTripDate()) + " " + HelpingHand.timeFormatter.format(newTcasBreakingInspection.getTripDate()) + "\n" +
                String mailBody = "Dear User" + ",\n\n" +
                        "This mail is to inform you of an incident that has been reported in the kavach system. Below are the details of the incident:\n\n" +
                        "Incident Details:\n\n" +
                        "Trip Date: " + HelpingHand.dateFormatter.format(newTcasBreakingInspection.getTripDate()) + "\n" +
//                        "Incident Tag: " + newTcasBreakingInspection.getIncidentTag() + "\n" +
                        "Loco ID/Number: " + (loco == null ? "" : loco.getLocoNo()) + "\n" +
                        "Division: " + (division == null ? "" : division.getName()) + "\n" +
                        "Station: " + (station == null ? "" : station.getName()) + "\n" +
//                        "Reported By: " + user.getName() + "\n" +
//                        "Designation: " + (user.getDesignation() == null ? "" : user.getDesignation().getName()) + "\n" +
                        "Issue Category: " +
                        (issueCategory != null ? issueCategory.getName() : "") + "\n" +
                        "Possible Issue: " +
                        (possibleIssue != null ? possibleIssue.getName() : "") + "\n" +
                        "Root Cause: " +
                        (possibleRootCause != null ? possibleRootCause.getName() : "") + "\n\n" +
//                        "Date & Time of Incident Reporting: " + HelpingHand.dateFormatter.format(newTcasBreakingInspection.getTripDate()) + " " + HelpingHand.timeFormatter.format(newTcasBreakingInspection.getTripDate()) + "\n" +
                        "We kindly request your immediate attention to this matter to ensure that the necessary preventive and corrective actions are taken promptly.\n\n" +
                        "Thank you for your prompt action on this matter.\n\n" +
                        "Best Regards,\n\n" +
                        "Kavach Administration" + "\n";
                String mailSubject = "New Incident Reported";
//                List<String> toList = Collections.singletonList(toUser.getEmail());
                List<User> toUserList = userService.getAllUsersForAddIncident(newTcasBreakingInspection);
//                toList.add("scrhqkavach@gmail.com");
//                logger.info("Emails for Add Incident : {}", toUserEmailList);

                for (User userForMail : toUserList) {
                    logger.info("Email for Add Incident : {}", userForMail.getEmail());
//                    emailService.mailService(mailSubject,mailBody, Collections.singletonList(userForMail.getEmail()));

                    logger.info("Contact for Add Incident: {}", userForMail.getContact());
                    //Sending OTP
//                    try {
//                        otpService.sendAddIncidentSms("User" , (loco == null ? "" : loco.getLocoNo()),  newTcasBreakingInspection.getIncidentTag(), (station == null ? "" : station.getName()), userForMail.getContact())
//                                .doOnNext(response -> {
////                                System.out.println("Mobile: " + contacts);
//                                    System.out.println("Response Code: " + response.getResponseCode());
//                                    System.out.println("Response Message: " + response.getResponseMessage());
//                                    System.out.println("Transaction ID: " + response.getTxId());
//                                    System.out.println("SMS Encoding: " + response.getSmsEncoding());
//                                    System.out.println("SMS Length: " + response.getSmsLength());
//                                    System.out.println("Balance Used: " + response.getBalanceUsed());
//                                    System.out.println("Total Mobile Number Submitted: " + response.getTotalMobileNumberSubmitted());
//                                })
//                                .block();
//                    } catch (Exception e){
//                        logger.error("Exception : ", e);
//                    }
                }

            }
        }

        return incident.getId();

    }

    public String getIncidentTag(Integer faultyStationId,LocalDate tripDate) {
        LocalTime currentTime = LocalTime.now();

        LocalDateTime dateTime = tripDate.atTime(currentTime);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyHHmmss");

        String formattedDate = dateTime.format(formatter);

        Station faultyStation = stationService.getStationById(faultyStationId);
        Division division = faultyStation.getDivision();
        String divisionCode = division.getCode();

        Long incidentCount = division.getIncidentCount();

        //        division.setIncidentCount(incidentCount + 1);
//        divisionService.updateDivision(division);

        return divisionCode +"/"+ formattedDate;
    }

    @Override
    public Long addTcasBreakingInspection(TcasBreakingInspection tcasBreakingInspection, List<MultipartFile> fileList) throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
        Date currentDate = new Date();
        String formattedDate = sdf.format(currentDate);

        IssueCategory issueCategory = issueCategoryService.getIssueCategoryById(tcasBreakingInspection.getIssueCategory() == null ? 0 : tcasBreakingInspection.getIssueCategory().getId());
        if (issueCategory.getName().equalsIgnoreCase("desirable braking")) {

            String uniqueIncidentTag = "DSRBL" + formattedDate;
            tcasBreakingInspection.setIncidentTag(uniqueIncidentTag);
            tcasBreakingInspection.setStatus("Close");

        } else {
            Long incidentNo_db = 0L;
            long parsed_incidentNo = 0L;
            String divisionCode = "";
            if (tcasBreakingInspection.getFaultyStation() != null && tcasBreakingInspection.getFaultyStation().getId() != null) {
                Station faultyStation = stationService.getStationById(tcasBreakingInspection.getFaultyStation().getId());
                Division division = faultyStation.getDivision();
//                Division division = divisionService.getDivisionByCode("HYB");
                divisionCode = division.getCode();

                incidentNo_db = division.getIncidentCount();

                if (incidentNo_db == 0) {
                    parsed_incidentNo = 1L;
                } else {
                    parsed_incidentNo = incidentNo_db + 1L;
                }

                division.setIncidentCount(parsed_incidentNo);
                divisionService.updateDivision(division);

            } else {
                divisionCode = "UNKWN";
            }

            String uniqueIncidentTag = divisionCode +"/"+ formattedDate + "/" + parsed_incidentNo;

            tcasBreakingInspection.setIncidentTag(uniqueIncidentTag);

            if (tcasBreakingInspection.getStatus().equalsIgnoreCase("Close")) {
                tcasBreakingInspection.setStatus("Close");
            } else {
                tcasBreakingInspection.setStatus("Open");
            }

        }

        Loco loco = tcasBreakingInspection.getLoco();
//        logger.info("Loco {}",loco);

        if (loco.getId() == null && !StringUtils.isEmpty(loco.getLocoNo())){
            int newLocoId = locoService.postLoco(loco);
            if (newLocoId > 0) {
                Loco newLoco = locoService.getLocoById(newLocoId);
                tcasBreakingInspection.setLoco(newLoco);
            } else {
                tcasBreakingInspection.setLoco(null);
            }
        } else if (loco.getId() == null && StringUtils.isEmpty(loco.getLocoNo())) {
            tcasBreakingInspection.setLoco(null);
        }

        Station faultyStation = tcasBreakingInspection.getFaultyStation();

        if (faultyStation.getId() == null && !StringUtils.isEmpty(faultyStation.getCode())){
            int newStationId = stationService.postStation(faultyStation);
            if (newStationId > 0) {
                Station newStation = stationService.getStationById(newStationId);
                tcasBreakingInspection.setFaultyStation(newStation);
            } else {
                tcasBreakingInspection.setFaultyStation(null);
            }
        } else if (faultyStation.getId() == null && StringUtils.isEmpty(faultyStation.getCode())) {
            tcasBreakingInspection.setFaultyStation(null);
        }

        PossibleRootCause possibleRootCause = tcasBreakingInspection.getPossibleRootCause();
        if (possibleRootCause != null && possibleRootCause.getId() == null && !StringUtils.isEmpty(possibleRootCause.getName())){
            int newId = possibleRootCauseService.postPossibleRootCause(possibleRootCause);
            if (newId > 0) {
                PossibleRootCause newPossibleRootCause = possibleRootCauseService.getPossibleRootCauseById(newId);
                tcasBreakingInspection.setPossibleRootCause(newPossibleRootCause);
            } else {
                tcasBreakingInspection.setPossibleRootCause(null);
            }
        } else if (possibleRootCause != null && possibleRootCause.getId() == null && StringUtils.isEmpty(possibleRootCause.getName())) {
            tcasBreakingInspection.setPossibleRootCause(null);
        }

        RootCauseSubCategory rootCauseSubCategory = tcasBreakingInspection.getRootCauseSubCategory();
        if (rootCauseSubCategory != null && rootCauseSubCategory.getId() == null && !StringUtils.isEmpty(rootCauseSubCategory.getName())){
            int newId = rootCauseSubCategoryService.postRootCauseSubCategory(rootCauseSubCategory);
            if (newId > 0) {
                RootCauseSubCategory newRootCauseSubCategory = rootCauseSubCategoryService.getRootCauseSubCategoryById(newId);
                tcasBreakingInspection.setRootCauseSubCategory(newRootCauseSubCategory);
            } else {
                tcasBreakingInspection.setRootCauseSubCategory(null);
            }
        } else if (rootCauseSubCategory != null && rootCauseSubCategory.getId() == null && StringUtils.isEmpty(rootCauseSubCategory.getName())) {
            tcasBreakingInspection.setRootCauseSubCategory(null);
        }

        PossibleIssue possibleIssue = tcasBreakingInspection.getPossibleIssue();
        if (possibleIssue != null && possibleIssue.getId() == null && !StringUtils.isEmpty(possibleIssue.getName())){
            int newId = possibleIssueService.postPossibleIssue(possibleIssue);
            if (newId > 0) {
                PossibleIssue newPossibleIssue = possibleIssueService.getPossibleIssueById(newId);
                tcasBreakingInspection.setPossibleIssue(newPossibleIssue);
            } else {
                tcasBreakingInspection.setPossibleIssue(null);
            }
        } else if (possibleIssue != null && possibleIssue.getId() == null && StringUtils.isEmpty(possibleIssue.getName())) {
            tcasBreakingInspection.setPossibleIssue(null);
        }

        //tcasBreakingInspection.setCriticalityLevel(0); // hardcoded low

        TcasBreakingInspection newTcasBreakingInspection = tcasBreakingInspectionRepo.save(tcasBreakingInspection);

        try {
            if (tcasBreakingInspection.getId() != null) {
                {

                    TcasBreakingInspectionStatus tcasBreakingInspectionStatus = new TcasBreakingInspectionStatus();
                    tcasBreakingInspectionStatus.setTcasBreakingInspection(newTcasBreakingInspection);
                    tcasBreakingInspectionStatus.setRemarks("");
                    tcasBreakingInspectionStatus.setStatus("Incident Reported");
                    tcasBreakingInspectionStatus.setUser(newTcasBreakingInspection.getUser());
                    tcasBreakingInspectionStatus.setProjectType(new ProjectType(1, ""));

                    try {
                        tcasBreakingInspectionStatus.setCreatedDateTime(tcasBreakingInspection.getTripDate().atStartOfDay());
                    } catch (Exception e) {
                        logger.error("Track Date Error Open");
                    }

                    tcasBreakingInspectionStatusService.addTcasBreakingInspectionStatus(tcasBreakingInspectionStatus, fileList);
                }

                if (tcasBreakingInspection.getStatus().equalsIgnoreCase("Close")) {
                    TcasBreakingInspectionStatus tcasBreakingInspectionStatus = new TcasBreakingInspectionStatus();
                    tcasBreakingInspectionStatus.setTcasBreakingInspection(newTcasBreakingInspection);
                    tcasBreakingInspectionStatus.setRemarks("");
                    tcasBreakingInspectionStatus.setStatus("Closed");
                    tcasBreakingInspectionStatus.setUser(newTcasBreakingInspection.getUser());
                    tcasBreakingInspectionStatus.setProjectType(new ProjectType(1, ""));

                    try {
                        tcasBreakingInspectionStatus.setCreatedDateTime(tcasBreakingInspection.getTripDate().atStartOfDay());
                    } catch (Exception e) {
                        logger.error("Track Date Error Close");
                    }

                    tcasBreakingInspectionStatusService.addIncidentStatus(tcasBreakingInspectionStatus);
                }

            }

        } catch (Exception e) {
            logger.error("Error Occurred", e);
        }

        return newTcasBreakingInspection.getId();
    }

    @Override
    public int importByExcelSheet(MultipartFile excelSheet, Long userId) throws Exception {

        int rowsAdded = 0;
        //header indexes
        int tripDateIndex = -1;
        int stationIndex = -1;
        int issueTimeIndex = -1;
        int locoIdIndex = -1;
        int trainNoIndex = -1;
        int briefDescriptionIndex = -1;
        int issueCategoryIndex = -1;
        int possibleIssueIndex = -1;
        int rootCauseIndex = -1;
        int criticalityLevelIndex = -1;
        int ticketNoIndex = -1;
        int ticketDescIndex = -1;
        int divisionRemarksIndex = -1;
        int assignmentIndex = -1;
        int assignToIndex = -1;
        int rootCauseSubCategoryIndex = -1;

        int tripNoIndex = -1;
        int divisionNoIndex = -1;
        int tripFromStnIndex = -1;
        int tripToStnIndex = -1;
        int tcasIndex = -1;
        int tcasPowerStatusIndex = -1;
        int signalCorrespondenceIndex = -1;

        int targetDateIndex = -1;
        int closureDateTimeIndex = -1;
        int statusIndex = -1;
        int oemRemarkIndex = -1;
        int closeDateIndex = -1;

        List<TcasBreakingInspection> failedDataList = new ArrayList<>();

        InputStream inputStream = excelSheet.getInputStream();
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            for (Sheet sheet : workbook) {
//                System.out.println(sheet.getSheetName());
                for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
                    Row row = sheet.getRow(i);
                    if (i == 0) {
                        for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
                            Cell cell = row.getCell(j);
                            if (cell != null) {
                                // Process each cell
                                System.out.print(cell.toString() + "\t");
                                if (cell.toString().equalsIgnoreCase("Trip No")) {
                                    tripNoIndex = j;
                                } else if (cell.toString().equalsIgnoreCase("Division")) {
                                    divisionNoIndex = j;
                                } else if (cell.toString().equalsIgnoreCase("Trip Date")) {
                                    tripDateIndex = j;
                                } else if (cell.toString().equalsIgnoreCase("Train No")) {
                                    trainNoIndex = j;
                                } else if (cell.toString().equalsIgnoreCase("Loco No")) {
                                    locoIdIndex = j;
                                } else if (cell.toString().equalsIgnoreCase("Station")) {
                                    stationIndex = j;
                                } else if (cell.toString().equalsIgnoreCase("Issue Time")) {
                                    issueTimeIndex = j;
                                } else if (cell.toString().equalsIgnoreCase("Brief Description")) {
                                    briefDescriptionIndex = j;
                                } else if (cell.toString().equalsIgnoreCase("Kavach/Non-Kavach")) {
                                    tcasIndex = j;
                                } else if (cell.toString().equalsIgnoreCase("Signal Non - Correspondance")) {
                                    signalCorrespondenceIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("Category")) {
                                    issueCategoryIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("Sub Category")) {
                                    possibleIssueIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("OEM Analysis")) {
                                    oemRemarkIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("Failure Category")) {
                                    rootCauseIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("Failure Sub Category")) {
                                    rootCauseSubCategoryIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("Criticality Level")) {
                                    criticalityLevelIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("Ticket No")) {
                                    ticketNoIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("Ticket Description")) {
                                    ticketDescIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("Division Remarks")) {
                                    divisionRemarksIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("Assignment")) {
                                    assignmentIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("Assigned To")) {
                                    assignToIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("Target Date")) {
                                    targetDateIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("Status")) {
                                    statusIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("Closure Date Time")) {
                                    closureDateTimeIndex = j;
                                }
                            }
                        }

                    } else {

                        TcasBreakingInspection newTcasBreakingInspection = new TcasBreakingInspection();
                        User user = new User();
                        user.setId(userId);
                        newTcasBreakingInspection.setUser(user);
                        {
                            String tripNo = row.getCell(tripNoIndex).toString().trim();
                            newTcasBreakingInspection.setTripNo(HelpingHand.convertToInteger(tripNo));
                        }
                        {
                            String divisionCode = row.getCell(divisionNoIndex).toString().trim();
//                            logger.info("Loco No {}",locoNo);
                            Division division = divisionService.getDivisionByCode(divisionCode);
                            if (division == null) {
                                throw new RuntimeException("Division Not Found");
                            }
                            newTcasBreakingInspection.setDivision(division);
                        }
                        LocalDate tripDate = null;
                        {
                            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yy");

                            try {
                                tripDate = LocalDate.parse(row.getCell(tripDateIndex).toString(), dateFormatter);
                                newTcasBreakingInspection.setTripDate(tripDate);
//                                logger.info("Date {}",tripDate);
                            } catch (DateTimeParseException e) {
                                // Handle parsing exception
                                try {
                                    dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yy");
                                    tripDate = LocalDate.parse(row.getCell(tripDateIndex).toString(), dateFormatter);
                                    newTcasBreakingInspection.setTripDate(tripDate);
//                                logger.info("Date {}",tripDate);
                                } catch (DateTimeParseException e1) {
                                    // Handle parsing exception
                                    try {
                                        dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                                        tripDate = LocalDate.parse(row.getCell(tripDateIndex).toString(), dateFormatter);
                                        newTcasBreakingInspection.setTripDate(tripDate);
//                                logger.info("Date {}",tripDate);
                                    } catch (DateTimeParseException e2) {
                                        // Handle parsing exception
                                        try {
                                            dateFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
                                            tripDate = LocalDate.parse(row.getCell(tripDateIndex).toString(), dateFormatter);
                                            newTcasBreakingInspection.setTripDate(tripDate);
//                                logger.info("Date {}",tripDate);
                                        } catch (DateTimeParseException e3) {
                                            // Handle parsing exception
                                            newTcasBreakingInspection.setTripDate(null);
                                            logger.error("Trip Date Error ",e3);
                                        }
                                    }
                                }
                            }
                        }
                        {
                            String locoNo = row.getCell(locoIdIndex).toString();
//                            logger.info("Loco No {}",locoNo);
                            Loco loco = locoService.findByLocoNo(HelpingHand.convertToInt(locoNo));
                            if (loco == null) {
                                throw new RuntimeException("Loco Not Found");
                            }
                            newTcasBreakingInspection.setLoco(loco);

                        }
                        {
                            String trainNo = row.getCell(trainNoIndex).toString();
//                            logger.info("Train No {}",trainNo);
                            newTcasBreakingInspection.setTrainNo(HelpingHand.convertToInt(trainNo));
                        }
                        {
                            String stationString = row.getCell(stationIndex).toString();
                            logger.info("Station {}",stationString);
                            Station station = stationService.getStationByCode(stationString);
                            String issueCatString = row.getCell(issueCategoryIndex).toString();
//                        logger.info("Issue Category {}",issueCatString);
                            IssueCategory issueCategory = issueCategoryService.getIssueCategoryByName(issueCatString);
                            if (issueCategory == null) {
                                throw new RuntimeException("Issue Category Not Found");
                            }
                            if (station == null && !issueCategory.getName().equalsIgnoreCase("No Issue")) {
                                throw new RuntimeException("Station Not Found");
                            }
                            newTcasBreakingInspection.setFaultyStation(station);
                        }
                        {
                            logger.info("Desc Cell {}",row.getCell(briefDescriptionIndex));
                            logger.info("Trip Date Cell {}",row.getCell(tripDateIndex));
                            logger.info("Issue Time Cell {}",row.getCell(issueTimeIndex));
                            String incidentTime = row.getCell(issueTimeIndex).toString().trim();
                            if (incidentTime.isEmpty() || tripDate == null) {
//                                throw new RuntimeException();
                                logger.info("Issue Time is not Valid");
                            } else {
                                // Parse the time from the stringDataFormatter dataFormatter = new DataFormatter();

                                if (row.getCell(issueTimeIndex) != null) {
                                    incidentTime = switch (row.getCell(issueTimeIndex).getCellType()) {
                                        case NUMERIC ->
                                            // Extract the time part if stored as a numeric date/time
                                                row.getCell(issueTimeIndex).getLocalDateTimeCellValue()
                                                        .toLocalTime().toString();
                                        case STRING ->
                                            // Directly read the formatted value for string cells
                                                row.getCell(issueTimeIndex).getStringCellValue().trim();
                                        default -> {
                                            // Use DataFormatter as fallback
                                            DataFormatter dataFormatter = new DataFormatter();
                                            yield dataFormatter.formatCellValue(row.getCell(issueTimeIndex)).trim();
                                        }
                                    };
                                } else {
                                    incidentTime = null; // Handle null cell gracefully
                                }
                                LocalTime time = LocalTime.parse(incidentTime);
                                LocalDateTime incidentDateTime = LocalDateTime.of(tripDate, time);
                                newTcasBreakingInspection.setIncidentDateTime(incidentDateTime);
                            }

                        }
                        {
                            String briefDescString = row.getCell(briefDescriptionIndex).toString();
//                            logger.info("Brief Desc {}",briefDescString);
                            newTcasBreakingInspection.setBriefDescription(briefDescString);
                        }
                        {
                            String tcasString = row.getCell(tcasIndex).toString().trim();
                            Tcas tcas = tcasService.getTcasByName(tcasString);
                            newTcasBreakingInspection.setTcas(tcas);
                        }
                        {
                            String signalCorrespondence = row.getCell(signalCorrespondenceIndex).toString().trim();
                            newTcasBreakingInspection.setSignalCorrespondence(signalCorrespondence);
                        }
                        IssueCategory issueCategory;
                        {
                            String issueCatString = row.getCell(issueCategoryIndex).toString();
//                        logger.info("Issue Category {}",issueCatString);
                            issueCategory = issueCategoryService.getIssueCategoryByName(issueCatString);
                            if (issueCategory == null) {
                                throw new RuntimeException("Issue Category Not Found");
                            }
                            newTcasBreakingInspection.setIssueCategory(issueCategory);
                        }
                        {
                            String possibleIssueString = row.getCell(possibleIssueIndex).toString();
                            if (!possibleIssueString.isEmpty()) {
//                            logger.info("Possible Issue {}",possibleIssueString);
                                PossibleIssue possibleIssue = possibleIssueService.getPossibleIssueByNameAndIssueCategoryId(possibleIssueString,issueCategory.getId());
                                if (possibleIssue == null) {
                                    logger.info("Possible Issue is not Valid");
                                }
                                newTcasBreakingInspection.setPossibleIssue(possibleIssue);

                            }
                        }

                        {
                            String rootCauseDescString = row.getCell(oemRemarkIndex).toString();
//                            logger.info("Root Cause Description {}",rootCauseDescString);
                            newTcasBreakingInspection.setRootCauseDescription(rootCauseDescString);
                        }
                        PossibleRootCause possibleRootCause = null;
                        {
                            String rootCauseString = row.getCell(rootCauseIndex).toString();
//                            logger.info("Root Cause {}",rootCauseString);
                            possibleRootCause = possibleRootCauseService.getPossibleRootCauseByName(rootCauseString);
                            if (possibleRootCause == null) {
                                logger.info("Possible Root Cause is not Valid");
                            }
                            newTcasBreakingInspection.setPossibleRootCause(possibleRootCause);
                        }

                       if (possibleRootCause != null) {
                            String rootCauseSubCategoryString = row.getCell(rootCauseSubCategoryIndex).toString();
//                            logger.info("Root Cause {}",rootCauseString);
                            RootCauseSubCategory rootCauseSubCategory = rootCauseSubCategoryService.findByNameAndPossibleRootCauseId(rootCauseSubCategoryString, possibleRootCause.getId());
                            if (rootCauseSubCategory == null) {
                                logger.info("Root Cause Sub Category is not Valid");
                            }
                            newTcasBreakingInspection.setRootCauseSubCategory(rootCauseSubCategory);
                       }

                       // Ticket Details
                        {
                            IncidentTicket incidentTicket = new IncidentTicket();
                            String ticketNo = row.getCell(ticketNoIndex).toString().trim();

                            if (!ticketNo.isEmpty()) {
                                incidentTicket.setTicketNo(ticketNo);
                                IncidentTicket existingIncidentTicket = incidentTicketService.getIncidentTicketByNo(ticketNo);

                                if (existingIncidentTicket != null && existingIncidentTicket.getId() != null) {
                                    incidentTicket.setId(existingIncidentTicket.getId());
                                    incidentTicket.setDivision(existingIncidentTicket.getDivision());
                                    incidentTicket.setTripDate(existingIncidentTicket.getTripDate());
                                    incidentTicket.setUser(existingIncidentTicket.getUser());
                                }
                                {
                                    String ticketDesc = row.getCell(ticketDescIndex).toString().trim();
                                    incidentTicket.setDescription(ticketDesc);
                                }
                                {
                                    String divisionRemarks = row.getCell(divisionRemarksIndex).toString().trim();
                                    incidentTicket.setRemark(divisionRemarks);
                                    newTcasBreakingInspection.setRemark(divisionRemarks);
                                }
                                {
                                    String rootCauseDescString = row.getCell(oemRemarkIndex).toString();
                                    incidentTicket.setOemRemark(rootCauseDescString);
                                }
                                {
                                    String assignment = row.getCell(assignmentIndex).toString();
                                    incidentTicket.setAssignTo(assignment);
                                }
                                {
                                    String firmName = row.getCell(assignToIndex).toString().trim();
                                    if (!firmName.isEmpty()) {
                                        Firm firm = firmService.getFirmByName(firmName);
                                        Set<Firm> assignedFirms = new HashSet<>();
                                        assignedFirms.add(firm);
                                        incidentTicket.setAssignedFirms(assignedFirms);
                                    }

                                }
                                {
                                    String status = row.getCell(statusIndex).toString().trim();
                                    Boolean blnStatus;

                                    if ("open".equalsIgnoreCase(status)) {
                                        blnStatus = true;
                                    } else if ("close".equalsIgnoreCase(status)) {
                                        blnStatus = false;
                                    } else {
                                        blnStatus = null;
                                    }
                                    incidentTicket.setStatus(blnStatus);

                                }
                                {
                                    if (row.getCell(closureDateTimeIndex) != null) {
                                        if (row.getCell(closureDateTimeIndex).getCellType() == CellType.NUMERIC) {
                                            // Handle numeric date-time cells properly
                                            LocalDateTime closureDateTime = row.getCell(closureDateTimeIndex)
                                                    .getLocalDateTimeCellValue(); // Directly extract LocalDateTime
                                            incidentTicket.setClosureDateTime(closureDateTime);
                                        } else {
                                            // Fallback for textual cells
                                            DataFormatter dataFormatter = new DataFormatter();
                                            String closureDateTimeString = dataFormatter.formatCellValue(row.getCell(closureDateTimeIndex)).trim();

                                            if (!closureDateTimeString.isEmpty()) {
                                                // Define the formatter to match input format
                                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

                                                // Parse and set the value
                                                LocalDateTime closureDateTime = LocalDateTime.parse(closureDateTimeString, formatter);
                                                incidentTicket.setClosureDateTime(closureDateTime);
                                            }
                                        }
                                    }
                                }

                                newTcasBreakingInspection.setIncidentTicket(incidentTicket);
                            }

                        }

//                        logger.info("Incident {}",newTcasBreakingInspection);
//                        Long insertId = addTcasBreakingInspection(newTcasBreakingInspection, new ArrayList<>());
                        Long insertId = addIncident(newTcasBreakingInspection);

                        if (insertId > 0) {
                            rowsAdded++;
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Exception ",e);
        }

        return rowsAdded;
    }

    @Override
    public List<SingleIncidentAnalysisChartData> getCountsByIssueCategoryForMonthAndYear(int month, int year) {

        List<SingleIncidentAnalysisChartData> singleIncidentAnalysisChartDataList = new ArrayList<>();
        try {
            // Overall Summary Added Below
            {

                SingleIncidentAnalysisChartData singleIncidentAnalysisChartData = new SingleIncidentAnalysisChartData();
                List<PieChartData> chartDataList = new ArrayList<>();
                int totalCount = 0;
                {
                    // adding Issue Category wise data below
                    PieChartData pieChartData = new PieChartData();
                    List<StringAndCount> issueCategoryObjAndCountList = new ArrayList<>();
                    List<Object[]> counts = tcasBreakingInspectionRepo.findCountsByIssueCategoryForMonthAndYear(month, year);
                    for (Object[] result : counts) {
                        IssueCategory issueCategory = (IssueCategory) result[0];
                        Long count = (Long) result[1];
                        totalCount += count;
                        StringAndCount issueCategoryObjAndCount = new StringAndCount(issueCategory.getName(), Math.toIntExact(count), "");
                        if (issueCategory.getName().equalsIgnoreCase("Mode Change")) {
                            issueCategoryObjAndCount.setColorCode("#f0ad4e");
                        } else if (issueCategory.getName().equalsIgnoreCase("Undesirable Braking")) {
                            issueCategoryObjAndCount.setColorCode("#E91E63");
                        } else {
                            issueCategoryObjAndCount.setColorCode(HelpingHand.getRandomColor());
                        }
                        issueCategoryObjAndCountList.add(issueCategoryObjAndCount);
                    }
                    pieChartData.setName("IssueCategory");
                    pieChartData.setChartDataList(issueCategoryObjAndCountList);
                    chartDataList.add(pieChartData);

                    singleIncidentAnalysisChartData.setTotal(totalCount);
                    // adding Issue Category wise data below
                }

//                {
//                    // adding TCAS wise data
//                    PieChartData pieChartData = new PieChartData();
//                    List<StringAndCount> tcasObjAndCountList = new ArrayList<>();
//                    List<Object[]> counts = tcasBreakingInspectionRepo.findCountsByTcasForMonthAndYear(month, year);
//                    for (Object[] result : counts) {
//                        Tcas tcas = (Tcas) result[0];
//                        Long count = (Long) result[1];
//                        StringAndCount tcasObjAndCount = new StringAndCount(tcas.getName(), Math.toIntExact(count), "");
//                        if (tcas.getName().equalsIgnoreCase("TCAS")) {
//                            tcasObjAndCount.setColorCode("#2196F3");
//                        } else if (tcas.getName().equalsIgnoreCase("Non - TCAS")) {
//                            tcasObjAndCount.setColorCode("#5cb85c");
//                        } else {
//                            tcasObjAndCount.setColorCode(HelpingHand.getRandomColor());
//                        }
//                        tcasObjAndCountList.add(tcasObjAndCount);
//                    }
//
//                    pieChartData.setName("Tcas");
//                    pieChartData.setChartDataList(tcasObjAndCountList);
//                    chartDataList.add(pieChartData);
//                }

                {
                    // adding Possible Issue wise data below
                    PieChartData pieChartData = new PieChartData();
                    List<StringAndCount> stringAndCountList = new ArrayList<>();
                    List<Object[]> counts = tcasBreakingInspectionRepo.findCountsByPossibleIssueForMonthAndYear(month, year);
                    int totalPossibleIssueWiseCount = 0;
                    for (Object[] result : counts) {
                        PossibleIssue possibleIssue = (PossibleIssue) result[0];
                        Long count = (Long) result[1];
                        totalPossibleIssueWiseCount += count;
                        StringAndCount stringAndCount = new StringAndCount(possibleIssue.getName(), Math.toIntExact(count), HelpingHand.getRandomColor());
                        stringAndCountList.add(stringAndCount);
                    }
                    if (totalCount - totalPossibleIssueWiseCount > 0) {
                        StringAndCount stringAndCount = new StringAndCount("Unknown", Math.toIntExact(totalCount - totalPossibleIssueWiseCount), HelpingHand.getRandomColor());
                        stringAndCountList.add(stringAndCount);
                    }
                    pieChartData.setName("PossibleIssue");
                    pieChartData.setChartDataList(stringAndCountList);
                    chartDataList.add(pieChartData);
                    // adding Possible Issue wise data above
                }
                {
                    // adding Possible Root Cause wise data below
                    PieChartData pieChartData = new PieChartData();
                    List<StringAndCount> stringAndCountList = new ArrayList<>();
                    List<Object[]> counts = tcasBreakingInspectionRepo.findCountsByPossibleRootCauseForMonthAndYear(month, year);
                    int totalPossibleRootCauseWiseCount = 0;
                    for (Object[] result : counts) {
                        PossibleRootCause possibleRootCause = (PossibleRootCause) result[0];
                        Long count = (Long) result[1];
                        totalPossibleRootCauseWiseCount += count;
                        StringAndCount stringAndCount = new StringAndCount(possibleRootCause.getName(), Math.toIntExact(count), HelpingHand.getRandomColor());
                        stringAndCountList.add(stringAndCount);
                    }
                    if (totalCount - totalPossibleRootCauseWiseCount > 0) {
                        StringAndCount stringAndCount = new StringAndCount("Unknown", Math.toIntExact(totalCount - totalPossibleRootCauseWiseCount), HelpingHand.getRandomColor());
                        stringAndCountList.add(stringAndCount);
                    }
                    pieChartData.setName("PossibleRootCause");
                    pieChartData.setChartDataList(stringAndCountList);
                    chartDataList.add(pieChartData);
                    // adding Possible Root Cause wise data above
                }

                singleIncidentAnalysisChartData.setChartTitle("Overall Summary");
                singleIncidentAnalysisChartData.setChartDataList(chartDataList);
                singleIncidentAnalysisChartDataList.add(singleIncidentAnalysisChartData);

                // Overall Summary Added Above
            }

            // Issue Category Wise Summary Added Below

            List<IssueCategory> issueCategoryList = issueCategoryService.getAllIssueCategory();

            for (IssueCategory issueCategory : issueCategoryList) {
                int issueCategoryId = issueCategory.getId();
                int totalCount = 0;
                List<Object[]> countResult = tcasBreakingInspectionRepo.findAllInspectionsCountByMonthYearIssueCategoryIdAndFilters(month, year, issueCategoryId,0L,0L);

                for (Object[] output : countResult) {
                    Long count = (Long) output[1]; // Assuming count is of type Long
                    totalCount = Math.toIntExact(count);
                }

                SingleIncidentAnalysisChartData singleIncidentAnalysisChartData = new SingleIncidentAnalysisChartData();
                List<PieChartData> chartDataList = new ArrayList<>();
                singleIncidentAnalysisChartData.setChartTitle("Summary of " + issueCategory.getName());

                {
                    // Adding issue wise chart data below
                    List<Object[]> counts = tcasBreakingInspectionRepo.findIssuesCountsByIssueCategoryIdForMonthAndYear(month, year, issueCategoryId);
                    PieChartData pieChartData = new PieChartData();
                    List<StringAndCount> issueCatWiseDataCountList = new ArrayList<>();
                    int totalPossibleIssueWiseCount = 0;
                    for (Object[] result : counts) {
                        String possibleIssueName = (String) result[0];
                        Long inspectionCount = (Long) result[1];
                        totalPossibleIssueWiseCount += inspectionCount;
                        StringAndCount stringAndCount = new StringAndCount(possibleIssueName, Math.toIntExact(inspectionCount), HelpingHand.getRandomColor());
                        issueCatWiseDataCountList.add(stringAndCount);
                    }
                    if (totalCount - totalPossibleIssueWiseCount > 0) {
                        StringAndCount stringAndCount = new StringAndCount("Unknown", Math.toIntExact(totalCount - totalPossibleIssueWiseCount), HelpingHand.getRandomColor());
                        issueCatWiseDataCountList.add(stringAndCount);
                    }
                    pieChartData.setName("PossibleIssue");
                    pieChartData.setChartDataList(issueCatWiseDataCountList);
                    chartDataList.add(pieChartData);
                    // Adding issue wise chart data above
                }

                {
                    // Adding Root Cause Wise chart data below
                    List<Object[]> counts = tcasBreakingInspectionRepo.findRootCausesCountsByIssueCategoryIdForMonthAndYear(month, year, issueCategoryId);
                    PieChartData pieChartData = new PieChartData();
                    List<StringAndCount> rootCauseDataCountList = new ArrayList<>();
                    int totalRootCauseWiseCount = 0;
                    for (Object[] result : counts) {
                        String rootCauseName = (String) result[0];
                        Long inspectionCount = (Long) result[1];
                        totalRootCauseWiseCount += inspectionCount;
                        StringAndCount stringAndCount = new StringAndCount(rootCauseName, Math.toIntExact(inspectionCount), HelpingHand.getRandomColor());

                        rootCauseDataCountList.add(stringAndCount);
                    }
                    if (totalCount - totalRootCauseWiseCount > 0) {
                        StringAndCount stringAndCount = new StringAndCount("Unknown", Math.toIntExact(totalCount - totalRootCauseWiseCount), HelpingHand.getRandomColor());
                        rootCauseDataCountList.add(stringAndCount);
                    }
                    pieChartData.setName("RootCause");
                    pieChartData.setChartDataList(rootCauseDataCountList);
                    chartDataList.add(pieChartData);
                    // Adding Root Cause Wise chart data above
                }

//                {
//                    // Adding Tcaswise chart data below
//                    List<Object[]> counts = tcasBreakingInspectionRepo.findTcasCountsByIssueCategoryIdForMonthAndYear(month, year, issueCategoryId);
//                    List<StringAndCount> tcasDataCountList = new ArrayList<>();
//                    PieChartData pieChartData = new PieChartData();
//                    int totalTcasWiseCount = 0;
//                    for (Object[] result : counts) {
//                        Tcas tcas = (Tcas) result[0];
//                        Long inspectionCount = (Long) result[1];
//                        totalTcasWiseCount += inspectionCount;
//                        StringAndCount tcasObjAndCount = new StringAndCount(tcas.getName(), Math.toIntExact(inspectionCount), "");
//                        if (tcas.getName().equalsIgnoreCase("TCAS")) {
//                            tcasObjAndCount.setColorCode("#2196F3");
//                        } else if (tcas.getName().equalsIgnoreCase("Non - TCAS")) {
//                            tcasObjAndCount.setColorCode("#5cb85c");
//                        } else {
//                            tcasObjAndCount.setColorCode(HelpingHand.getRandomColor());
//                        }
//                        tcasDataCountList.add(tcasObjAndCount);
//                    }
//                    if (totalCount - totalTcasWiseCount > 0) {
//                        StringAndCount stringAndCount = new StringAndCount("Unknown", Math.toIntExact(totalCount - totalTcasWiseCount), HelpingHand.getRandomColor());
//                        tcasDataCountList.add(stringAndCount);
//                    }
//                    pieChartData.setName("Tcas");
//                    pieChartData.setChartDataList(tcasDataCountList);
//                    chartDataList.add(pieChartData);
//                    // Adding Tcaswise chart data above
//                }

                // Adding all Data
                singleIncidentAnalysisChartData.setTotal(totalCount);
                singleIncidentAnalysisChartData.setChartDataList(chartDataList);
                singleIncidentAnalysisChartDataList.add(singleIncidentAnalysisChartData);

            }

            // Issue Category Wise Summary Added Above

        } catch (Exception e) {
            e.printStackTrace();
        }

        return singleIncidentAnalysisChartDataList;
    }

    @Override
    public void closeInspectionIssue(TcasBreakingInspectionStatus tcasBreakingInspectionStatus) throws Exception {

        tcasBreakingInspectionStatus.setStatus("Issue Closed");
        TcasBreakingInspectionStatus inspectionStatus = tcasBreakingInspectionStatusService.postTcasBreakingInspectionStatus(tcasBreakingInspectionStatus);
        TcasBreakingInspection tcasBreakingInspection = getTcasBreakingInspectionById(inspectionStatus.getTcasBreakingInspection().getId());
        tcasBreakingInspection.setStatus("Close");
        updateTcasBreakingInspection(tcasBreakingInspection);

        List<User> userListToNotify = userService.getAllUsersRelatedToInspection(tcasBreakingInspection);

        String title = inspectionStatus.getStatus();
        String msg = "Issue closed for the incident of loco " + tcasBreakingInspection.getLoco().getLocoNo() + " ";

        notificationService.sendNotificationToUsersAfterInspectionStatusAdded(userListToNotify, tcasBreakingInspection, title, msg);

        for (User toUser : userListToNotify) {
            String mailBody = "Dear "+ toUser.getName() +",\n\n" +
                    msg +
                    "\n\n" +
                    "Best Regards,\n\n" +
                    "Kavach Administration" + "\n";
            String mailSubject = inspectionStatus.getStatus();
            List<String> toList = Collections.singletonList(toUser.getEmail());
            emailService.mailService(mailSubject,mailBody,toList);

            try {
                otpService.sendCloseIncidentSms(toUser.getName(), tcasBreakingInspection.getFaultyStation().getFirm().getName() , tcasBreakingInspection.getLoco().getLocoNo(), tcasBreakingInspection.getIncidentTag(), tcasBreakingInspection.getFaultyStation().getCode(), toUser.getContact())
                        .doOnNext(response -> {
                            System.out.println("Response Code: " + response.getResponseCode());
                            System.out.println("Response Message: " + response.getResponseMessage());
                            System.out.println("Transaction ID: " + response.getTxId());
                            System.out.println("SMS Encoding: " + response.getSmsEncoding());
                            System.out.println("SMS Length: " + response.getSmsLength());
                            System.out.println("Balance Used: " + response.getBalanceUsed());
                            System.out.println("Total Mobile Number Submitted: " + response.getTotalMobileNumberSubmitted());
                        })
                        .block();
            } catch (Exception e){
                logger.error("Exception : ", e);
            }
        }

    }

    @Override
    public YearlyReportExcelResponse getYearlyReportExcelResponse(int divisionId) {
        YearlyReportExcelResponse yearlyReportExcelResponse = new YearlyReportExcelResponse();

        List<MonthYear> monthYearList = new ArrayList<>();
        Calendar cal = Calendar.getInstance();

        // Get the current month and year
        int currentMonth = cal.get(Calendar.MONTH) + 1; // Month is zero-based, so adding 1
        int currentYear = cal.get(Calendar.YEAR);

        // Add the current month and year to the hashmap
        monthYearList.add(new MonthYear(currentMonth, currentYear));

        // Calculate and add the previous 11 months
        for (int i = 1; i < 12; i++) {
            cal.add(Calendar.MONTH, -1); // Move to the previous month
            int previousMonth = cal.get(Calendar.MONTH) + 1; // Month is zero-based, so adding 1
            int previousYear = cal.get(Calendar.YEAR);
            monthYearList.add(new MonthYear(previousMonth, previousYear));
        }

        //Initializing Headers below
        String[] headers;
        List<String> headerList = new ArrayList<>();
        headerList.add("Month");
        headerList.add("Total Inspections");
        List<IssueCategory> issueCategoryList = issueCategoryService.getAllIssueCategory();
        List<Tcas> tcasList = tcasService.getAllTcas();
        for (IssueCategory issueCategory : issueCategoryList) {
            headerList.add(issueCategory.getName());
            headerList.add(issueCategory.getName() + " %");
            for (Tcas tcas : tcasList) {
                headerList.add(tcas.getName());
                headerList.add(tcas.getName() + " %");
            }
        }

        // Convert List<String> to String[]
        headers = headerList.toArray(new String[0]);
        yearlyReportExcelResponse.setHeaders(headers);
        //Initializing Headers above

        //Initializing remaining data
        List<YearlyReportRowData> yearlyReportRowDataList = new ArrayList<>();


        // Loop for each previous 12 months
        for (MonthYear monthYear : monthYearList) {
            YearlyReportRowData yearlyReportRowData = new YearlyReportRowData();
            int month = monthYear.getMonth();
            int year = monthYear.getYear();

            String monthName = HelpingHand.getMonthName(month);
            yearlyReportRowData.setMonth(monthName + " - " + year);

            int totalCount = 0;
//            System.out.println("Month: " + monthYear.getMonth() + ", Year: " + monthYear.getYear());
            if (divisionId == 0) {
                totalCount = tcasBreakingInspectionRepo.getTotalInspectionsByMonthAndYear(month, year);
            } else {
                totalCount = tcasBreakingInspectionRepo.getTotalInspectionsByMonthAndYearAndDivisionId(month, year, divisionId);
            }
            yearlyReportRowData.setTotalInspections(String.valueOf(totalCount));

            List<IssuewiseYearlyData> issuewiseYearlyDataList = new ArrayList<>();
            //Setting IssueCategory Wise data below
            //Issue Category Wise Iterations
            for (IssueCategory issueCategory : issueCategoryList) {

                IssuewiseYearlyData issuewiseYearlyData = new IssuewiseYearlyData();

                int issueCategoryCount = 0;
                float issueCategoryPercent;
                List<Object[]> countObjArr = new ArrayList<>();
                ;
                if (divisionId > 0) {
                    countObjArr = tcasBreakingInspectionRepo.findAllInspectionsCountByMonthYearAndIssueCategoryIdAndDivisionId(month, year, issueCategory.getId(), divisionId);
                } else {
                    countObjArr = tcasBreakingInspectionRepo.findAllInspectionsCountByMonthYearIssueCategoryIdAndFilters(month, year, issueCategory.getId(),0L,0L);
                }
                for (Object[] output : countObjArr) {
                    Long count = (Long) output[1]; // Assuming count is of type Long
                    issueCategoryCount = Math.toIntExact(count);
                    System.out.println(issueCategoryCount);
                }
                if (totalCount != 0) {
                    issueCategoryPercent = (issueCategoryCount / (float) totalCount) * 100;
                } else {
                    issueCategoryPercent = 0;
                }

                issuewiseYearlyData.setIssueCategory(String.valueOf(issueCategoryCount));
                issuewiseYearlyData.setIssueCategoryPercent(String.format("%.2f", issueCategoryPercent));

                List<TcaswiseYearlyData> tcaswiseYearlyDataList = new ArrayList<>();

                //Tcas Wise Iterations
                for (Tcas tcas : tcasList) {
                    List<Object[]> countTcasObjArr = new ArrayList<>();
                    if (divisionId > 0) {
//                        countTcasObjArr = tcasBreakingInspectionRepo.findAllInspectionsCountByMonthYearAndIssueCategoryIdAndTcasIdAndDivisionId(month, year, issueCategory.getId(), tcas.getId(), divisionId);
                    } else {
//                        countTcasObjArr = tcasBreakingInspectionRepo.findAllInspectionsCountByMonthYearAndIssueCategoryIdAndTcasId(month, year, issueCategory.getId(), tcas.getId());
                    }
                    int totalTcasCount = 0;
                    for (Object[] objects : countTcasObjArr) {
                        totalTcasCount += (Long) objects[1];
                    }
                    float tcasCountPercent;
                    if (issueCategoryCount != 0) {
                        tcasCountPercent = (totalTcasCount / (float) issueCategoryCount) * 100;
                    } else {
                        tcasCountPercent = 0;
                    }
                    TcaswiseYearlyData tcaswiseYearlyData = new TcaswiseYearlyData();
                    tcaswiseYearlyData.setTcas(String.valueOf(totalTcasCount));
                    tcaswiseYearlyData.setTcasPercent(String.format("%.2f", tcasCountPercent));

                    tcaswiseYearlyDataList.add(tcaswiseYearlyData);
                }

                issuewiseYearlyData.setTcaswiseYearlyDataList(tcaswiseYearlyDataList);
                issuewiseYearlyDataList.add(issuewiseYearlyData);
            }
            //Setting IssueCategory Wise data above
            yearlyReportRowData.setIssuewiseYearlyDataList(issuewiseYearlyDataList);
            yearlyReportRowDataList.add(yearlyReportRowData);
        }

        yearlyReportExcelResponse.setYearlyReportRowDataList(yearlyReportRowDataList);

        return yearlyReportExcelResponse;
    }

    @Override
    public void addInspectionStatusWithActions(TcasBreakingInspectionStatus tcasBreakingInspectionStatus, boolean preventiveAction, boolean correctiveAction) throws Exception {

        tcasBreakingInspectionStatus.setStatus(preventiveAction ? "Preventive Action Added" :
                correctiveAction ? "Corrective Action Added" :
                        "Remarks Added");

        TcasBreakingInspectionStatus inspectionStatus = tcasBreakingInspectionStatusService.postTcasBreakingInspectionStatus(tcasBreakingInspectionStatus);
        TcasBreakingInspection tcasBreakingInspection = getTcasBreakingInspectionById(inspectionStatus.getTcasBreakingInspection().getId());

        updateTcasBreakingInspection(tcasBreakingInspection);

        List<User> userListToNotify = userService.getAllUsersRelatedToInspection(tcasBreakingInspection);

        String title = inspectionStatus.getStatus();
        String msg = inspectionStatus.getStatus() + " for the inspection of loco " + tcasBreakingInspection.getLoco().getLocoNo() + " by " + tcasBreakingInspection.getUser().getName() + " ( " + tcasBreakingInspection.getUser().getDesignation().getName() + " )";

        notificationService.sendNotificationToUsersAfterInspectionStatusAdded(userListToNotify, tcasBreakingInspection, title, msg);

        for (User toUser : userListToNotify) {
            String mailBody = "Dear "+ toUser.getName() +",\n\n" +
                    msg +
                    "\n\n" +
                    "Best Regards,\n\n" +
                    "Kavach Administration" + "\n";
            String mailSubject = inspectionStatus.getStatus();
            List<String> toList = Collections.singletonList(toUser.getEmail());
            emailService.mailService(mailSubject,mailBody,toList);
        }
    }

    @Override
    public List<TcasBreakingInspection> getPendingInspections() {
        return findAndSetLastAssignedUserStatusInIncidentList(tcasBreakingInspectionRepo.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            String status = "Open";
            predicates.add(criteriaBuilder.equal(root.get("status"), status));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        }));
    }

    @Override
    public List<TcasBreakingInspection> getClosedInspections() {
        return findAndSetLastAssignedUserStatusInIncidentList(tcasBreakingInspectionRepo.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            String status = "CLose";
            predicates.add(criteriaBuilder.equal(root.get("status"), status));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        }));
    }

    @Override
    public void deleteIncidentById(Long id) {
        tcasBreakingInspectionRepo.deleteById(id);
    }

}
