package com.railbit.tcasanalysis.service.serviceImpl;


import com.railbit.tcasanalysis.entity.PossibleIssue;
import com.railbit.tcasanalysis.entity.Station;
import com.railbit.tcasanalysis.repository.PossibleIssueRepo;
import com.railbit.tcasanalysis.service.PossibleIssueService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PossibleIssueServiceImpl implements PossibleIssueService {

    private final PossibleIssueRepo possibleIssueRepo;

    @Override
    public PossibleIssue getPossibleIssueById(Integer id) {
        Optional<PossibleIssue> data=possibleIssueRepo.findById(id);
        if(data.isEmpty())
            throw new NoSuchElementException("PossibleIssue not found");
        return data.get();
    }
    @Override
    public PossibleIssue getPossibleIssueByName(String name) {
        return possibleIssueRepo.findByName(name);
    }
    @Override
    public PossibleIssue getPossibleIssueByNameAndIssueCategoryId(String name,Integer issueCategoryId) {
        return possibleIssueRepo.findByNameAndIssueCategoryId(name,issueCategoryId);
    }
    @Override
    public List<PossibleIssue> getAllPossibleIssues() {
        return possibleIssueRepo.findAll();
    }

    @Override
    public List<PossibleIssue> getAllPossibleIssueByIssueCategory(Integer issueCategoryId) {
        return possibleIssueRepo.findByIssueCategoryId(issueCategoryId);
    }

    @Override
    public List<PossibleIssue> getAllPossibleIssuesByProjectType(Integer projectTypeId) {
        return possibleIssueRepo.findByProjectTypeId(projectTypeId);
    }

    @Override
    public int postPossibleIssue(PossibleIssue possibleIssue) {
       PossibleIssue newPossibleIssue = possibleIssueRepo.save(possibleIssue);
       return newPossibleIssue.getId();
    }

    @Override
    public void updatePossibleIssue(PossibleIssue possibleIssue) {
        possibleIssueRepo.save(possibleIssue);
    }

    @Override
    public void deletePossibleIssueById(Integer id) {
        possibleIssueRepo.deleteById(id);
    }

    @Override
    public int importByExcelSheet(MultipartFile excelSheet) throws Exception {
        return 0;
    }
}
