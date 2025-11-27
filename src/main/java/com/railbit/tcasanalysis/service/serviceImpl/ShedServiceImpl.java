package com.railbit.tcasanalysis.service.serviceImpl;


import com.railbit.tcasanalysis.entity.loco.Shed;
import com.railbit.tcasanalysis.repository.ShedRepo;
import com.railbit.tcasanalysis.service.ShedService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ShedServiceImpl implements ShedService {
    private final ShedRepo shedRepo;

    @Override
    public Shed getShedByName(String name) {
        System.out.println(name);
        System.out.println(shedRepo.findByName(name));
        return shedRepo.findByName(name);
    }

    @Override
    public Shed getShedById(Integer id) {
        Optional<Shed> data=shedRepo.findById(id);
        if(data.isEmpty())
            throw new NoSuchElementException("Shed not found");
        return data.get();
    }

    @Override
    public List<Shed> getAllShed() {
        return shedRepo.findAll();
    }

    @Override
    public int postShed(Shed shed) {
       Shed newShed = shedRepo.save(shed);
       return newShed.getId();
    }

    @Override
    public void updateShed(Shed shed) {
        shedRepo.save(shed);
    }

    @Override
    public void deleteShedById(Integer id) {
        shedRepo.deleteById(id);
    }

    @Override
    public int importByExcelSheet(MultipartFile excelSheet) throws Exception {
        return 0;
    }
}
