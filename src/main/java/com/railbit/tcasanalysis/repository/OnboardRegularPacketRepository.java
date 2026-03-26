package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.locomodal.OnboardRegularPacket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OnboardRegularPacketRepository extends JpaRepository<OnboardRegularPacket, Long> {
}