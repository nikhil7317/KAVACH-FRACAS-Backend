package com.railbit.tcasanalysis.service.serviceImpl;


import com.railbit.tcasanalysis.entity.Firm;
import com.railbit.tcasanalysis.entity.loco.Loco;
import com.railbit.tcasanalysis.repository.FirmRepo;
import com.railbit.tcasanalysis.service.FirmService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FirmServiceImpl implements FirmService {
    private final FirmRepo firmRepo;

    @Override
    public Firm getFirmByName(String name) {
        return firmRepo.findByName(name);
    }

    @Override
    public Firm getFirmById(Integer id) {
        Optional<Firm> data=firmRepo.findById(id);
        if(data.isEmpty())
            throw new NoSuchElementException("Firm not found");
        return data.get();
    }

    @Override
    public List<Firm> getAllFirm() {
        return firmRepo.findAll();
    }

    @Override
    public List<String> getAllFirmNames() {
        return getAllFirm().stream()
                .map(Firm::getName)
                .collect(Collectors.toList());
    }

    @Override
    public int postFirm(Firm firm) {
       Firm newFirm = firmRepo.save(firm);
       return newFirm.getId();
    }

    @Override
    public void updateFirm(Firm firm) {
        firmRepo.save(firm);
    }

    @Override
    public void deleteFirmById(Integer id) {
        firmRepo.deleteById(id);
    }

    @Override
    public int importByExcelSheet(MultipartFile excelSheet) throws Exception {
        return 0;
    }
}
