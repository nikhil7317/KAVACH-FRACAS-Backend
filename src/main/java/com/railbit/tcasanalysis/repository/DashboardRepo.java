package com.railbit.tcasanalysis.repository;


import com.railbit.tcasanalysis.entity.TcasBreakingInspection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface DashboardRepo extends JpaRepository<TcasBreakingInspection,Long> {

    @Query("SELECT fs.code, COUNT(tci) AS totalCount " +
            "FROM tcasbreakinginspection tci " +
            "JOIN tci.faultyStation fs " +
            "JOIN tci.issueCategory ic " +
            "WHERE (ic.name!='No Issue') " +
            "AND tci.tripDate BETWEEN :fromDate AND :toDate " +
            "GROUP BY fs.code " +
            "ORDER BY totalCount DESC")
    List<Object[]> findTopStationsWithMostIssuesBetweenDates(@Param("fromDate") LocalDate fromDate,
                                                             @Param("toDate") LocalDate toDate,
                                                             Pageable pageable);

    @Query("SELECT l.locoNo, COUNT(tci) AS totalCount " +
            "FROM tcasbreakinginspection tci " +
            "JOIN tci.loco l " +
            "JOIN tci.issueCategory ic " +
            "WHERE (ic.name!='No Issue') " +
            "AND tci.tripDate BETWEEN :fromDate AND :toDate " +
            "GROUP BY l.locoNo " +
            "ORDER BY totalCount DESC")
    List<Object[]> findTopLocosWithMostIssuesBetweenDates(@Param("fromDate") LocalDate fromDate,
                                                             @Param("toDate") LocalDate toDate,
                                                             Pageable pageable);

    @Query("SELECT r.name, COUNT(tci) AS totalCount " +
            "FROM tcasbreakinginspection tci " +
            "JOIN tci.possibleRootCause r " +
            "JOIN tci.issueCategory ic " +
            "WHERE (ic.name!='No Issue') " +
            "AND tci.tripDate BETWEEN :fromDate AND :toDate " +
            "GROUP BY r.name " +
            "ORDER BY totalCount DESC")
    List<Object[]> findTopCausesWithMostIssuesBetweenDates(@Param("fromDate") LocalDate fromDate,
                                                          @Param("toDate") LocalDate toDate,
                                                          Pageable pageable);

    @Query("SELECT d.code, COUNT(tci) AS totalCount " +
            "FROM tcasbreakinginspection tci " +
            "JOIN tci.faultyStation fs " +
            "JOIN fs.division d " +
            "JOIN tci.issueCategory ic " +
            "WHERE ic.name!='No Issue' " +
            "AND tci.tripDate BETWEEN :fromDate AND :toDate " +
            "GROUP BY d.code " +
            "ORDER BY totalCount DESC")
    List<Object[]> findTopDivisionsWithMostIssuesBetweenDates(@Param("fromDate") LocalDate fromDate,
                                                           @Param("toDate") LocalDate toDate,
                                                           Pageable pageable);

    @Query("SELECT COUNT(tci) AS totalCount " +
            "FROM tcasbreakinginspection tci " +
            "JOIN tci.issueCategory ic " +
            "WHERE (ic.name!='No Issue') " +
            "AND tci.tripDate BETWEEN :fromDate AND :toDate " +
            "ORDER BY totalCount DESC")
    List<Object[]> findTopOEMsWithMostIssuesBetweenDates(@Param("fromDate") LocalDate fromDate,
                                                              @Param("toDate") LocalDate toDate,
                                                              Pageable pageable);


}
