package com.railbit.tcasanalysis.service.serviceImpl;

import com.railbit.tcasanalysis.entity.KavachDashboard;
import com.railbit.tcasanalysis.repository.KavachDashboardRepository;
import com.railbit.tcasanalysis.service.KavachDashboardService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class KavachDashboardImpl implements KavachDashboardService {

    private final KavachDashboardRepository kavachDashboardRepository;
    @Override
    public List<KavachDashboard> getAllKavachDashboards() {
        return kavachDashboardRepository.findAll();
    }

}
