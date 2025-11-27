package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.Division;
import com.railbit.tcasanalysis.entity.cmsabn.CMSAbn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CMSAbnRepo extends JpaRepository<CMSAbn,Long> {
    boolean existsByAbnId(String abnId);
}
