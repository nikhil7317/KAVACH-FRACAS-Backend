package com.railbit.tcasanalysis.service.serviceImpl;


import com.railbit.tcasanalysis.entity.Section;
import com.railbit.tcasanalysis.repository.SectionRepo;
import com.railbit.tcasanalysis.service.SectionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SectionServiceImpl implements SectionService {
    private final SectionRepo sectionRepo;

    @Override
    public Section getSectionByName(String name) {
//        System.out.println(name);
//        System.out.println(sectionRepo.findByName(name));
        return sectionRepo.findByName(name);
    }

    @Override
    public Section getSectionById(Integer id) {
        Optional<Section> data=sectionRepo.findById(id);
        if(data.isEmpty())
            throw new NoSuchElementException("Section not found");
        return data.get();
    }

    @Override
    public List<Section> getAllSection() {
        return sectionRepo.findAll();
    }

    @Override
    public List<Section> getAllSectionByDivisionId(Integer divisionId) {
        return sectionRepo.findByDivisionId(divisionId);
    }

    @Override
    public Section postSection(Section section) {
        return sectionRepo.save(section);
    }

    @Override
    public void updateSection(Section section) {
        sectionRepo.save(section);
    }

    @Override
    public void deleteSectionById(Integer id) {
        sectionRepo.deleteById(id);
    }

    @Override
    public int importByExcelSheet(MultipartFile excelSheet) throws Exception {
        return 0;
    }
}
