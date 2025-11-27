package com.railbit.tcasanalysis.service;

import com.railbit.tcasanalysis.DTO.FaultCodeCountDTO;
import com.railbit.tcasanalysis.DTO.FaultPacketDTO;
import com.railbit.tcasanalysis.entity.nmspackets.FaultPacket;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface FaultPacketService {
    FaultPacket getFaultPacketById(Long id);
    String postFaultPacket(FaultPacketDTO faultPacket);
    void updateFaultPacket(FaultPacket faultPacket);
    void deleteFaultPacketById(Long id);
    int importByExcelSheet(MultipartFile excelSheet) throws Exception;
    List<FaultCodeCountDTO>  getFaultCodeCountsByTime();
    Map<String, Object> getAllFaultPacketData(Integer page, Integer intRecord, LocalDateTime fromDate, LocalDateTime toDate);
}
