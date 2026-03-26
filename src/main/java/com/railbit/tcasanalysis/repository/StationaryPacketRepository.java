package com.railbit.tcasanalysis.repository;
import com.railbit.tcasanalysis.entity.StationaryPacket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface StationaryPacketRepository extends JpaRepository<StationaryPacket, Long> {

    // Date only (required)
    @Query("SELECT DISTINCT sp FROM StationaryPacket sp " +
           "WHERE sp.atDate BETWEEN :fromDate AND :toDate " +
           "ORDER BY sp.atDate ASC")
    List<StationaryPacket> findByDateRange(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate);

    // Date + stnCode
    @Query("SELECT DISTINCT sp FROM StationaryPacket sp " +
           "WHERE sp.atDate BETWEEN :fromDate AND :toDate " +
           "AND sp.stnCode = :stnCode " +
           "ORDER BY sp.atDate ASC")
    List<StationaryPacket> findByDateRangeAndStnCode(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("stnCode") Integer stnCode);

    // Date + locoId (joins into station_inner_packet for dest_loco_id)
    @Query("SELECT DISTINCT sp FROM StationaryPacket sp " +
           "LEFT JOIN sp.innerPackets ip " +
           "WHERE sp.atDate BETWEEN :fromDate AND :toDate " +
           "AND ip.destLocoId = :locoId " +
           "ORDER BY sp.atDate ASC")
    List<StationaryPacket> findByDateRangeAndLocoId(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("locoId") Integer locoId);

    // Date + stnCode + locoId
    @Query("SELECT DISTINCT sp FROM StationaryPacket sp " +
           "LEFT JOIN sp.innerPackets ip " +
           "WHERE sp.atDate BETWEEN :fromDate AND :toDate " +
           "AND sp.stnCode = :stnCode " +
           "AND ip.destLocoId = :locoId " +
           "ORDER BY sp.atDate ASC")
    List<StationaryPacket> findByDateRangeAndStnCodeAndLocoId(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("stnCode") Integer stnCode,
            @Param("locoId") Integer locoId);
}
