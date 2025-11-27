package com.railbit.tcasanalysis.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;

import java.util.List;

public interface FCMNotificationService {

    void sendNotification(String token, String title, String body) throws FirebaseMessagingException;
    void sendNotifications(List<String> tokens, String title, String body);
    String sendNotification(Message note) throws FirebaseMessagingException;
    String sendNotificationToAdmin(String note) throws FirebaseMessagingException;

}
