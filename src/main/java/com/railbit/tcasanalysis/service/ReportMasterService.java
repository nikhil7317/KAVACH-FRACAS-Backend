package com.railbit.tcasanalysis.service;

import com.railbit.tcasanalysis.entity.ReportMaster;
import com.railbit.tcasanalysis.repository.ReportMasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportMasterService {

    private final ReportMasterRepository repository;

    public List<ReportMaster> getAllReportMasters() {
        return repository.findAll()
                .stream()
                .sorted(Comparator.comparing(ReportMaster::getId))
                .toList();
    }
}