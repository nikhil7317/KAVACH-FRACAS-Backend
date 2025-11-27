package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.nmspackets.FaultPacket;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import com.railbit.tcasanalysis.DTO.FaultPacketDTO;

public interface FaultPacketRepo extends JpaRepository<FaultPacket,Long> {

    @Query("SELECT fp.faultCode, f.faultName, COUNT(fp) AS count " +
            "FROM FaultPacket fp " +
            "JOIN FaultCode f ON f.id = fp.faultCode.id " +
            "WHERE CONCAT(fp.date, ' ', fp.time) >= :time " +
            "GROUP BY fp.faultCode, f.faultName " +
            "ORDER BY count DESC")
    List<Object[]> findFaultCodeCountsBytime(@Param("time") String time, Pageable pageable);

    Optional<FaultPacket> findByHexValue(String hexValue);

}
