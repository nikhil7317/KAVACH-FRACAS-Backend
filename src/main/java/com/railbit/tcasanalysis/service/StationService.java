package com.railbit.tcasanalysis.service;


import com.railbit.tcasanalysis.entity.Station;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StationService {
    Station getStationById(Integer id);
    Station getStationByName(String name);
    Station getStationByCode(String code);
    Station findByTcassubsysid(Integer id);
    List<Station> getAllStations();
//    List<Station> getAllStationsBySection(Integer sectionId);
    List<Station> getAllStationsByDivision(Integer divisionId);
    List<Station> getAllStationsByZone(Integer zoneId);
    int postStation(Station station);
    void updateStation(Station station);
    void deleteStationById(Integer id);
    int importByExcelSheet(MultipartFile excelSheet) throws Exception;
}
