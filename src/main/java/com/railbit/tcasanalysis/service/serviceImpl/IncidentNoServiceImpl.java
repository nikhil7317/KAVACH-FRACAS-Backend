package com.railbit.tcasanalysis.service.serviceImpl;


import com.railbit.tcasanalysis.entity.Firm;
import com.railbit.tcasanalysis.entity.IncidentNo;
import com.railbit.tcasanalysis.repository.FirmRepo;
import com.railbit.tcasanalysis.repository.IncidentNoRepo;
import com.railbit.tcasanalysis.service.IncidentNoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class IncidentNoServiceImpl implements IncidentNoService {
    private final IncidentNoRepo incidentNoRepo;

    @Override
    public void updateIncident(IncidentNo incidentNo) {
        incidentNoRepo.save(incidentNo);
    }
}
