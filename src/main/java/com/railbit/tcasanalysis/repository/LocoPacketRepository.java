package com.railbit.tcasanalysis.repository;


import com.railbit.tcasanalysis.locomodal.LocoPacket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface LocoPacketRepository extends JpaRepository<LocoPacket, Long> {

    // Date only (required)
    @Query("SELECT DISTINCT lp FROM LocoPacket lp " +
           "WHERE lp.atDate BETWEEN :fromDate AND :toDate " +
           "ORDER BY lp.atDate ASC")
    List<LocoPacket> findByDateRange(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate);

    // Date + locoId
    @Query("SELECT DISTINCT lp FROM LocoPacket lp " +
           "WHERE lp.atDate BETWEEN :fromDate AND :toDate " +
           "AND lp.locoId = :locoId " +
           "ORDER BY lp.atDate ASC")
    List<LocoPacket> findByDateRangeAndLocoId(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("locoId") Integer locoId);

    // Date + stnId (joins into access_request_packet for approaching_stn_id)
    @Query("SELECT DISTINCT lp FROM LocoPacket lp " +
           "LEFT JOIN lp.accessRequestPackets arp " +
           "WHERE lp.atDate BETWEEN :fromDate AND :toDate " +
           "AND arp.approachingStnId = :stnId " +
           "ORDER BY lp.atDate ASC")
    List<LocoPacket> findByDateRangeAndStnId(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("stnId") Integer stnId);

    // Date + locoId + stnId
    @Query("SELECT DISTINCT lp FROM LocoPacket lp " +
           "LEFT JOIN lp.accessRequestPackets arp " +
           "WHERE lp.atDate BETWEEN :fromDate AND :toDate " +
           "AND lp.locoId = :locoId " +
           "AND arp.approachingStnId = :stnId " +
           "ORDER BY lp.atDate ASC")
    List<LocoPacket> findByDateRangeAndLocoIdAndStnId(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("locoId") Integer locoId,
            @Param("stnId") Integer stnId);


}
