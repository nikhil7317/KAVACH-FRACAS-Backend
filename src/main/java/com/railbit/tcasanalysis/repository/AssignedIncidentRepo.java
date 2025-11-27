package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.AssignedIncident;
import com.railbit.tcasanalysis.entity.TcasBreakingInspection;
import com.railbit.tcasanalysis.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AssignedIncidentRepo extends JpaRepository<AssignedIncident,Long> {
    AssignedIncident findFirstByTcasBreakingInspectionAndAssignedToUserOrderByIdDesc(TcasBreakingInspection tcasBreakingInspection, User assignedToUser);
    AssignedIncident findFirstByTcasBreakingInspectionOrderByIdDesc(TcasBreakingInspection tcasBreakingInspection);

    @Query("SELECT ai.tcasBreakingInspection FROM assignedincident ai WHERE ai.assignedToUser.id = :assignedToUserId GROUP BY ai.tcasBreakingInspection.id")
    List<TcasBreakingInspection> findByAssignedToUserGrouped(@Param("assignedToUserId") Long assignedToUserId);

}
