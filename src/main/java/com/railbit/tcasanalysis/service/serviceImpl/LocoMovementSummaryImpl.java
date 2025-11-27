package com.railbit.tcasanalysis.service.serviceImpl;

import com.railbit.tcasanalysis.entity.LocoMovementSummary;
import com.railbit.tcasanalysis.repository.LocoMovementSummaryRepo;
import com.railbit.tcasanalysis.service.LocoMovementSummaryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LocoMovementSummaryImpl implements LocoMovementSummaryService {

    private final LocoMovementSummaryRepo movementSummaryRepo;


    @Override
    public void saveLocoMovementSummary(LocoMovementSummary movementSummary) {
        movementSummaryRepo.save(movementSummary);
    }
}
