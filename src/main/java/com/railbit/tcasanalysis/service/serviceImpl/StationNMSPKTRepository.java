package com.railbit.tcasanalysis.service.serviceImpl;

import com.railbit.tcasanalysis.DTO.NMSStationStatusDTO;
import com.railbit.tcasanalysis.entity.Division;
import com.railbit.tcasanalysis.entity.Station;
import com.railbit.tcasanalysis.entity.nmspackets.stationarypackets.NMSStationStatus;
import com.railbit.tcasanalysis.repository.NMSStationStatusRepo;
import com.railbit.tcasanalysis.repository.StationRepo;
import com.railbit.tcasanalysis.repository.StationaryPacketRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class StationNMSPKTRepository {


    @Autowired
    StationaryPacketRepo stationaryPacketRepo;

    @Autowired
    NMSStationStatusRepo nmsStationStatusRepo;

    @Autowired
    StationRepo  stationRepo;

    public void findLatestStationaryPackets() {
        try {

            List<NMSStationStatus> existingRecords = nmsStationStatusRepo.findAll();
            List<NMSStationStatusDTO> newRecords = getLatestStationaryPackets();

            Map<Integer, NMSStationStatus> statusMap = existingRecords.stream()
                    .filter(status -> status.getStation() != null)
                    .collect(Collectors.toMap(status -> status.getStation().getId(), Function.identity()));

            List<NMSStationStatus> updatedRecords = newRecords.stream()
                    .map(dto -> {
                        NMSStationStatus existingStatus = statusMap.get(dto.getStationId());
                        if (existingStatus != null) {
                            existingStatus.setAtDate(dto.getAtDate()==null?existingStatus.getAtDate():dto.getAtDate());
                            existingStatus.setSrcIp(dto.getSrcIp());
                            existingStatus.setSrcPort(dto.getSrcPort());
                            existingStatus.setStatus(getOnlineStatus(dto.getAtDate()));
                            return existingStatus;
                        } else {
                            Optional<Station> station=null;
                            if(dto.getStationId()!=null) {
                                station = stationRepo.findById(dto.getStationId());
                            }
                            if (station.isEmpty()) {
                                throw new RuntimeException("Station not found for " + dto.getStnCode());
                            }
                            Division division = station.get().getDivision(); // Get associated Division

                            return new NMSStationStatus(
                                    dto.getAtDate(),
                                    dto.getStnCode(),
                                    dto.getSrcIp(),
                                    dto.getSrcPort(),
                                    station.get(),
                                    division,
                                    getOnlineStatus(dto.getAtDate())
                            );
                        }
                    })
                    .collect(Collectors.toList());

            nmsStationStatusRepo.saveAll(updatedRecords);

//            System.out.println("Upsert completed: " + updatedRecords.size() + " records processed.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public List<NMSStationStatusDTO> getLatestStationaryPackets() {
        List<Object[]> results = stationaryPacketRepo.findLatestStationaryPackets();

        return results.stream()
                .map(row -> new NMSStationStatusDTO(
                        (Integer) row[0], // stationId
                        (Integer) row[1], // divisionId
                        (Date) row[2],    // atDate
                        (String) row[3],  // stnCode
                        (String) row[4],  // srcPort
                        (String) row[5]   // srcIp
                ))
                .collect(Collectors.toList());
    }

    private String getOnlineStatus(Date atDate) {
        if (atDate == null) {
            return "OFFLINE";
        }

        Instant atDateInstant = atDate.toInstant();
        Instant fiveMinutesAgo = Instant.now().minusSeconds(6 * 60);

        return atDateInstant.isAfter(fiveMinutesAgo) ? "ONLINE" : "OFFLINE";
    }
}