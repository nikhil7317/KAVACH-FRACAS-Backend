package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.Designation;
import com.railbit.tcasanalysis.entity.FCMToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FCMTokenRepo extends JpaRepository<FCMToken,Long> {
    List<FCMToken> findByUserId(Long userId);
    @Transactional
    void deleteByUserIdAndToken(Long userId, String token);
}
