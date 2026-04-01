package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.StationHealthPacket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface StationHealthPacketRepository extends JpaRepository<StationHealthPacket, Long> {
 
    @Query("SELECT sp FROM StationHealthPacket sp " +
           "WHERE sp.atDate BETWEEN :fromDate AND :toDate " +
           "ORDER BY sp.atDate ASC")
    List<StationHealthPacket> findByDateRange(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate);
 
    @Query("SELECT sp FROM StationHealthPacket sp " +
           "WHERE sp.atDate BETWEEN :fromDate AND :toDate " +
           "AND sp.kavachId = :kavachId " +
           "ORDER BY sp.atDate ASC")
    List<StationHealthPacket> findByDateRangeAndKavachId(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("kavachId") Integer kavachId);
 
    // Find health packets that contain a specific event type
    @Query("SELECT DISTINCT sp FROM StationHealthPacket sp " +
           "LEFT JOIN sp.events ev " +
           "WHERE sp.atDate BETWEEN :fromDate AND :toDate " +
           "AND sp.kavachId = :kavachId " +
           "AND ev.eventId = :eventId " +
           "ORDER BY sp.atDate ASC")
    List<StationHealthPacket> findByDateRangeAndKavachIdAndEventId(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("kavachId") Integer kavachId,
            @Param("eventId") Integer eventId);
}
 