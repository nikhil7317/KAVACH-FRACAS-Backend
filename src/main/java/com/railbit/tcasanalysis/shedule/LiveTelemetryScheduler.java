package com.railbit.tcasanalysis.shedule;



import com.railbit.tcasanalysis.DTO.LocoTelemetryDTO;
import com.railbit.tcasanalysis.cache.LiveTelemetryCache;
import com.railbit.tcasanalysis.locomodal.AccessRequestPacket;
import com.railbit.tcasanalysis.locomodal.LocoPacket;
import com.railbit.tcasanalysis.repository.LocoPacketRepository;
import com.railbit.tcasanalysis.service.LocoQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
        import java.util.stream.Collectors;

/**
 * Runs every 60 seconds.
 *
 * Flow:
 *   1. Query DB for packets in the last 60-second window (tiny result set).
 *   2. Group by locoId.
 *   3. Write each loco's snapshot into LiveTelemetryCache.
 *
 * The controller then reads from the cache — no DB hit on each frontend poll.
 */
@Component
public class LiveTelemetryScheduler {

    private static final Logger logger = LoggerFactory.getLogger(LiveTelemetryScheduler.class);

    // How far back to look on each tick (milliseconds). Matches poll interval.
    private static final long WINDOW_MS = 60_000L;

    @Autowired
    private LocoQueryService locoQueryService;

    @Autowired
    private LiveTelemetryCache cache;

    /**
     * Fires every 60 seconds (fixedRate = 60_000 ms).
     * initialDelay = 0 means it also runs once on startup so the cache
     * is warm before the first frontend request arrives.
     */
    @Scheduled(fixedRate = 60_000, initialDelay = 0)
    public void refreshLiveCache() {
        try {
            Date now  = new Date();
            Date from = new Date(now.getTime() - WINDOW_MS);

            logger.info("[LiveTelemetryScheduler] Refreshing cache window: {} → {}", from, now);

            // Fetch all locos for the last 60 seconds (no locoId filter = all locos)
            List<LocoPacket> packets = locoQueryService.findPackets(from, now, null, null);

            if (packets == null || packets.isEmpty()) {
                logger.info("[LiveTelemetryScheduler] No packets found in window.");
                return;
            }

            // Extract telemetry from every packet and group by locoId
            Map<Integer, List<LocoTelemetryDTO>> byLoco = packets.stream()
                    .flatMap(p -> extractTelemetry(p).stream())
                    .collect(Collectors.groupingBy(LocoTelemetryDTO::getLocoId));

            // Write each loco's snapshot into the cache
            byLoco.forEach((locoId, telemetry) -> {
                List<LocoTelemetryDTO> sorted = telemetry.stream()
                        .sorted(Comparator.comparing(LocoTelemetryDTO::getTimestamp))
                        .collect(Collectors.toList());
                cache.put(locoId, sorted);
                logger.debug("[LiveTelemetryScheduler] Cached {} points for loco {}", sorted.size(), locoId);
            });

            logger.info("[LiveTelemetryScheduler] Cache refreshed for {} loco(s).", byLoco.size());

        } catch (Exception e) {
            logger.error("[LiveTelemetryScheduler] Error during cache refresh: {}", e.getMessage(), e);
        }
    }

    // ── Helpers (mirrors LocoTelemetryController logic) ──────────────────────

    private List<LocoTelemetryDTO> extractTelemetry(LocoPacket packet) {
        List<LocoTelemetryDTO> results = new ArrayList<>();

        if (packet.getAccessRequestPackets() != null) {
            for (AccessRequestPacket arp : packet.getAccessRequestPackets()) {
                Date timestamp = parseFrameTime(packet.getAtDate(), arp.getFrameTime());
                results.add(new LocoTelemetryDTO(
                        packet.getLocoId(),
                        timestamp,
                        arp.getTrainSpeed(),
                        arp.getLatitudeDeg(),
                        arp.getLongitudeDeg(),
                        "accessRequest"
                ));
            }
        }

        if (packet.getOnboardRegularPackets() != null && !packet.getOnboardRegularPackets().isEmpty()) {
            // Extend here when onboardRegularPackets carry telemetry data
        }

        return results;
    }

    private Date parseFrameTime(Date atDate, String frameTime) {
        if (frameTime == null || frameTime.isEmpty()) return atDate;
        try {
            String[] parts = frameTime.split(":");
            Calendar cal = Calendar.getInstance();
            cal.setTime(atDate);
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
            cal.set(Calendar.MINUTE,      Integer.parseInt(parts[1]));
            cal.set(Calendar.SECOND,      Integer.parseInt(parts[2]));
            return cal.getTime();
        } catch (Exception e) {
            return atDate;
        }
    }
}
