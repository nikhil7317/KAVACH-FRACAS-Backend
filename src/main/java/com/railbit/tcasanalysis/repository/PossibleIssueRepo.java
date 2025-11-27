package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.PossibleIssue;
import com.railbit.tcasanalysis.entity.RootCauseSubCategory;
import com.railbit.tcasanalysis.entity.Station;
import com.railbit.tcasanalysis.entity.User;
import com.railbit.tcasanalysis.entity.loco.Shed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PossibleIssueRepo extends JpaRepository<PossibleIssue,Integer> {
    List<PossibleIssue> findByIssueCategoryId(Integer issueCategoryId);
    List<PossibleIssue> findByProjectTypeId(Integer projectTypeId);
    PossibleIssue findByName(String name);
    PossibleIssue findByNameAndIssueCategoryId(String name, Integer issueCategoryId);
}
