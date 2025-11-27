package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.PossibleIssue;
import com.railbit.tcasanalysis.entity.TcasBreakingInspection;
import com.railbit.tcasanalysis.entity.TcasBreakingInspectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TcasBreakingInspectionStatusRepo extends JpaRepository<TcasBreakingInspectionStatus,Long> {
    List<TcasBreakingInspectionStatus> findByTcasBreakingInspectionIdOrderByIdAsc(Long id);

    @Query("SELECT rt.type FROM tcasbreakinginspectionstatus ts " +
            "JOIN ts.user u " +
            "JOIN u.role r " +
            "JOIN ts.remarkType rt " +
            "WHERE r.name LIKE %:roleName% " +
            "AND ts.tcasBreakingInspection.id = :inspectionId AND rt.id != 1")
    List<String> findRemarkTypesByRoleNameAndInspectionId(@Param("roleName") String roleName, @Param("inspectionId") Long inspectionId);
}
