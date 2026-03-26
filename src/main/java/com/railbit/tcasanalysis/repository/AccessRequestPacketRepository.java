package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.locomodal.AccessRequestPacket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessRequestPacketRepository extends JpaRepository<AccessRequestPacket, Long> {
}
