package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.Designation;
import com.railbit.tcasanalysis.entity.IncidentTicketTrack;
import com.railbit.tcasanalysis.entity.TcasBreakingInspectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentTicketTrackRepo extends JpaRepository<IncidentTicketTrack,Long> {

    List<IncidentTicketTrack> findByIncidentTicketIdOrderByIdAsc(Long id);

}
