package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.Designation;
import com.railbit.tcasanalysis.entity.FCMToken;
import com.railbit.tcasanalysis.entity.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepo extends JpaRepository<Notification,Integer> {
    List<Notification> findByUserIdOrderByCreatedDateTimeDesc(Integer userId, Pageable pageable);
}
