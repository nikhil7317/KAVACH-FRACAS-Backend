package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.Designation;
import com.railbit.tcasanalysis.entity.IssueCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueCategoryRepo extends JpaRepository<IssueCategory,Integer> {
    IssueCategory findByName(String name);
}
