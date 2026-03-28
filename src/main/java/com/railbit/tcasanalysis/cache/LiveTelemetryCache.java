
package com.railbit.tcasanalysis.cache;

import com.railbit.tcasanalysis.DTO.LocoTelemetryDTO;
import org.springframework.stereotype.Component;

import java.util.*;
        import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory cache that holds the latest 1-minute telemetry snapshot for each loco.
 *
 * The scheduler (LiveTelemetryScheduler) writes here every 60 seconds.
 * The controller reads from here — zero DB hits on poll requests.
 */
@Component
public class LiveTelemetryCache {

    // locoId → list of telemetry points from the last 60-second window
    private final Map<Integer, List<LocoTelemetryDTO>> cache = new ConcurrentHashMap<>();

    // locoId → timestamp of last cache refresh
    private final Map<Integer, Date> lastRefreshed = new ConcurrentHashMap<>();

    /** Replace the cached snapshot for a given loco. */
    public void put(Integer locoId, List<LocoTelemetryDTO> data) {
        cache.put(locoId, Collections.unmodifiableList(new ArrayList<>(data)));
        lastRefreshed.put(locoId, new Date());
    }

    /** Get the cached snapshot for a loco (empty list if not yet populated). */
    public List<LocoTelemetryDTO> get(Integer locoId) {
        return cache.getOrDefault(locoId, Collections.emptyList());
    }

    /** Get all cached loco IDs. */
    public Set<Integer> getAllLocoIds() {
        return cache.keySet();
    }

    /** When was the cache last refreshed for this loco? */
    public Date getLastRefreshed(Integer locoId) {
        return lastRefreshed.get(locoId);
    }

    /** Full snapshot for all locos (used by the "all locos" map view). */
    public Map<Integer, List<LocoTelemetryDTO>> getAll() {
        return Collections.unmodifiableMap(cache);
    }
}