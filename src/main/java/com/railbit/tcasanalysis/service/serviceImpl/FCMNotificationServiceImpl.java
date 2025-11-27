package com.railbit.tcasanalysis.service.serviceImpl;

import com.google.firebase.messaging.*;
import com.railbit.tcasanalysis.service.FCMNotificationService;
import com.railbit.tcasanalysis.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FCMNotificationServiceImpl implements FCMNotificationService {

    private final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    public void sendNotification(String token, String title, String body) {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setToken(token)
                .setNotification(notification)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            logger.info("Successfully sent message: {}", response);
        } catch (Exception e) {
            logger.error("Error sending push notification", e);
        }
    }

    @Override
    public String sendNotification(Message note) throws FirebaseMessagingException {
        return "";
    }

    @Override
    public String sendNotificationToAdmin(String note) throws FirebaseMessagingException {
        return "";
    }

    @Override
    public void sendNotifications(List<String> tokens, String title, String body) {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        MulticastMessage message = MulticastMessage.builder()
                .addAllTokens(tokens)
                .setNotification(notification)
                .build();

        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
            logger.info("Successfully sent message to {} devices, {} failures",
                    response.getSuccessCount(), response.getFailureCount());

            if (response.getFailureCount() > 0) {
                response.getResponses().forEach((sendResponse) -> {
                    if (!sendResponse.isSuccessful()) {
                        logger.error("Error sending message to token: {}", sendResponse.getMessageId(),
                                sendResponse.getException());
                    }
                });
            }
        } catch (Exception e) {
            logger.error("Error sending multicast push notification", e);
        }
    }
}
