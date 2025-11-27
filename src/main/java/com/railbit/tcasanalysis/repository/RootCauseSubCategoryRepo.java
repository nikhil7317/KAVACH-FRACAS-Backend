package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.RootCauseSubCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RootCauseSubCategoryRepo extends JpaRepository<RootCauseSubCategory,Integer> {
    List<RootCauseSubCategory> findByName(String name);


    List<RootCauseSubCategory> findByPossibleRootCauseId(Integer rootCauseId);

    RootCauseSubCategory findByNameAndPossibleRootCauseId(String name, Integer rootCauseId);
}
