package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.AutoTicketConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface AutoTicketConfigRepository extends JpaRepository<AutoTicketConfig, Long> {

    /** Fetch the single active config row. */
    Optional<AutoTicketConfig> findByIsActiveTrue();

    /** Deactivate ALL config rows — called before saving a new one. */
    @Modifying
    @Transactional
    @Query("UPDATE AutoTicketConfig c SET c.isActive = false")
    void deactivateAll();
}