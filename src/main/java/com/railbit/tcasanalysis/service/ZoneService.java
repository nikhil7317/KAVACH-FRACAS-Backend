package com.railbit.tcasanalysis.service;

import com.railbit.tcasanalysis.entity.Zone;
import com.railbit.tcasanalysis.entity.Zone;
import com.railbit.tcasanalysis.repository.ZoneRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ZoneService {

    private final ZoneRepo zoneRepo;
    
    public List<Zone> getAllZones() {
        return zoneRepo.findAll();
    }

    public Zone getZoneByName(String name) {
        return zoneRepo.findByName(name);
    }

    public Zone getZoneByCode(String code) {
        return zoneRepo.findByCode(code);
    }

    public Zone getZoneById(Integer id) {
        Optional<Zone> data=zoneRepo.findById(id);
        if(data.isEmpty())
            throw new NoSuchElementException("Zone not found");
        return data.get();
    }

    public int postZone(Zone zone) {
        Zone newZone = zoneRepo.save(zone);
        return newZone.getId();
    }

    public void updateZone(Zone zone) {
        zoneRepo.save(zone);
    }

    public void deleteZoneById(Integer id) {
        zoneRepo.deleteById(id);
    }

    public int importByExcelSheet(MultipartFile excelSheet) throws Exception {
        return 0;
    }
    
}
