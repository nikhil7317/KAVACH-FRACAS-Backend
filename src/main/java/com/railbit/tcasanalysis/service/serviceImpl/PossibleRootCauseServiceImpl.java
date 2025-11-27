package com.railbit.tcasanalysis.service.serviceImpl;


import com.railbit.tcasanalysis.entity.PossibleIssue;
import com.railbit.tcasanalysis.entity.PossibleRootCause;
import com.railbit.tcasanalysis.repository.PossibleRootCauseRepo;
import com.railbit.tcasanalysis.service.PossibleRootCauseService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PossibleRootCauseServiceImpl implements PossibleRootCauseService {

    private final PossibleRootCauseRepo possibleRootCauseRepo;

    @Override
    public PossibleRootCause getPossibleRootCauseById(Integer id) {
        Optional<PossibleRootCause> data=possibleRootCauseRepo.findById(id);
        if(data.isEmpty())
            throw new NoSuchElementException("PossibleRootCause not found");
        return data.get();
    }
    @Override
    public PossibleRootCause getPossibleRootCauseByName(String name) {
        return possibleRootCauseRepo.findByName(name);
    }
    @Override
    public List<PossibleRootCause> getAllPossibleRootCauses() {
        return possibleRootCauseRepo.findAll();
    }

    @Override
    public List<PossibleRootCause> getAllPossibleRootCausesByProjectType(Integer projectTypeId) {
        return possibleRootCauseRepo.findByProjectTypeId(projectTypeId);
    }

    @Override
    public int postPossibleRootCause(PossibleRootCause possibleRootCause) {
       PossibleRootCause newPossibleRootCause = possibleRootCauseRepo.save(possibleRootCause);
       return newPossibleRootCause.getId();
    }

    @Override
    public void updatePossibleRootCause(PossibleRootCause possibleRootCause) {
        possibleRootCauseRepo.save(possibleRootCause);
    }

    @Override
    public void deletePossibleRootCauseById(Integer id) {
        possibleRootCauseRepo.deleteById(id);
    }

    @Override
    public int importByExcelSheet(MultipartFile excelSheet) throws Exception {
        return 0;
    }
}
