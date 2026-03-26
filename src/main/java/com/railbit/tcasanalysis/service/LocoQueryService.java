package com.railbit.tcasanalysis.service;


import com.railbit.tcasanalysis.locomodal.LocoPacket;
import com.railbit.tcasanalysis.repository.LocoPacketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class LocoQueryService {

    private static final Logger logger = LoggerFactory.getLogger(LocoQueryService.class);

    @Autowired
    private LocoPacketRepository locoPacketRepository;

    /**
     * Find loco packets with filters.
     *
     * @param fromDate required - start date/time
     * @param toDate   required - end date/time
     * @param locoId   optional - filter by SOURCE_LOCO_ID
     * @param stnId    optional - filter by APPROACHING_STN_ID from Access Request
     */
    public List<LocoPacket> findPackets(Date fromDate, Date toDate, Integer locoId, Integer stnId) {

        logger.info("Query: from={}, to={}, locoId={}, stnId={}", fromDate, toDate, locoId, stnId);

        if (locoId != null && stnId != null) {
            return locoPacketRepository.findByDateRangeAndLocoIdAndStnId(fromDate, toDate, locoId, stnId);
        } else if (locoId != null) {
            return locoPacketRepository.findByDateRangeAndLocoId(fromDate, toDate, locoId);
        } else if (stnId != null) {
            return locoPacketRepository.findByDateRangeAndStnId(fromDate, toDate, stnId);
        } else {
            return locoPacketRepository.findByDateRange(fromDate, toDate);
        }
    }
}
