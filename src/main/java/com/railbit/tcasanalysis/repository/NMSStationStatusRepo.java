package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.Notification;
import com.railbit.tcasanalysis.entity.nmspackets.stationarypackets.NMSStationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NMSStationStatusRepo extends JpaRepository<NMSStationStatus,Long> {
}
