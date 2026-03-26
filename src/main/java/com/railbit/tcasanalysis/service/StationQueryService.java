package com.railbit.tcasanalysis.service;

import com.railbit.tcasanalysis.entity.StationaryPacket;
import com.railbit.tcasanalysis.repository.StationaryPacketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class StationQueryService {

    private static final Logger logger = LoggerFactory.getLogger(StationQueryService.class);

    @Autowired
    private StationaryPacketRepository stationaryPacketRepository;

    /**
     * Find station packets with filters.
     *
     * @param fromDate required - start date/time
     * @param toDate   required - end date/time
     * @param stnCode  optional - filter by SOURCE_STN_ILC_IBS_ID
     * @param locoId   optional - filter by DEST_LOCO_ID from inner packet
     */
    public List<StationaryPacket> findPackets(Date fromDate, Date toDate, Integer stnCode, Integer locoId) {

        logger.info("Query: from={}, to={}, stnCode={}, locoId={}", fromDate, toDate, stnCode, locoId);

        if (stnCode != null && locoId != null) {
            return stationaryPacketRepository.findByDateRangeAndStnCodeAndLocoId(fromDate, toDate, stnCode, locoId);
        } else if (stnCode != null) {
            return stationaryPacketRepository.findByDateRangeAndStnCode(fromDate, toDate, stnCode);
        } else if (locoId != null) {
            return stationaryPacketRepository.findByDateRangeAndLocoId(fromDate, toDate, locoId);
        } else {
            return stationaryPacketRepository.findByDateRange(fromDate, toDate);
        }
    }
}
