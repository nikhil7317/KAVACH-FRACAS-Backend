package com.railbit.tcasanalysis.shedule;

import com.railbit.tcasanalysis.repository.TempUserRepo;
import jakarta.transaction.Transactional;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CleanUpTempUsers {

    private final TempUserRepo tempUserRepo;

    // Run cleanup every 1 minute
    @Scheduled(fixedRate = 60 * 1000)
    @Transactional
    public void cleanUpExpiredEntries() {
        tempUserRepo.deleteByExpiresAtBefore(LocalDateTime.now());
    }

}
