package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.LocoFailure;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface LocoFailureRepository extends JpaRepository<LocoFailure, Long> {
    long countByTicketNoContaining(String datePart);
    @Query("SELECT lf FROM LocoFailure lf WHERE " +
            "(:locoId IS NULL OR lf.locoId = :locoId) AND " +
            "(:fromDate IS NULL OR lf.incidentCreatedAt >= :fromDate) AND " +
            "(:toDate IS NULL OR lf.incidentCreatedAt <= :toDate) AND " +
            "(:severity IS NULL OR lf.severity = :severity) AND " +
            "(:ticketStatus IS NULL OR lf.ticketStatus = :ticketStatus) AND " +
            "(:ticketNo IS NULL OR LOWER(lf.ticketNo) LIKE LOWER(CONCAT('%', :ticketNo, '%'))) AND " +
            "(:userId IS NULL OR " +
            "(lf.assignedTo.id = :userId AND lf.ticketStatus IN ('OPEN', 'RE-ASSIGN', 'REASSIGN'))) " +
            "ORDER BY lf.incidentCreatedAt DESC")
    Page<LocoFailure> findAllWithFilters(
            @Param("locoId") Integer locoId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("severity") String severity,
            @Param("ticketStatus") String ticketStatus,
            @Param("ticketNo") String ticketNo,
            @Param("userId") Integer userId,
            Pageable pageable);
}