package com.railbit.tcasanalysis.service;

import com.railbit.tcasanalysis.entity.ReportSubChildStn;
import com.railbit.tcasanalysis.repository.ReportSubChildStnRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportSubChildStnService {

    private final ReportSubChildStnRepository repository;

    public List<ReportSubChildStn> getByChildId(Integer childId) {
        return repository.findByReportMasterChildId(childId);
    }
}