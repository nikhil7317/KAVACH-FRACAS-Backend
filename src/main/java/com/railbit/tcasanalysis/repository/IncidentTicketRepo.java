package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.Division;
import com.railbit.tcasanalysis.entity.Firm;
import com.railbit.tcasanalysis.entity.IncidentTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface IncidentTicketRepo extends JpaRepository<IncidentTicket,Long> {
    IncidentTicket findByTicketNo(String ticketNo);

    @Query("SELECT f FROM incidentticket i JOIN i.assignedFirms f WHERE i.id = :incidentTicketId")
    Set<Firm> findAssignedFirmsByIncidentTicketId(@Param("incidentTicketId") Long incidentTicketId);

    List<IncidentTicket> findByDivisionIdAndStatusOrderByIdDesc(Integer division_id, Boolean status);
    List<IncidentTicket> findByDivisionIdOrderByIdDesc(Integer division_id);

    List<IncidentTicket> findAllByOrderByIdDesc();

    int countByStatus(Boolean status);

}
