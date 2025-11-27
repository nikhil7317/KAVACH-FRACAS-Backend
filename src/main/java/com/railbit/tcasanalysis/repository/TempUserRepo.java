package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.Designation;
import com.railbit.tcasanalysis.entity.TemporaryUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

public interface TempUserRepo extends JpaRepository<TemporaryUser,Integer> {
    Optional<TemporaryUser> findByEmail(String email);
    Optional<TemporaryUser> findByContact(String contact);
    void deleteByExpiresAtBefore(LocalDateTime now);
    void deleteByEmail(String email);

}
