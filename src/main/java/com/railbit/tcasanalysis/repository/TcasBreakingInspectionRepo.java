package com.railbit.tcasanalysis.repository;


import com.railbit.tcasanalysis.entity.IncidentTicket;
import com.railbit.tcasanalysis.entity.IssueCategory;
import com.railbit.tcasanalysis.entity.TcasBreakingInspection;
import com.railbit.tcasanalysis.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TcasBreakingInspectionRepo extends JpaRepository<TcasBreakingInspection,Long> {

    // Find all inspections by the IncidentTicket
    List<TcasBreakingInspection> findByIncidentTicket(IncidentTicket incidentTicket);

    // Alternatively, find all inspections by the IncidentTicket ID
    List<TcasBreakingInspection> findByIncidentTicket_Id(Long incidentTicketId);

    TcasBreakingInspection findFirstByIncidentTagOrderByIdDesc(String incidentTag);

    //getting filtered data
    List<TcasBreakingInspection> findAll(Specification<TcasBreakingInspection> spec);
//    List<TcasBreakingInspection> findAll(Specification<TcasBreakingInspection> spec, Pageable pageable);
    Page<TcasBreakingInspection> findAll(Specification<TcasBreakingInspection> spec, Pageable pageable);

    @Query("SELECT tb FROM tcasbreakinginspection tb " +
            "JOIN tb.faultyStation s " +
            "JOIN s.division d " +
            "WHERE d.id = :divisionId " +
            "ORDER BY tb.tripDate DESC")
    List<TcasBreakingInspection> findByDivisionIdOrderByTripDateDesc(int divisionId);

    @Query("SELECT t FROM tcasbreakinginspection t ORDER BY t.tripDate DESC")
    List<TcasBreakingInspection> findByLatestTripDate();



    @Query("SELECT t FROM tcasbreakinginspection t WHERE MONTH(t.tripDate) = :month ORDER BY t.tripDate DESC")
    List<TcasBreakingInspection> getAllByMonth(int month);
    @Query("SELECT t.issueCategory, COUNT(t.issueCategory) " +
            "FROM tcasbreakinginspection t " +
            "WHERE MONTH(t.tripDate) = :month AND YEAR(t.tripDate) = :year " +
            "GROUP BY t.issueCategory")
    List<Object[]> findCountsByIssueCategoryForMonthAndYear(int month, int year);
    @Query("SELECT t.possibleIssue, COUNT(t.possibleIssue) " +
            "FROM tcasbreakinginspection t " +
            "WHERE MONTH(t.tripDate) = :month AND YEAR(t.tripDate) = :year " +
            "GROUP BY t.possibleIssue")
    List<Object[]> findCountsByPossibleIssueForMonthAndYear(int month, int year);
    @Query("SELECT t.possibleRootCause, COUNT(t.possibleRootCause) " +
            "FROM tcasbreakinginspection t " +
            "WHERE MONTH(t.tripDate) = :month AND YEAR(t.tripDate) = :year " +
            "GROUP BY t.possibleRootCause")
    List<Object[]> findCountsByPossibleRootCauseForMonthAndYear(int month, int year);
//    @Query("SELECT t.tcas, COUNT(t.tcas) FROM TcasBreakingInspection t WHERE MONTH(t.tripDate) = :month AND YEAR(t.tripDate) = :year GROUP BY t.tcas")
//    List<Object[]> findCountsByTcasForMonthAndYear(int month, int year);

    @Query("SELECT pi.name, COUNT(t) " +
            "FROM tcasbreakinginspection t " +
            "JOIN t.issueCategory ic " +
            "JOIN t.possibleIssue pi " +
            "WHERE MONTH(t.tripDate) = :month " +
            "AND YEAR(t.tripDate) = :year " +
            "AND ic.id = :issueCategoryId " +
            "GROUP BY pi.id")
    List<Object[]> findIssuesCountsByIssueCategoryIdForMonthAndYear(int month, int year, int issueCategoryId);

    @Query("SELECT pi.name, COUNT(t) " +
            "FROM tcasbreakinginspection t " +
            "JOIN t.issueCategory ic " +
            "JOIN t.possibleRootCause pi " +
            "WHERE MONTH(t.tripDate) = :month " +
            "AND YEAR(t.tripDate) = :year " +
            "AND ic.id = :issueCategoryId " +
            "GROUP BY pi.id")
    List<Object[]> findRootCausesCountsByIssueCategoryIdForMonthAndYear(int month, int year, int issueCategoryId);

    @Query("SELECT COUNT(t) " +
            "FROM tcasbreakinginspection t " +
            "JOIN t.issueCategory ic " +
            "JOIN t.possibleIssue pi " +
            "WHERE MONTH(t.tripDate) = :month " +
            "AND YEAR(t.tripDate) = :year " +
            "AND ic.id = :issueCategoryId " +
            "AND pi.id = :possibleIssueId")
    Long findIssueCountByIssueCategoryIdForMonthAndYearAndPossibleIssueId(int month, int year, int issueCategoryId, int possibleIssueId);


//    @Query("SELECT t.tcas, COUNT(t.tcas) " +
//            "FROM TcasBreakingInspection t " +
//            "JOIN t.issueCategory ic " +
//            "WHERE MONTH(t.tripDate) = :month " +
//            "AND YEAR(t.tripDate) = :year " +
//            "AND ic.id = :issueCategoryId GROUP BY t.tcas")
//    List<Object[]> findTcasCountsByIssueCategoryIdForMonthAndYear(int month, int year, int issueCategoryId);

    @Query("SELECT ic.name, COUNT(t) " +
            "FROM tcasbreakinginspection t " +
            "JOIN t.issueCategory ic " +
            "LEFT JOIN t.possibleRootCause rc " +
            "LEFT JOIN t.rootCauseSubCategory sc " +
            "JOIN t.division d " +
            "JOIN d.zone z " +
            "WHERE FUNCTION('MONTH', t.tripDate) = :month " +
            "AND FUNCTION('YEAR', t.tripDate) = :year " +
            "AND ic.name != 'No Issue' " +
            "AND ic.id = :issueCategoryId " +
            "AND (:zoneId IS NULL OR :zoneId = 0 OR z.id = :zoneId) " +
            "AND (:divisionId IS NULL OR :divisionId = 0 OR d.id = :divisionId)")
    List<Object[]> findAllInspectionsCountByMonthYearIssueCategoryIdAndFilters(
            @Param("month") int month,
            @Param("year") int year,
            @Param("issueCategoryId") int issueCategoryId,
            @Param("zoneId") Long zoneId,
            @Param("divisionId") Long divisionId);


    //Between Trip Dates
    List<TcasBreakingInspection> findBytripDateBetweenAndStatusAndIssueCategory_Id(LocalDate fromDate, LocalDate toDate, String status, Integer issueCategoryId);

    // For Yearly Report Excel Below
    @Query("SELECT COUNT(t) " +
            "FROM tcasbreakinginspection t " +
            "WHERE MONTH(t.tripDate) = :month " +
            "AND YEAR(t.tripDate) = :year")
    int getTotalInspectionsByMonthAndYear(@Param("month") int month, @Param("year") int year);

    @Query("SELECT COUNT(t) " +
            "FROM tcasbreakinginspection t " +
            "JOIN t.faultyStation s " +
            "JOIN s.division d " +
            "WHERE MONTH(t.tripDate) = :month " +
            "AND YEAR(t.tripDate) = :year " +
            "AND d.id = :divisionId")
    int getTotalInspectionsByMonthAndYearAndDivisionId(@Param("month") int month, @Param("year") int year, @Param("divisionId") int divisionId);

    @Query("SELECT ic.name, COUNT(t) " +
            "FROM tcasbreakinginspection t " +
            "JOIN t.issueCategory ic " +
            "JOIN t.faultyStation s " +
            "JOIN s.division d " +
            "WHERE FUNCTION('MONTH', t.tripDate) = :month " +
            "AND FUNCTION('YEAR', t.tripDate) = :year " +
            "AND d.id = :divisionId " +
            "AND ic.id = :issueCategoryId")
    List<Object[]> findAllInspectionsCountByMonthYearAndIssueCategoryIdAndDivisionId(int month, int year, int issueCategoryId, int divisionId);

    // For Yearly Report Excel Above

    // For Yearly Graphs Below

    @Query("SELECT MONTH(t.tripDate) AS month, COUNT(t.tripDate) AS count " +
            "FROM tcasbreakinginspection t " +
            "JOIN t.division d " +
            "JOIN d.zone z " +
            "JOIN t.issueCategory ic " +
            "LEFT JOIN t.possibleRootCause rc " +
            "LEFT JOIN t.rootCauseSubCategory sc " +
            "WHERE YEAR(t.tripDate) = :year " +
            "AND ic.name != 'No Issue' " +
            "AND (:zoneId IS NULL OR :zoneId = 0 OR z.id = :zoneId) " +
            "AND (:divisionId IS NULL OR :divisionId = 0 OR d.id = :divisionId) " +
            "GROUP BY MONTH(t.tripDate) " +
            "ORDER BY MONTH(t.tripDate)")
    List<Object[]> findTotalMonthlyCountsByYearAndFilters(
            @Param("year") int year,
            @Param("zoneId") Long zoneId,
            @Param("divisionId") Long divisionId);

    // For Yearly Graphs Above




}
