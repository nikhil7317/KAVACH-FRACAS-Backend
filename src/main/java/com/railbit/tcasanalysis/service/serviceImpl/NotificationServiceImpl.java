package com.railbit.tcasanalysis.service.serviceImpl;

import com.railbit.tcasanalysis.entity.*;
import com.railbit.tcasanalysis.entity.Notification;
import com.railbit.tcasanalysis.repository.NotificationRepo;
import com.railbit.tcasanalysis.service.FCMNotificationService;
import com.railbit.tcasanalysis.service.FCMTokenService;
import com.railbit.tcasanalysis.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepo notificationRepo;
    private final FCMNotificationService fcmNotificationService;
    private final FCMTokenService fcmTokenService;
    private final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Override
    public Notification getNotificationById(Integer id) {
        Optional<Notification> data=notificationRepo.findById(id);
        if(data.isEmpty())
            throw new NoSuchElementException("Notification not found");
        return data.get();
    }

    @Override
    public List<Notification> getAllNotification() {
        return notificationRepo.findAll();
    }

    @Override
    public List<Notification> getByUserId(Integer userId) {
        Pageable pageable = PageRequest.of(0, 1000); // Page 0 with 1000 items
        return notificationRepo.findByUserIdOrderByCreatedDateTimeDesc(userId, pageable);
    }

    @Override
    public Long postNotification(Notification notification) {
        Notification newNotification = notificationRepo.save(notification);
        return newNotification.getId();
    }

    @Override
    public void updateNotification(Notification notification) {
        notificationRepo.save(notification);
    }

    @Override
    public void deleteNotificationById(Integer id) {
        notificationRepo.deleteById(id);
    }

    @Override
    public int importByExcelSheet(MultipartFile excelSheet) throws Exception {
        return 0;
    }

    @Override
    public void sendNotificationToUsersAfterInspectionStatusAdded(List<User> userListToNotify, TcasBreakingInspection inspection,
                                                                   String title, String msg) {
        List<FCMToken> fcmTokenList = new ArrayList<>();
        for (User user : userListToNotify) {
//            logger.info("User : {}",user.getName());
            Notification notification = new Notification();
            notification.setTcasBreakingInspection(inspection);
            notification.setUser(user);
            notification.setTitle(title);
            notification.setMsg(msg);
            postNotification(notification);
            fcmTokenList.addAll(fcmTokenService.getByUserId(user.getId()));
        }
        if (!fcmTokenList.isEmpty()) {
            // Convert List<FCMToken> to List<String>
            List<String> tokens = fcmTokenList.stream()
                    .map(FCMToken::getToken)
                    .filter(Objects::nonNull)         // Exclude null values
                    .filter(token -> !token.isEmpty()) // Exclude empty values
                    .toList();
//                logger.info("Tokens : {}",tokens);
            fcmNotificationService.sendNotifications(tokens,title,msg);
        }

    }

}
