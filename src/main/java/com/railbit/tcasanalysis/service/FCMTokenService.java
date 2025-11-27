package com.railbit.tcasanalysis.service;



import com.railbit.tcasanalysis.entity.FCMToken;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FCMTokenService {
    FCMToken getFCMTokenById(Long id);
    List<FCMToken> getByUserId(Long userId);
    List<FCMToken> getAllFCMToken();
    Long postFCMToken(FCMToken fCMToken);
    void updateFCMToken(FCMToken fCMToken);
    void deleteFCMTokenById(Long id);
    boolean removeToken(Long userId, String fcmToken);
}
