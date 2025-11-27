package com.railbit.tcasanalysis.service;



import com.railbit.tcasanalysis.entity.Asset;
import com.railbit.tcasanalysis.entity.analysis.StringAndCount;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DashboardService {
    List<StringAndCount> getMajorIncidentsStationWise(String filter);
    List<StringAndCount> getMajorIncidentsLocoWise(String filter);
    List<StringAndCount> getMajorIncidentsCauseWise(String filter);
    List<StringAndCount> getMajorIncidentsDivisionWise(String filter);
    List<StringAndCount> getMajorIncidentsOEMWise(String filter);
}
