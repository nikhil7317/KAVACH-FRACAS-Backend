package com.railbit.tcasanalysis.service;

import com.railbit.tcasanalysis.entity.nmspackets.stationarypackets.StationaryPacket;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StationaryPacketService {
    StationaryPacket getStationaryPacketById(Long id);
    List<StationaryPacket> getAllStationaryPacket();
    List<StationaryPacket> get100StationaryPacket();
    String postStationaryPacket(StationaryPacket stationaryPacket);
    void updateStationaryPacket(StationaryPacket stationaryPacket);
    void deleteStationaryPacketById(Long id);
    int importByExcelSheet(MultipartFile excelSheet) throws Exception;
//    List<FaultCodeCountDTO>  getFaultCodeCountsByTime();
}
