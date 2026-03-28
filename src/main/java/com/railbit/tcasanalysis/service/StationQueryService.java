package com.railbit.tcasanalysis.service;

import com.railbit.tcasanalysis.entity.StationaryPacket;
import com.railbit.tcasanalysis.repository.StationaryPacketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationQueryService {

    private static final Logger logger = LoggerFactory.getLogger(StationQueryService.class);
    private static final int MAX_RESULTS = 100;

    @Autowired
    private StationaryPacketRepository stationaryPacketRepository;

    public List<StationaryPacket> findPackets(Date fromDate, Date toDate, Integer stnCode, Integer locoId) {

        logger.info("Query: from={}, to={}, stnCode={}, locoId={}", fromDate, toDate, stnCode, locoId);

        List<StationaryPacket> results;

        if (stnCode != null && locoId != null) {
            results = stationaryPacketRepository.findByDateRangeAndStnCodeAndLocoId(fromDate, toDate, stnCode, locoId);
        } else if (stnCode != null) {
            results = stationaryPacketRepository.findByDateRangeAndStnCode(fromDate, toDate, stnCode);
        } else if (locoId != null) {
            results = stationaryPacketRepository.findByDateRangeAndLocoId(fromDate, toDate, locoId);
        } else {
            results = stationaryPacketRepository.findByDateRange(fromDate, toDate);
        }

        // Limit to latest 100 records (already sorted DESC by date in query)
        return results.stream()
                .limit(MAX_RESULTS)
                .collect(Collectors.toList());
    }
}