package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.PossibleIssue;
import com.railbit.tcasanalysis.entity.PossibleRootCause;
import com.railbit.tcasanalysis.entity.PossibleRootCause;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PossibleRootCauseRepo extends JpaRepository<PossibleRootCause,Integer> {
    List<PossibleRootCause> findByProjectTypeId(Integer projectTypeId);
    PossibleRootCause findByName(String name);
}
