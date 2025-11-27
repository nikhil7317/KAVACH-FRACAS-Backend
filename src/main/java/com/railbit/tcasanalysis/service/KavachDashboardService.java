package com.railbit.tcasanalysis.service;

import com.railbit.tcasanalysis.entity.KavachDashboard;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface KavachDashboardService {

    List<KavachDashboard> getAllKavachDashboards();
}
