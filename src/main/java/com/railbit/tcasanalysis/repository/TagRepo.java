package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.Tag;
import com.railbit.tcasanalysis.entity.Tcas;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepo extends JpaRepository<Tag,Long> {

}
