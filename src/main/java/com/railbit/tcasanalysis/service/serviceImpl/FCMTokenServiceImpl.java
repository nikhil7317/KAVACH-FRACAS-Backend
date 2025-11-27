package com.railbit.tcasanalysis.service.serviceImpl;


import com.railbit.tcasanalysis.entity.FCMToken;
import com.railbit.tcasanalysis.repository.FCMTokenRepo;
import com.railbit.tcasanalysis.service.FCMTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FCMTokenServiceImpl implements FCMTokenService {
    private final FCMTokenRepo fCMTokenRepo;

    @Override
    public FCMToken getFCMTokenById(Long id) {
        Optional<FCMToken> data=fCMTokenRepo.findById(id);
        if(data.isEmpty())
            throw new NoSuchElementException("FCMToken not found");
        return data.get();
    }

    @Override
    public List<FCMToken> getAllFCMToken() {
        return fCMTokenRepo.findAll();
    }

    @Override
    public List<FCMToken> getByUserId(Long userId) {
        return fCMTokenRepo.findByUserId(userId);
    }

    @Override
    public Long postFCMToken(FCMToken fCMToken) {
       FCMToken newFCMToken = fCMTokenRepo.save(fCMToken);
       return newFCMToken.getId();
    }

    @Override
    public void updateFCMToken(FCMToken fCMToken) {
        fCMTokenRepo.save(fCMToken);
    }

    @Override
    public void deleteFCMTokenById(Long id) {
        fCMTokenRepo.deleteById(id);
    }

    @Transactional
    @Override
    public boolean removeToken(Long userId, String fcmToken) {
        try {
            fCMTokenRepo.deleteByUserIdAndToken(userId, fcmToken);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
