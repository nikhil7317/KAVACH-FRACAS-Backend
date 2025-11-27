package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.Role;
import com.railbit.tcasanalysis.entity.loco.Loco;
import com.railbit.tcasanalysis.entity.loco.Shed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LocoRepo extends JpaRepository<Loco,Integer> {
    Loco findByLocoNo(String locoNo);
    Loco findByNmsLocoId(String locoNo);

    @Query("SELECT l FROM loco l " +
            "WHERE (:searchTerm IS NULL OR LOWER(l.locoNo) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "AND (:locoType IS NULL OR l.locoType.id = :locoType) " +
            "AND (:firm IS NULL OR l.firm.id = :firm) " +
            "AND (:shed IS NULL OR l.shed.id = :shed) " +
            "AND (:month IS NULL OR LOWER(l.month) = LOWER(:month)) " +
            "AND (:version IS NULL OR LOWER(l.version) = LOWER(:version))")
    Page<Loco> searchLocos(
            @Param("searchTerm") String searchTerm,
            @Param("locoType") Integer locoType,
            @Param("firm") Integer firm,
            @Param("shed") Integer shed,
            @Param("month") String month,
            @Param("version") String version,
            Pageable pageable);

    @Query("SELECT DISTINCT l.version FROM loco l")
    List<String> findDistinctVersions();

}
