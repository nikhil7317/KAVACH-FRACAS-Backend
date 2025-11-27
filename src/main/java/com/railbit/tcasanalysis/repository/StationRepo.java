package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.Station;
import com.railbit.tcasanalysis.entity.User;
import com.railbit.tcasanalysis.entity.loco.Loco;
import com.railbit.tcasanalysis.entity.loco.Shed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StationRepo extends JpaRepository<Station,Integer> {

    Station findByTcassubsysid(Integer id);
    Station findByName(String name);
    Station findByCode(String code);
//    List<Station> findBySectionId(Integer sectionId);
//    @Query("SELECT s FROM station s JOIN s.section sec JOIN sec.division div WHERE div.id = :divisionId")
//    List<Station> findByDivisionId(@Param("divisionId") Integer divisionId);
    List<Station> findByDivisionId(Integer divisionId);
    List<Station> findByDivision_Zone_Id(Integer zoneId);

}
