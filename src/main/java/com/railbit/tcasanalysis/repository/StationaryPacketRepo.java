package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.DTO.NMSStationStatusDTO;
import com.railbit.tcasanalysis.entity.nmspackets.FaultPacket;
import com.railbit.tcasanalysis.entity.nmspackets.stationarypackets.StationaryPacket;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StationaryPacketRepo extends JpaRepository<StationaryPacket,Long> {



    List<StationaryPacket> findTop100ByOrderByIdDesc();

    @Query(value = "SELECT * FROM stationary_packet WHERE hexData = :hexData LIMIT 1", nativeQuery = true)
    Optional<StationaryPacket> findByHexData(@Param("hexData") String hexData);

    @Query(value = """
        SELECT s.id AS stationId, s.division_id AS divisionId, 
               st.atDate AS atDate, st.stnCode AS stnCode, 
               st.srcPort AS srcPort, st.srcIp AS srcIp
        FROM station AS s
        LEFT JOIN (
            SELECT sp1.stnCode, MAX(sp1.atDate) AS max_date
            FROM stationary_packet sp1
            GROUP BY sp1.stnCode
        ) AS latest_packets
        ON s.tcas_subsys_id = latest_packets.stnCode
        LEFT JOIN stationary_packet AS st
        ON st.stnCode = latest_packets.stnCode AND st.atDate = latest_packets.max_date
        """, nativeQuery = true)
    List<Object[]> findLatestStationaryPackets();
}
