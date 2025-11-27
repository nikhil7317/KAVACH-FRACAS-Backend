package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.Designation;
import com.railbit.tcasanalysis.entity.ProjectType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectTypeRepo extends JpaRepository<ProjectType,Integer> {
    ProjectType findByName(String name);
}
