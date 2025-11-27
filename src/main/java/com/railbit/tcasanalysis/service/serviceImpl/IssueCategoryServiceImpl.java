package com.railbit.tcasanalysis.service.serviceImpl;


import com.railbit.tcasanalysis.entity.IssueCategory;
import com.railbit.tcasanalysis.repository.IssueCategoryRepo;
import com.railbit.tcasanalysis.service.IssueCategoryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class IssueCategoryServiceImpl implements IssueCategoryService {
    private final IssueCategoryRepo issueCategoryRepo;

    @Override
    public IssueCategory getIssueCategoryByName(String name) {
        System.out.println(name);
        System.out.println(issueCategoryRepo.findByName(name));
        return issueCategoryRepo.findByName(name);
    }

    @Override
    public IssueCategory getIssueCategoryById(Integer id) {
        Optional<IssueCategory> data=issueCategoryRepo.findById(id);
        if(data.isEmpty())
            throw new NoSuchElementException("IssueCategory not found");
        return data.get();
    }

    @Override
    public List<IssueCategory> getAllIssueCategory() {
        return issueCategoryRepo.findAll();
    }

    @Override
    public int postIssueCategory(IssueCategory issueCategory) {
       IssueCategory newIssueCategory = issueCategoryRepo.save(issueCategory);
       return newIssueCategory.getId();
    }

    @Override
    public void updateIssueCategory(IssueCategory issueCategory) {
        issueCategoryRepo.save(issueCategory);
    }

    @Override
    public void deleteIssueCategoryById(Integer id) {
        issueCategoryRepo.deleteById(id);
    }

    @Override
    public int importByExcelSheet(MultipartFile excelSheet) throws Exception {
        return 0;
    }
}
