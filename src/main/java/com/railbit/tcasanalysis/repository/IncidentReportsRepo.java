package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.DTO.reports.CausesWiseRepeatedIncidentsReportDTO;
import com.railbit.tcasanalysis.entity.TcasBreakingInspection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface IncidentReportsRepo  extends JpaRepository<TcasBreakingInspection,Integer> {

    @Query("SELECT COUNT(t.id) FROM tcasbreakinginspection t JOIN t.issueCategory ic WHERE ic.name != 'Desirable Braking'")
    long countByIssueCategoryNotDesirable();

    //Open Incident Count
    @Query("SELECT COUNT(t.id) " +
            "FROM tcasbreakinginspection t JOIN t.issueCategory ic " +
            "WHERE ic.name != 'Desirable Braking' AND t.status = :status")
    long findCountByStatus(String status);

    //For Incidents Overview Date wise below
    @Query("SELECT t.issueCategory, COUNT(t.issueCategory) " +
            "FROM tcasbreakinginspection t " +
            "JOIN t.issueCategory ic " +
            "LEFT JOIN t.possibleRootCause rc " +
            "LEFT JOIN t.rootCauseSubCategory sc " +
            "JOIN t.division d " +
            "JOIN d.zone z " +
            "WHERE t.tripDate BETWEEN :fromDate AND :toDate " +
            "AND ic.name != 'No Issue' " +
            "AND (rc.name != 'Site Alteration' OR rc.name IS NULL) " +
            "AND (sc.name != 'ALTERATION WORK' OR sc.name IS NULL) " +
            "AND (:zoneId IS NULL OR :zoneId = 0 OR z.id = :zoneId) " +
            "AND (:divisionId IS NULL OR :divisionId = 0 OR d.id = :divisionId) " +
            "GROUP BY t.issueCategory")
    List<Object[]> findCountsByIssueCategoryWithFilters(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("zoneId") Long zoneId,
            @Param("divisionId") Long divisionId
    );


    @Query("SELECT t.possibleIssue, COUNT(t.possibleIssue) " +
            "FROM tcasbreakinginspection t " +
            "LEFT JOIN t.possibleRootCause rc " +
            "LEFT JOIN t.rootCauseSubCategory sc " +
            "JOIN t.division d " +
            "JOIN d.zone z " +
            "WHERE t.tripDate BETWEEN :fromDate AND :toDate " +
            "AND rc.name != 'Site Alteration' " +
            "AND sc.name != 'ALTERATION WORK' " +
            "AND (:zoneId IS NULL OR :zoneId = 0 OR z.id = :zoneId) " +
            "AND (:divisionId IS NULL OR :divisionId = 0 OR d.id = :divisionId) " +
            "GROUP BY t.possibleIssue")
    List<Object[]> findCountsByPossibleIssueBetweenDateAndZoneAndDivision(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("zoneId") Long zoneId,
            @Param("divisionId") Long divisionId
    );

    @Query("SELECT t.possibleRootCause, COUNT(t.possibleRootCause) " +
            "FROM tcasbreakinginspection t " +
            "JOIN t.division d " +
            "JOIN d.zone z " +
            "LEFT JOIN t.possibleRootCause rc " +
            "LEFT JOIN t.rootCauseSubCategory sc " +
            "WHERE t.tripDate BETWEEN :fromDate AND :toDate " +
            "AND (rc.name != 'Site Alteration') " +
            "AND (sc.name != 'ALTERATION WORK' OR sc.name IS NULL) " +
            "AND (:zoneId IS NULL OR :zoneId = 0 OR z.id = :zoneId) " +
            "AND (:divisionId IS NULL OR :divisionId = 0 OR d.id = :divisionId) " +
            "GROUP BY t.possibleRootCause")
    List<Object[]> findCountsByPossibleRootCauseBetweenDateAndZoneAndDivision(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("zoneId") Long zoneId,
            @Param("divisionId") Long divisionId
    );

    @Query("SELECT sc, COUNT(t.possibleRootCause) " +
            "FROM tcasbreakinginspection t " +
            "JOIN t.division d " +
            "JOIN d.zone z " +
            "LEFT JOIN t.possibleRootCause rc " +
            "LEFT JOIN t.rootCauseSubCategory sc " +
            "WHERE t.tripDate BETWEEN :fromDate AND :toDate " +
            "AND (rc.name != 'Site Alteration' OR rc.name IS NULL) " +
            "AND (sc.name != 'ALTERATION WORK') " +
            "AND (:zoneId IS NULL OR :zoneId = 0 OR z.id = :zoneId) " +
            "AND (:divisionId IS NULL OR :divisionId = 0 OR d.id = :divisionId) " +
            "GROUP BY sc")
    List<Object[]> findCountsByRootCauseSubCategoryBetweenDateAndZoneAndDivision(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("zoneId") Long zoneId,
            @Param("divisionId") Long divisionId
    );


    @Query("SELECT ic.name, COUNT(t) " +
            "FROM tcasbreakinginspection t " +
            "JOIN t.issueCategory ic " +
            "JOIN t.division d " +
            "JOIN d.zone z " +
            "LEFT JOIN t.possibleRootCause rc " +
            "LEFT JOIN t.rootCauseSubCategory sc " +
            "WHERE t.tripDate BETWEEN :fromDate AND :toDate " +
            "AND ic.id = :issueCategoryId " +
            "AND (rc.name != 'Site Alteration' OR rc.name IS NULL) " +
            "AND (sc.name != 'ALTERATION WORK' OR sc.name IS NULL) " +
            "AND (:zoneId IS NULL OR :zoneId = 0 OR z.id = :zoneId) " +
            "AND (:divisionId IS NULL OR :divisionId = 0 OR d.id = :divisionId) " +
            "GROUP BY ic.name")
    List<Object[]> findAllInspectionsCountBetweenDateAndFilters(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("issueCategoryId") int issueCategoryId,
            @Param("zoneId") Long zoneId,
            @Param("divisionId") Long divisionId
    );

    @Query("SELECT pi.name, COUNT(t) " +
            "FROM tcasbreakinginspection t " +
            "JOIN t.issueCategory ic " +
            "JOIN t.possibleIssue pi " +
            "JOIN t.division d " +
            "JOIN d.zone z " +
            "LEFT JOIN t.possibleRootCause rc " +
            "LEFT JOIN t.rootCauseSubCategory sc " +
            "WHERE t.tripDate BETWEEN :fromDate AND :toDate " +
            "AND ic.id = :issueCategoryId " +
            "AND (rc.name != 'Site Alteration' OR rc.name IS NULL) " +
            "AND (sc.name != 'ALTERATION WORK' OR sc.name IS NULL) " +
            "AND (:zoneId IS NULL OR :zoneId = 0 OR z.id = :zoneId) " +
            "AND (:divisionId IS NULL OR :divisionId = 0 OR d.id = :divisionId) " +
            "GROUP BY pi.id")
    List<Object[]> findIssuesCountsByIssueCategoryIdAndFilters(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("issueCategoryId") int issueCategoryId,
            @Param("zoneId") Long zoneId,
            @Param("divisionId") Long divisionId
    );

    @Query("SELECT pi.name, COUNT(t) " +
            "FROM tcasbreakinginspection t " +
            "JOIN t.issueCategory ic " +
            "LEFT JOIN t.possibleRootCause pi " +
            "LEFT JOIN t.rootCauseSubCategory sc " +
            "JOIN t.division d " +
            "JOIN d.zone z " +
            "WHERE t.tripDate BETWEEN :fromDate AND :toDate " +
            "AND ic.id = :issueCategoryId " +
            "AND (:zoneId IS NULL OR :zoneId = 0 OR z.id = :zoneId) " +
            "AND (:divisionId IS NULL OR :divisionId = 0 OR d.id = :divisionId) " +
            "GROUP BY pi.id")
    List<Object[]> findRootCausesCountsByIssueCategoryIdAndFilters(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("issueCategoryId") int issueCategoryId,
            @Param("zoneId") Long zoneId,
            @Param("divisionId") Long divisionId
    );

    @Query("SELECT pi.name, COUNT(t) " +
            "FROM tcasbreakinginspection t " +
            "JOIN t.issueCategory ic " +
            "LEFT JOIN t.possibleRootCause rc " +
            "LEFT JOIN t.rootCauseSubCategory pi " +
            "JOIN t.division d " +
            "JOIN d.zone z " +
            "WHERE t.tripDate BETWEEN :fromDate AND :toDate " +
            "AND ic.id = :issueCategoryId " +
            "AND (:zoneId IS NULL OR :zoneId = 0 OR z.id = :zoneId) " +
            "AND (:divisionId IS NULL OR :divisionId = 0 OR d.id = :divisionId) " +
            "GROUP BY pi.name")
    List<Object[]> findRootCauseSubCategoryCountsWithFilters(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("issueCategoryId") int issueCategoryId,
            @Param("zoneId") Long zoneId,
            @Param("divisionId") Long divisionId
    );


}
