package com.railbit.tcasanalysis.service.serviceImpl;


import com.railbit.tcasanalysis.entity.Division;
import com.railbit.tcasanalysis.entity.Station;
import com.railbit.tcasanalysis.repository.DivisionRepo;
import com.railbit.tcasanalysis.service.DivisionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DivisionServiceImpl implements DivisionService {
    private final DivisionRepo divisionRepo;


    @Override
    public List<Division> getAllDivisionByZone(Integer zoneId) {
        return divisionRepo.findByZoneId(zoneId);
    }

    @Override
    public Division getDivisionByName(String name) {
        return divisionRepo.findByName(name);
    }

    @Override
    public Division getDivisionByCode(String code) {
        return divisionRepo.findByCode(code);
    }

    @Override
    public Division getDivisionById(Integer id) {
        Optional<Division> data=divisionRepo.findById(id);
        if(data.isEmpty())
            throw new NoSuchElementException("Division not found");
        return data.get();
    }

    @Override
    public List<Division> getAllDivision() {
        return divisionRepo.findAll();
    }

    @Override
    public int postDivision(Division division) {
       Division newDivision = divisionRepo.save(division);
       return newDivision.getId();
    }

    @Override
    public void updateDivision(Division division) {
        divisionRepo.save(division);
    }

    @Override
    public void deleteDivisionById(Integer id) {
        divisionRepo.deleteById(id);
    }

    @Override
    public int importByExcelSheet(MultipartFile excelSheet) throws Exception {
        return 0;
    }
}
