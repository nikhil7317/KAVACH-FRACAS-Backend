package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.Division;
import com.railbit.tcasanalysis.entity.Section;
import com.railbit.tcasanalysis.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectionRepo extends JpaRepository<Section,Integer> {
    Section findByName(String name);
    List<Section> findByDivisionId(Integer divisionId);
}
