package com.railbit.tcasanalysis.repository;


import com.railbit.tcasanalysis.entity.KavachFaultPacket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface KavachFaultPacketRepository extends JpaRepository<KavachFaultPacket, Long> {

    @Query("SELECT fp FROM KavachFaultPacket fp " +
           "WHERE fp.atDate BETWEEN :fromDate AND :toDate " +
           "ORDER BY fp.atDate DESC")
    List<KavachFaultPacket> findByDateRange(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate);

    @Query("SELECT fp FROM KavachFaultPacket fp " +
           "WHERE fp.atDate BETWEEN :fromDate AND :toDate " +
           "AND fp.kavachSubsystemId = :subsystemId " +
           "ORDER BY fp.atDate DESC")
    List<KavachFaultPacket> findByDateRangeAndSubsystemId(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("subsystemId") Integer subsystemId);

    @Query("SELECT fp FROM KavachFaultPacket fp " +
           "WHERE fp.atDate BETWEEN :fromDate AND :toDate " +
           "AND fp.subsystemType = :subsystemType " +
           "ORDER BY fp.atDate DESC")
    List<KavachFaultPacket> findByDateRangeAndSubsystemType(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("subsystemType") Integer subsystemType);

    @Query("SELECT fp FROM KavachFaultPacket fp " +
           "WHERE fp.atDate BETWEEN :fromDate AND :toDate " +
           "AND (:subsystemId IS NULL OR fp.kavachSubsystemId = :subsystemId) " +
           "AND (:subsystemType IS NULL OR fp.subsystemType = :subsystemType) " +
           "ORDER BY fp.atDate DESC")
    List<KavachFaultPacket> findByFilters(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("subsystemId") Integer subsystemId,
            @Param("subsystemType") Integer subsystemType);
}
