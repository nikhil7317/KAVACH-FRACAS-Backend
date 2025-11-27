package com.railbit.tcasanalysis.service;


import com.railbit.tcasanalysis.entity.User;

import java.util.List;

public interface EmailService {
    void sendEmail(String subject, String msg, List<String> toList);
    void sendUsernameAndPassword(User user);
    void mailService(String subject, String msg, List<String> to);
    void sendOTP(String email, String otp);
}
