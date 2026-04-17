package com.railbit.tcasanalysis.service;


import com.railbit.tcasanalysis.entity.Severity;
import com.railbit.tcasanalysis.repository.SeverityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class SeverityService {

    private final SeverityRepository severityRepository;

    // GET ALL — used by frontend to populate severity dropdown
    public List<Severity> getAllSeverities() {
        return severityRepository.findAll();
    }
}