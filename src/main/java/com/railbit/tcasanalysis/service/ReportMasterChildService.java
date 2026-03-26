package com.railbit.tcasanalysis.service;

import com.railbit.tcasanalysis.entity.ReportMasterChild;
import com.railbit.tcasanalysis.repository.ReportMasterChildRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportMasterChildService {

    private final ReportMasterChildRepository repository;

    public List<ReportMasterChild> getByReportMasterId(Integer reportMasterId) {
        return repository.findByReportMasterId(reportMasterId);
    }
}