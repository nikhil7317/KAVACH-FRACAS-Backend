package com.railbit.tcasanalysis.service;

import com.railbit.tcasanalysis.entity.nmspackets.stationarypackets.NMSStationStatus;
import com.railbit.tcasanalysis.repository.NMSStationStatusRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class NMSStationStatusService {

    private final NMSStationStatusRepo nmsStationStatusRepo;

    // Create
    @Transactional
    public NMSStationStatus create(NMSStationStatus nmsStationStatus) {
        return nmsStationStatusRepo.save(nmsStationStatus);
    }

    // Read operations
    public List<NMSStationStatus> findAll() {
        return nmsStationStatusRepo.findAll();
    }

    public Page<NMSStationStatus> findAll(Pageable pageable) {
        return nmsStationStatusRepo.findAll(pageable);
    }

    public NMSStationStatus findById(Long id) {
        return nmsStationStatusRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("NMSStationStatus not found with id: " + id));
    }

    // Update
    @Transactional
    public NMSStationStatus update(Long id, NMSStationStatus nmsStationStatus) {
        if (!nmsStationStatusRepo.existsById(id)) {
            throw new EntityNotFoundException("NMSStationStatus not found with id: " + id);
        }
        nmsStationStatus.setId(id);
        return nmsStationStatusRepo.save(nmsStationStatus);
    }

    // Delete
    @Transactional
    public void delete(Long id) {
        if (!nmsStationStatusRepo.existsById(id)) {
            throw new EntityNotFoundException("NMSStationStatus not found with id: " + id);
        }
        nmsStationStatusRepo.deleteById(id);
    }

    // Bulk create
    @Transactional
    public List<NMSStationStatus> createAll(List<NMSStationStatus> nmsStationStatuses) {
        return nmsStationStatusRepo.saveAll(nmsStationStatuses);
    }

    // Check if exists
    public boolean existsById(Long id) {
        return nmsStationStatusRepo.existsById(id);
    }



}