package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetRepo extends JpaRepository<Asset,Long> {
}
