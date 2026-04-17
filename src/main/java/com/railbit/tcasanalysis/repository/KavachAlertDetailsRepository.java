package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.KavachAlertDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface KavachAlertDetailsRepository extends JpaRepository<KavachAlertDetails, Long> {

    KavachAlertDetails findByKavachAlertId(Long kavachAlertId);
    long countByTicketNoStartingWith(String prefix);
    boolean existsByKavachAlertId(Long kavachAlertId);

    @Query("SELECT COUNT(d) FROM KavachAlertDetails d " +
            "WHERE FUNCTION('DATE', d.incidentCreatedAt) = CURRENT_DATE")
    long countTodayTickets();
}