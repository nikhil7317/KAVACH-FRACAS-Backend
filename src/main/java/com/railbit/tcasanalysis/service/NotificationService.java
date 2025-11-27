package com.railbit.tcasanalysis.service;



import com.railbit.tcasanalysis.entity.FCMToken;
import com.railbit.tcasanalysis.entity.Notification;
import com.railbit.tcasanalysis.entity.TcasBreakingInspection;
import com.railbit.tcasanalysis.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface NotificationService {
    Notification getNotificationById(Integer id);
    List<Notification> getAllNotification();
    List<Notification> getByUserId(Integer userId);
    Long postNotification(Notification notification);
    void updateNotification(Notification notification);
    void deleteNotificationById(Integer id);
    int importByExcelSheet(MultipartFile excelSheet) throws Exception;

    void sendNotificationToUsersAfterInspectionStatusAdded(List<User> userListToNotify, TcasBreakingInspection inspection,
                                                      String title, String msg);
}
