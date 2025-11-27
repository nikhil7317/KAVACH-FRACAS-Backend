package com.railbit.tcasanalysis.service.serviceImpl;


import com.railbit.tcasanalysis.entity.Designation;
import com.railbit.tcasanalysis.repository.DesignationRepo;
import com.railbit.tcasanalysis.service.DesignationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DesignationServiceImpl implements DesignationService {
    private final DesignationRepo designationRepo;

    @Override
    public Designation getDesignationByName(String name) {
//        System.out.println(name);
//        System.out.println(designationRepo.findByName(name));
        return designationRepo.findByName(name);
    }

    @Override
    public Designation getDesignationById(Integer id) {
        Optional<Designation> data=designationRepo.findById(id);
        if(data.isEmpty())
            throw new NoSuchElementException("Designation not found");
        return data.get();
    }

    @Override
    public List<Designation> getAllDesignation() {
        return designationRepo.findAll();
    }

    @Override
    public Designation postDesignation(Designation designation) {
        return designationRepo.save(designation);
    }

    @Override
    public void updateDesignation(Designation designation) {
        designationRepo.save(designation);
    }

    @Override
    public void deleteDesignationById(Integer id) {
        designationRepo.deleteById(id);
    }

    @Override
    public int importByExcelSheet(MultipartFile excelSheet) throws Exception {
        return 0;
    }
}
