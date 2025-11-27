package com.railbit.tcasanalysis.service.serviceImpl;


import com.railbit.tcasanalysis.entity.RootCauseSubCategory;
import com.railbit.tcasanalysis.repository.RootCauseSubCategoryRepo;
import com.railbit.tcasanalysis.service.RootCauseSubCategoryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RootCauseSubCategoryServiceImpl implements RootCauseSubCategoryService {

    private final RootCauseSubCategoryRepo rootCauseSubCategoryRepo;

    @Override
    public RootCauseSubCategory getRootCauseSubCategoryById(Integer id) {
        Optional<RootCauseSubCategory> data=rootCauseSubCategoryRepo.findById(id);
        if(data.isEmpty())
            throw new NoSuchElementException("RootCauseSubCategory not found");
        return data.get();
    }
    @Override
    public List<RootCauseSubCategory> getRootCauseSubCategoryByName(String name) {
        return rootCauseSubCategoryRepo.findByName(name);
    }


    @Override
    public RootCauseSubCategory findByNameAndPossibleRootCauseId(String name, Integer rootCauseId) {
        return rootCauseSubCategoryRepo.findByNameAndPossibleRootCauseId(name, rootCauseId);
    }

    @Override
    public List<RootCauseSubCategory> getAllRootCauseSubCategorys() {
        return rootCauseSubCategoryRepo.findAll();
    }

    @Override
    public List<RootCauseSubCategory> getAllByRootCause(Integer rootCauseId) {
        return rootCauseSubCategoryRepo.findByPossibleRootCauseId(rootCauseId);
    }

    @Override
    public int postRootCauseSubCategory(RootCauseSubCategory rootCauseSubCategory) {
       RootCauseSubCategory newRootCauseSubCategory = rootCauseSubCategoryRepo.save(rootCauseSubCategory);
       return newRootCauseSubCategory.getId();
    }

    @Override
    public void updateRootCauseSubCategory(RootCauseSubCategory rootCauseSubCategory) {
        rootCauseSubCategoryRepo.save(rootCauseSubCategory);
    }

    @Override
    public void deleteRootCauseSubCategoryById(Integer id) {
        rootCauseSubCategoryRepo.deleteById(id);
    }

    @Override
    public int importByExcelSheet(MultipartFile excelSheet) throws Exception {
        return 0;
    }


}
