package com.railbit.tcasanalysis.service.serviceImpl;

import com.railbit.tcasanalysis.DTO.FaultCodeCountDTO;
import com.railbit.tcasanalysis.entity.Station;
import com.railbit.tcasanalysis.entity.nmspackets.stationarypackets.StationaryPacket;
import com.railbit.tcasanalysis.repository.StationaryPacketRepo;
import com.railbit.tcasanalysis.service.StationService;
import com.railbit.tcasanalysis.service.StationaryPacketService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class StationaryPacketServiceImpl implements StationaryPacketService {

    private final  StationaryPacketRepo stationaryPacketRepo;
    private final StationService stationService;

    @Override
    public StationaryPacket getStationaryPacketById(Long id) {
        Optional<StationaryPacket> data=stationaryPacketRepo.findById(id);
        if(data.isEmpty())
            throw new NoSuchElementException("StationaryPacket not found");
        return data.get();
    }

    @Override
    public List<StationaryPacket> getAllStationaryPacket() {
        return stationaryPacketRepo.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    @Override
    public List<StationaryPacket> get100StationaryPacket() {
        return setStationData(stationaryPacketRepo.findTop100ByOrderByIdDesc());
    }

    public List<StationaryPacket> setStationData(List<StationaryPacket> stationaryPackets) {
        List<StationaryPacket> result = new ArrayList<>();
        for (StationaryPacket stationaryPacket : stationaryPackets) {
            String stnCode = stationaryPacket.getStnCode();
            Station station = null;
            if (!StringUtils.isEmpty(stnCode)) {
                station = stationService.findByTcassubsysid(Integer.valueOf(stnCode));
                stationaryPacket.setStation(station);
            }
            result.add(stationaryPacket);
        }
        return result;
    }

    @Override
    public String postStationaryPacket(StationaryPacket stationaryPacket) {
        Optional<StationaryPacket> optionalStationaryPacket = stationaryPacketRepo.findByHexData(stationaryPacket.getHexData());
        if (optionalStationaryPacket.isPresent()) {
            return "Packet Already Available";
        } else {
            stationaryPacketRepo.save(stationaryPacket);
        }
        return "Added Successfully";
    }

    @Override
    public void updateStationaryPacket(StationaryPacket stationaryPacket) {
        stationaryPacketRepo.save(stationaryPacket);
    }

    @Override
    public void deleteStationaryPacketById(Long id) {
        stationaryPacketRepo.deleteById(id);
    }

    @Override
    public int importByExcelSheet(MultipartFile excelSheet) throws Exception {
        return 0;
    }

//    @Override
//    public List<FaultCodeCountDTO> getFaultCodeCountsByTime() {
//        Pageable pageable = PageRequest.of(0, 5);
//        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusDays(1);
//        String formattedThirtyMinutesAgo = thirtyMinutesAgo.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
////        List<Object[]> faultCount = stationaryPacketRepo.findFaultCodeCountsByTime(formattedThirtyMinutesAgo,pageable);
//        return parseFaultCount(faultCount);
//    }

    private List<FaultCodeCountDTO> parseFaultCount(List<Object[]> faultCount) {
        List<FaultCodeCountDTO> result = new ArrayList<>();
        for (Object[] row : faultCount) {
            int code = (int) row[0];  // fault code
            String msg = (String) row[1];   // fault name
            int count = ((Number) row[2]).intValue(); // count

            FaultCodeCountDTO dto = new FaultCodeCountDTO(code, msg, count);
            result.add(dto);
        }
        return result;
    }
}
