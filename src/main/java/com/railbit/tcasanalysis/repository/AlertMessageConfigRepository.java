package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.AlertMessageConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlertMessageConfigRepository
        extends JpaRepository<AlertMessageConfig, Long> {

    /** All configs, ordered for UI display. */
    List<AlertMessageConfig> findAllByOrderByAlertCategoryAscAlertMessageAsc();

    /** Check duplicate before insert. */
    Optional<AlertMessageConfig> findByAlertCategoryAndAlertMessage(
            String alertCategory, String alertMessage);

    /**
     * Returns every (alertCategory, alertMessage) pair that is DISABLED.
     * Used by dashboard service to build its exclusion list efficiently
     * in a single DB round-trip.
     *
     * Result: Object[] { alertCategory (String), alertMessage (String) }
     */
    @Query("""
            SELECT a.alertCategory, a.alertMessage
            FROM AlertMessageConfig a
            WHERE a.enabled = false
            """)
    List<Object[]> findDisabledPairs();

    /** Convenience: all configs for one category. */
    List<AlertMessageConfig> findByAlertCategoryOrderByAlertMessageAsc(String alertCategory);
}