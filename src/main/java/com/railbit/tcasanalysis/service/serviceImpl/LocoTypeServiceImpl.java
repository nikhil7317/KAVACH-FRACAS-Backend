package com.railbit.tcasanalysis.service.serviceImpl;


import com.railbit.tcasanalysis.entity.Firm;
import com.railbit.tcasanalysis.entity.loco.LocoType;
import com.railbit.tcasanalysis.repository.LocoTypeRepo;
import com.railbit.tcasanalysis.service.LocoTypeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class LocoTypeServiceImpl implements LocoTypeService {
    private final LocoTypeRepo locoTypeRepo;

    @Override
    public LocoType getLocoTypeByName(String name) {
        return locoTypeRepo.findByName(name);
    }

    @Override
    public LocoType getLocoTypeById(Integer id) {
        Optional<LocoType> data=locoTypeRepo.findById(id);
        if(data.isEmpty())
            throw new NoSuchElementException("LocoType not found");
        return data.get();
    }

    @Override
    public List<LocoType> getAllLocoType() {
        return locoTypeRepo.findAll();
    }

    @Override
    public List<String> getAllLocoTypeNames() {
        return getAllLocoType().stream()
                .map(LocoType::getName)
                .collect(Collectors.toList());
    }

    @Override
    public int postLocoType(LocoType locoType) {
       LocoType newLocoType = locoTypeRepo.save(locoType);
       return newLocoType.getId();
    }

    @Override
    public void updateLocoType(LocoType locoType) {
        locoTypeRepo.save(locoType);
    }

    @Override
    public void deleteLocoTypeById(Integer id) {
        locoTypeRepo.deleteById(id);
    }

    @Override
    public int importByExcelSheet(MultipartFile excelSheet) throws Exception {
        return 0;
    }
}
