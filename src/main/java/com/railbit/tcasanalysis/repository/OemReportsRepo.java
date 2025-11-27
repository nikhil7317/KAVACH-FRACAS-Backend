package com.railbit.tcasanalysis.repository;


import com.railbit.tcasanalysis.entity.TcasBreakingInspection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface OemReportsRepo extends JpaRepository<TcasBreakingInspection,Integer> {

    // Monthly Data Count according to Firm
    // Group By issueCategory
    @Query("SELECT t.issueCategory, COUNT(t.issueCategory) " +
            "FROM tcasbreakinginspection t " +
            "JOIN t.issueCategory ic " +
            "LEFT JOIN t.possibleRootCause rc " +
            "LEFT JOIN t.rootCauseSubCategory sc " +
            "JOIN t.faultyStation s " +
            "JOIN s.firm f1 " +
            "JOIN t.loco l " +
            "JOIN l.firm f2 " +
            "WHERE (f1.id = :firmId OR f2.id = :firmId) " +
            "AND ic.name != 'No Issue' " +
            "AND (rc.name != 'Alterations' OR rc.name IS NULL) " +
            "AND (sc.name != 'ALTERATION WORK' OR sc.name IS NULL) " +
            "AND t.createdDateTime BETWEEN :fromDate AND :toDate " +
            "GROUP BY t.issueCategory")
    List<Object[]> findInspectionCountsByIssueCategoryAndDate(
            @Param("firmId") int firmId,
            @Param("fromDate") LocalDateTime  fromDate,
            @Param("toDate") LocalDateTime toDate);

    // Monthly Data Count according to Firm
    // Group By Possible Issues
    @Query("SELECT t.possibleIssue, COUNT(t.possibleIssue) " +
            "FROM tcasbreakinginspection t " +
            "JOIN t.issueCategory ic " +
            "LEFT JOIN t.possibleRootCause rc " +
            "LEFT JOIN t.rootCauseSubCategory sc " +
            "JOIN t.faultyStation s " +
            "JOIN s.firm f1 " +
            "JOIN t.loco l " +
            "JOIN l.firm f2 " +
            "WHERE (f1.id = :firmId OR f2.id = :firmId) " +
            "AND ic.name != 'No Issue' " +
            "AND (rc.name != 'Alterations' OR rc.name IS NULL) " +
            "AND (sc.name != 'ALTERATION WORK' OR sc.name IS NULL) " +
            "AND t.createdDateTime BETWEEN :fromDate AND :toDate " +
            "GROUP BY t.possibleIssue")
    List<Object[]> findInspectionCountsByPossibleIssueAndDate(
            @Param("firmId") int firmId,
            @Param("fromDate") LocalDateTime  fromDate,
            @Param("toDate") LocalDateTime toDate);

    // Monthly Data Count according to Firm
    // Group By Possible Root Causes
    @Query("SELECT t.possibleRootCause, COUNT(t.possibleRootCause) " +
            "FROM tcasbreakinginspection t " +
            "JOIN t.faultyStation s " +
            "JOIN s.firm f1 " +
            "JOIN t.loco l " +
            "JOIN l.firm f2 " +
            "JOIN t.issueCategory ic " +
            "LEFT JOIN t.possibleRootCause rc " +
            "LEFT JOIN t.rootCauseSubCategory sc " +
            "WHERE (f1.id = :firmId OR f2.id = :firmId) " +
            "AND ic.name != 'No Issue' " +
            "AND (rc.name != 'Alterations') " +
            "AND (sc.name != 'ALTERATION WORK' OR sc.name IS NULL) " +
            "AND t.createdDateTime BETWEEN :fromDate AND :toDate " +
            "GROUP BY t.possibleRootCause")
    List<Object[]> findInspectionCountsByPossibleRootCauseAndDate(
            @Param("firmId") int firmId,
            @Param("fromDate") LocalDateTime  fromDate,
            @Param("toDate") LocalDateTime toDate);

    @Query("SELECT ic.name, COUNT(t) " +
            "FROM tcasbreakinginspection t " +
            "JOIN t.issueCategory ic " +
            "LEFT JOIN t.possibleRootCause rc " +
            "LEFT JOIN t.rootCauseSubCategory sc " +
            "JOIN t.faultyStation s " +
            "JOIN s.firm f1 " +
            "JOIN t.loco l " +
            "JOIN l.firm f2 " +
            "WHERE (f1.id = :firmId OR f2.id = :firmId) " +
            "AND ic.name != 'No Issue' " +
            "AND (rc.name != 'Alterations' OR rc.name IS NULL) " +
            "AND (sc.name != 'ALTERATION WORK' OR sc.name IS NULL) " +
            "AND t.createdDateTime BETWEEN :fromDate AND :toDate " +
            "AND ic.id = :issueCategoryId")
    List<Object[]> findAllInspectionsCountByMonthYearAndIssueCategoryIdAndFirmId(LocalDateTime  fromDate, LocalDateTime toDate,
                                                                                 int issueCategoryId,int firmId);

    // Monthly Data Count according to Firm and IssueCategory
    // Group By Possible Issues
    @Query("SELECT t.possibleIssue, COUNT(t.possibleIssue) " +
            "FROM tcasbreakinginspection t " +
            "JOIN t.faultyStation s " +
            "JOIN s.firm f1 " +
            "JOIN t.loco l " +
            "JOIN l.firm f2 " +
            "JOIN t.issueCategory ic " +
            "LEFT JOIN t.possibleRootCause rc " +
            "LEFT JOIN t.rootCauseSubCategory sc " +
            "WHERE (f1.id = :firmId OR f2.id = :firmId) " +
            "AND ic.name != 'No Issue' " +
            "AND (rc.name != 'Alterations' OR rc.name IS NULL) " +
            "AND (sc.name != 'ALTERATION WORK' OR sc.name IS NULL) " +
            "AND ic.id = :issueCategoryId " +
            "AND t.createdDateTime BETWEEN :fromDate AND :toDate " +
            "GROUP BY t.possibleIssue")
    List<Object[]> findInspectionCountsByPossibleIssueAndIssueCategoryAndDate(
            @Param("issueCategoryId") int issueCategoryId,
            @Param("firmId") int firmId,
            @Param("fromDate") LocalDateTime  fromDate,
            @Param("toDate") LocalDateTime toDate);

    // Monthly Data Count according to Firm
    // Group By Possible Root Causes
    @Query("SELECT t.possibleRootCause, COUNT(t.possibleRootCause) " +
            "FROM tcasbreakinginspection t " +
            "JOIN t.faultyStation s " +
            "JOIN s.firm f1 " +
            "JOIN t.loco l " +
            "JOIN l.firm f2 " +
            "JOIN t.issueCategory ic " +
            "LEFT JOIN t.possibleRootCause rc " +
            "LEFT JOIN t.rootCauseSubCategory sc " +
            "WHERE (f1.id = :firmId OR f2.id = :firmId) " +
            "AND ic.id = :issueCategoryId " +
            "AND ic.name != 'No Issue' " +
            "AND (rc.name != 'Alterations') " +
            "AND (sc.name != 'ALTERATION WORK' OR sc.name IS NULL) " +
            "AND t.createdDateTime BETWEEN :fromDate AND :toDate " +
            "GROUP BY t.possibleRootCause")
    List<Object[]> findInspectionCountsByPossibleRootCauseAndIssueCategoryAndDate(
            @Param("issueCategoryId") int issueCategoryId,
            @Param("firmId") int firmId,
            @Param("fromDate") LocalDateTime  fromDate,
            @Param("toDate") LocalDateTime toDate);


    //Gets Firm wise count if inspection is closed within 2 days
    @Query(value = "SELECT COUNT(ts.id) " +
            "FROM tcasbreakinginspectionstatus ts " +
            "JOIN tcasbreakinginspection t ON ts.tcasBreakingInspection_id = t.id " +
            "JOIN station s ON t.faultyStation_id = s.id " +
            "JOIN firm f1 ON s.firm_id = f1.id " +
            "JOIN loco l ON t.loco_id = l.id " +
            "JOIN firm f2 ON l.firm_id = f2.id " +
            "WHERE ts.createdDateTime = (" +
            "    SELECT MAX(ts2.createdDateTime) " +
            "    FROM tcasbreakinginspectionstatus ts2 " +
            "    WHERE ts2.tcasBreakingInspection_id = ts.tcasBreakingInspection_id" +
            ") " +
            "AND (f1.id = :firmId OR f2.id = :firmId) " +
            "AND t.status = 'Close' " +
            "AND t.createdDateTime BETWEEN :fromDate AND :toDate " +
            "AND ts.createdDateTime <= DATE_ADD(t.createdDateTime, INTERVAL 3 DAY)", nativeQuery = true)
    long countByFirmWiseClosedInspectionWithinThreeDays(
            @Param("firmId") int firmId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate);

    //Gets Firm wise count if inspection is closed within 5 days
    @Query(value = "SELECT COUNT(ts.id) " +
            "FROM tcasbreakinginspectionstatus ts " +
            "JOIN tcasbreakinginspection t ON ts.tcasBreakingInspection_id = t.id " +
            "JOIN station s ON t.faultyStation_id = s.id " +
            "JOIN firm f1 ON s.firm_id = f1.id " +
            "JOIN loco l ON t.loco_id = l.id " +
            "JOIN firm f2 ON l.firm_id = f2.id " +
            "WHERE ts.createdDateTime = (" +
            "    SELECT MAX(ts2.createdDateTime) " +
            "    FROM tcasbreakinginspectionstatus ts2 " +
            "    WHERE ts2.tcasBreakingInspection_id = ts.tcasBreakingInspection_id" +
            ") " +
            "AND (f1.id = :firmId OR f2.id = :firmId) " +
            "AND t.status = 'Close' " +
            "AND t.createdDateTime BETWEEN :fromDate AND :toDate " +
            "AND ts.createdDateTime > DATE_ADD(t.createdDateTime, INTERVAL 3 DAY)" +
            "AND ts.createdDateTime <= DATE_ADD(t.createdDateTime, INTERVAL 6 DAY)", nativeQuery = true)
    long countByFirmWiseClosedInspectionWithinFiveDays(
            @Param("firmId") int firmId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate);

    //Gets Firm wise count if inspection is closed within 5 to 10 days
    @Query(value = "SELECT COUNT(ts.id) " +
            "FROM tcasbreakinginspectionstatus ts " +
            "JOIN tcasbreakinginspection t ON ts.tcasBreakingInspection_id = t.id " +
            "JOIN station s ON t.faultyStation_id = s.id " +
            "JOIN firm f1 ON s.firm_id = f1.id " +
            "JOIN loco l ON t.loco_id = l.id " +
            "JOIN firm f2 ON l.firm_id = f2.id " +
            "WHERE ts.createdDateTime = (" +
            "    SELECT MAX(ts2.createdDateTime) " +
            "    FROM tcasbreakinginspectionstatus ts2 " +
            "    WHERE ts2.tcasBreakingInspection_id = ts.tcasBreakingInspection_id" +
            ") " +
            "AND (f1.id = :firmId OR f2.id = :firmId) " +
            "AND t.status = 'Close' " +
            "AND t.createdDateTime BETWEEN :fromDate AND :toDate " +
            "AND ts.createdDateTime > DATE_ADD(t.createdDateTime, INTERVAL 6 DAY)" +
            "AND ts.createdDateTime <= DATE_ADD(t.createdDateTime, INTERVAL 11 DAY)", nativeQuery = true)
    long countByFirmWiseClosedInspectionWithinFiveToTenDays(
            @Param("firmId") int firmId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate);

    //Gets Firm wise count if inspection is closed within 10 to 30 days
    @Query(value = "SELECT COUNT(ts.id) " +
            "FROM tcasbreakinginspectionstatus ts " +
            "JOIN tcasbreakinginspection t ON ts.tcasBreakingInspection_id = t.id " +
            "JOIN station s ON t.faultyStation_id = s.id " +
            "JOIN firm f1 ON s.firm_id = f1.id " +
            "JOIN loco l ON t.loco_id = l.id " +
            "JOIN firm f2 ON l.firm_id = f2.id " +
            "WHERE ts.createdDateTime = (" +
            "    SELECT MAX(ts2.createdDateTime) " +
            "    FROM tcasbreakinginspectionstatus ts2 " +
            "    WHERE ts2.tcasBreakingInspection_id = ts.tcasBreakingInspection_id" +
            ") " +
            "AND (f1.id = :firmId OR f2.id = :firmId) " +
            "AND t.status = 'Close' " +
            "AND t.createdDateTime BETWEEN :fromDate AND :toDate " +
            "AND ts.createdDateTime > DATE_ADD(t.createdDateTime, INTERVAL 11 DAY)" +
            "AND ts.createdDateTime <= DATE_ADD(t.createdDateTime, INTERVAL 31 DAY)", nativeQuery = true)
    long countByFirmWiseClosedInspectionWithinTenToThirtyDays(
            @Param("firmId") int firmId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate);

    //Gets Firm wise count if inspection is closed in more than 30 Days
    @Query(value = "SELECT COUNT(ts.id) " +
            "FROM tcasbreakinginspectionstatus ts " +
            "JOIN tcasbreakinginspection t ON ts.tcasBreakingInspection_id = t.id " +
            "JOIN station s ON t.faultyStation_id = s.id " +
            "JOIN firm f1 ON s.firm_id = f1.id " +
            "JOIN loco l ON t.loco_id = l.id " +
            "JOIN firm f2 ON l.firm_id = f2.id " +
            "WHERE ts.createdDateTime = (" +
            "    SELECT MAX(ts2.createdDateTime) " +
            "    FROM tcasbreakinginspectionstatus ts2 " +
            "    WHERE ts2.tcasBreakingInspection_id = ts.tcasBreakingInspection_id" +
            ") " +
            "AND (f1.id = :firmId OR f2.id = :firmId) " +
            "AND t.status = 'Close' " +
            "AND t.createdDateTime BETWEEN :fromDate AND :toDate " +
            "AND ts.createdDateTime > DATE_ADD(t.createdDateTime, INTERVAL 31 DAY)", nativeQuery = true)
    long countByFirmWiseClosedInspectionInMoreThanThirtyDays(
            @Param("firmId") int firmId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate);

    //Open more than 2 days
    @Query(value = "SELECT COUNT(t.id) " +
            "FROM tcasbreakinginspection t " +
            "JOIN station s ON t.faultyStation_id = s.id " +
            "JOIN firm f1 ON s.firm_id = f1.id " +
            "JOIN loco l ON t.loco_id = l.id " +
            "JOIN firm f2 ON l.firm_id = f2.id " +
            "WHERE (f1.id = :firmId OR f2.id = :firmId) " +
            "AND t.status = 'Open' " +
            "AND t.createdDateTime BETWEEN :fromDate AND :toDate " +
            "AND t.createdDateTime BETWEEN CURRENT_DATE - INTERVAL '2' DAY AND CURRENT_DATE - INTERVAL '0' DAY", nativeQuery = true)
    long countOpenInspectionsOlderThan2Days(@Param("firmId") int firmId,
                                            @Param("fromDate") LocalDateTime fromDate,
                                            @Param("toDate") LocalDateTime toDate);

    //Open more than 5 days
    @Query(value = "SELECT COUNT(t.id) " +
            "FROM tcasbreakinginspection t " +
            "JOIN station s ON t.faultyStation_id = s.id " +
            "JOIN firm f1 ON s.firm_id = f1.id " +
            "JOIN loco l ON t.loco_id = l.id " +
            "JOIN firm f2 ON l.firm_id = f2.id " +
            "WHERE (f1.id = :firmId OR f2.id = :firmId) " +
            "AND t.status = 'Open' " +
            "AND t.createdDateTime BETWEEN :fromDate AND :toDate " +
            "AND t.createdDateTime BETWEEN CURRENT_DATE - INTERVAL '5' DAY AND CURRENT_DATE - INTERVAL '3' DAY", nativeQuery = true)
    long countOpenInspectionsOlderThan5Days(@Param("firmId") int firmId,
                                            @Param("fromDate") LocalDateTime fromDate,
                                            @Param("toDate") LocalDateTime toDate);

    //Open more than 10 days
    @Query(value = "SELECT COUNT(t.id) " +
            "FROM tcasbreakinginspection t " +
            "JOIN station s ON t.faultyStation_id = s.id " +
            "JOIN firm f1 ON s.firm_id = f1.id " +
            "JOIN loco l ON t.loco_id = l.id " +
            "JOIN firm f2 ON l.firm_id = f2.id " +
            "WHERE (f1.id = :firmId OR f2.id = :firmId) " +
            "AND t.status = 'Open' " +
            "AND t.createdDateTime BETWEEN :fromDate AND :toDate " +
            "AND t.createdDateTime BETWEEN CURRENT_DATE - INTERVAL '10' DAY AND CURRENT_DATE - INTERVAL '6' DAY", nativeQuery = true)
    long countOpenInspectionsOlderThan10Days(@Param("firmId") int firmId,
                                            @Param("fromDate") LocalDateTime fromDate,
                                            @Param("toDate") LocalDateTime toDate);

    //Open more than 20 days
    @Query(value = "SELECT COUNT(t.id) " +
            "FROM tcasbreakinginspection t " +
            "JOIN station s ON t.faultyStation_id = s.id " +
            "JOIN firm f1 ON s.firm_id = f1.id " +
            "JOIN loco l ON t.loco_id = l.id " +
            "JOIN firm f2 ON l.firm_id = f2.id " +
            "WHERE (f1.id = :firmId OR f2.id = :firmId) " +
            "AND t.status = 'Open' " +
            "AND t.createdDateTime BETWEEN :fromDate AND :toDate " +
            "AND t.createdDateTime BETWEEN CURRENT_DATE - INTERVAL '20' DAY AND CURRENT_DATE - INTERVAL '11' DAY", nativeQuery = true)
    long countOpenInspectionsOlderThan20Days(@Param("firmId") int firmId,
                                             @Param("fromDate") LocalDateTime fromDate,
                                             @Param("toDate") LocalDateTime toDate);

    //Open more than 30 days
    @Query(value = "SELECT COUNT(t.id) " +
            "FROM tcasbreakinginspection t " +
            "JOIN station s ON t.faultyStation_id = s.id " +
            "JOIN firm f1 ON s.firm_id = f1.id " +
            "JOIN loco l ON t.loco_id = l.id " +
            "JOIN firm f2 ON l.firm_id = f2.id " +
            "WHERE (f1.id = :firmId OR f2.id = :firmId) " +
            "AND t.status = 'Open' " +
            "AND t.createdDateTime BETWEEN :fromDate AND :toDate " +
            "AND t.createdDateTime <= CURRENT_DATE - INTERVAL '30' DAY", nativeQuery = true)
    long countOpenInspectionsOlderThan30Days(@Param("firmId") int firmId,
                                             @Param("fromDate") LocalDateTime fromDate,
                                             @Param("toDate") LocalDateTime toDate);

    //No of Days to Close Firmwise
    @Query(value = "SELECT DATEDIFF(ts.createdDateTime, t.createdDateTime) " +
            "FROM tcasbreakinginspectionstatus ts " +
            "JOIN tcasbreakinginspection t ON ts.tcasBreakingInspection_id = t.id " +
            "JOIN station s ON t.faultyStation_id = s.id " +
            "JOIN firm f1 ON s.firm_id = f1.id " +
            "JOIN loco l ON t.loco_id = l.id " +
            "JOIN firm f2 ON l.firm_id = f2.id " +
            "WHERE ts.createdDateTime = (" +
            "  SELECT MAX(ts2.createdDateTime) " +
            "  FROM tcasbreakinginspectionstatus ts2 " +
            "  WHERE ts2.tcasBreakingInspection_id = ts.tcasBreakingInspection_id) " +
            "AND (f1.id = :firmId OR f2.id = :firmId) " +
            "AND t.status = 'close'", nativeQuery = true)
    List<Long> daysTakenToCloseByFirm(@Param("firmId") int firmId);

    //No of Days to Close FirmWise and IssueCategoryWise
    @Query(value = "SELECT DATEDIFF(ts.createdDateTime, t.createdDateTime) " +
            "FROM tcasbreakinginspectionstatus ts " +
            "JOIN tcasbreakinginspection t ON ts.tcasBreakingInspection_id = t.id " +
            "JOIN station s ON t.faultyStation_id = s.id " +
            "JOIN firm f1 ON s.firm_id = f1.id " +
            "JOIN loco l ON t.loco_id = l.id " +
            "JOIN firm f2 ON l.firm_id = f2.id " +
            "JOIN issuecategory ic ON t.issueCategory_id = ic.id " +
            "WHERE ts.createdDateTime = (" +
            "  SELECT MAX(ts2.createdDateTime) " +
            "  FROM tcasbreakinginspectionstatus ts2 " +
            "  WHERE ts2.tcasBreakingInspection_id = ts.tcasBreakingInspection_id) " +
            "AND (f1.id = :firmId OR f2.id = :firmId) " +
            "AND ic.id = :issueCategoryId " +
            "AND t.status = 'close'", nativeQuery = true)
    List<Long> daysTakenToCloseByFirmAndIssueCategory(@Param("firmId") int firmId,@Param("issueCategoryId") int issueCategoryId);

    @Query(value = "SELECT t.status, COUNT(t.id) AS total_incidents " +
            "FROM tcasbreakinginspection t " +
            "JOIN loco l ON l.id = t.loco_id " +
            "JOIN station s ON s.id = t.faultyStation_id " +
            "LEFT JOIN firm f1 ON f1.id = l.firm_id " +
            "LEFT JOIN firm f2 ON f2.id = s.firm_id " +
            "WHERE (f1.id = :firmId OR f2.id = :firmId) " +
            "GROUP BY t.status " +
            "UNION ALL " +
            "SELECT 'Total' AS status, COUNT(t.id) AS total_incidents " +
            "FROM tcasbreakinginspection t " +
            "JOIN loco l ON l.id = t.loco_id " +
            "JOIN station s ON s.id = t.faultyStation_id " +
            "LEFT JOIN firm f1 ON f1.id = l.firm_id " +
            "LEFT JOIN firm f2 ON f2.id = s.firm_id " +
            "WHERE (f1.id = :firmId OR f2.id = :firmId)", nativeQuery = true)
    List<Object[]> findCountsByFirmAndStatus(@Param("firmId") int firmId);

    @Query(value = "SELECT t.status, COUNT(t.id) AS total_incidents " +
            "FROM tcasbreakinginspection t " +
            "JOIN station s ON t.faultyStation_id = s.id " +
            "JOIN firm f1 ON s.firm_id = f1.id " +
            "JOIN loco l ON t.loco_id = l.id " +
            "JOIN firm f2 ON l.firm_id = f2.id " +
            "WHERE MONTH(t.createdDateTime) = :month " +
            "AND YEAR(t.createdDateTime) = :year " +
            "AND (f1.id = :firmId OR f2.id = :firmId) " +
            "GROUP BY t.status",
            nativeQuery = true)
    List<Object[]> findMonthlyInspectionCountsByFirmGroupByStatus(@Param("month") int month,
                                                       @Param("year") int year,
                                                       @Param("firmId") int firmId);


    // By Firm and IssueCategory
    @Query("SELECT t.status, COUNT(t) " +
            "FROM tcasbreakinginspection t " +
            "JOIN t.faultyStation s " +
            "JOIN s.firm f1 " +
            "JOIN t.loco l " +
            "JOIN l.firm f2 " +
            "JOIN t.issueCategory ic ON ic.id = t.issueCategory.id " +
            "WHERE (f1.id = :firmId OR f2.id = :firmId) " +
            "AND t.issueCategory.id = :issueCategoryId " +
            "GROUP BY t.status")
    List<Object[]> findInspectionCountsByFirmAndStatus(
            @Param("firmId") int firmId,
            @Param("issueCategoryId") int issueCategoryId);


}
