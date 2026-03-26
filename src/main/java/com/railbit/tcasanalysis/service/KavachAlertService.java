package com.railbit.tcasanalysis.service;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Real-time alert generator — called during packet decoding.
 *
 * Checks 3 fields for critical conditions:
 *
 * 1. EMERGENCY_STATUS (3 bits) — from Onboard Regular + Access Request
 *    001 = Unusual Stoppage      → WARNING
 *    010 = SoS                   → CRITICAL
 *    011 = Roll Back Detected    → CRITICAL
 *    100 = Head On Collision     → CRITICAL
 *    101 = Rear End Collision    → CRITICAL
 *    110 = Parting SoS           → CRITICAL
 *
 * 2. Brake_Applied (3 bits) — from Onboard Regular only
 *    010 = Normal Service Brake  → WARNING
 *    011 = Full Service Brake    → WARNING
 *    100 = Emergency Brake       → CRITICAL
 *
 * 3. TAG_LINK_INFO (3 bits) — from Onboard Regular only
 *    001 = Duplicate Tag missing → WARNING
 *    010 = Main Tag missing      → WARNING
 *    011 = Both Tag missing      → CRITICAL
 *    100 = Tag position interchanged → WARNING
 */
@Service
public class KavachAlertService {

//    private static final Logger logger = LoggerFactory.getLogger(KavachAlertService.class);
//    private static final String M = "AlertService";
//
//    @Autowired
//    private KavachAlertRepository alertRepository;
//
//    /**
//     * Check Onboard Regular Packet for alerts.
//     * Called from LocoInnerPacketParser after successful decode.
//     */
//    public void checkOnboardRegular(OnboardRegularPacket pkt, LocoPacket loco) {
//
//        List<KavachAlert> alerts = new ArrayList<>();
//        Date eventTime = loco.getAtDate();
//        Integer locoId = pkt.getSourceLocoId();
//        Integer stationId = loco.getKavachId();
//        Long locoPacketId = loco.getId();
//
//        // 1. EMERGENCY_STATUS
//        if (pkt.getEmergencyStatus() != null && pkt.getEmergencyStatus() > 0) {
//            KavachAlert alert = buildBase(eventTime, locoId, stationId, locoPacketId, "ONBOARD_REGULAR");
//            alert.setAlertCategory("EMERGENCY");
//            alert.setAlertCode(pkt.getEmergencyStatus());
//            alert.setAlertMessage(pkt.getEmergencyStatusStr());
//            alert.setSeverity(getEmergencySeverity(pkt.getEmergencyStatus()));
//            alert.setTrainSpeed(pkt.getTrainSpeed());
//            alert.setLocoMode(pkt.getLocoModeStr());
//            alert.setAbsLocoLoc(pkt.getAbsLocoLoc());
//            alerts.add(alert);
//        }
//
//        // 2. Brake_Applied (only alertable values: 2,3,4)
//        if (pkt.getBrakeApplied() != null && pkt.getBrakeApplied() >= 2) {
//            KavachAlert alert = buildBase(eventTime, locoId, stationId, locoPacketId, "ONBOARD_REGULAR");
//            alert.setAlertCategory("BRAKE");
//            alert.setAlertCode(pkt.getBrakeApplied());
//            alert.setAlertMessage(pkt.getBrakeAppliedStr());
//            alert.setSeverity(pkt.getBrakeApplied() == 4 ? "CRITICAL" : "WARNING");
//            alert.setTrainSpeed(pkt.getTrainSpeed());
//            alert.setLocoMode(pkt.getLocoModeStr());
//            alert.setAbsLocoLoc(pkt.getAbsLocoLoc());
//            alerts.add(alert);
//        }
//
//        // 3. TAG_LINK_INFO (only alertable values: 1,2,3,4)
//        if (pkt.getTagLinkInfo() != null && pkt.getTagLinkInfo() >= 1 && pkt.getTagLinkInfo() <= 4) {
//            KavachAlert alert = buildBase(eventTime, locoId, stationId, locoPacketId, "ONBOARD_REGULAR");
//            alert.setAlertCategory("TAG_LINK");
//            alert.setAlertCode(pkt.getTagLinkInfo());
//            alert.setAlertMessage(pkt.getTagLinkInfoStr());
//            alert.setSeverity(pkt.getTagLinkInfo() == 3 ? "CRITICAL" : "WARNING");
//            alert.setTrainSpeed(pkt.getTrainSpeed());
//            alert.setLocoMode(pkt.getLocoModeStr());
//            alert.setAbsLocoLoc(pkt.getAbsLocoLoc());
//            alerts.add(alert);
//        }
//
//        saveAlerts(alerts);
//    }
//
//    /**
//     * Check Access Request Packet for alerts.
//     * Called from LocoInnerPacketParser after successful decode.
//     */
//    public void checkAccessRequest(AccessRequestPacket pkt, LocoPacket loco) {
//
//        List<KavachAlert> alerts = new ArrayList<>();
//        Date eventTime = loco.getAtDate();
//        Integer locoId = pkt.getSourceLocoId();
//        Integer stationId = pkt.getApproachingStnId();
//        Long locoPacketId = loco.getId();
//
//        // 1. EMERGENCY_STATUS
//        if (pkt.getEmergencyStatus() != null && pkt.getEmergencyStatus() > 0) {
//            KavachAlert alert = buildBase(eventTime, locoId, stationId, locoPacketId, "ACCESS_REQUEST");
//            alert.setAlertCategory("EMERGENCY");
//            alert.setAlertCode(pkt.getEmergencyStatus());
//            alert.setAlertMessage(pkt.getEmergencyStatusStr());
//            alert.setSeverity(getEmergencySeverity(pkt.getEmergencyStatus()));
//            alert.setTrainSpeed(pkt.getTrainSpeed());
//            alert.setLocoMode(pkt.getLocoModeStr());
//            alert.setAbsLocoLoc(pkt.getAbsLocoLoc());
//            alert.setLatitude(pkt.getLatitudeDeg());
//            alert.setLongitude(pkt.getLongitudeDeg());
//            alerts.add(alert);
//        }
//
//        saveAlerts(alerts);
//    }
//
//    private KavachAlert buildBase(Date eventTime, Integer locoId, Integer stationId,
//                                   Long locoPacketId, String sourcePktType) {
//        KavachAlert a = new KavachAlert();
//        a.setEventTime(eventTime);
//        a.setLocoId(locoId);
//        a.setStationId(stationId);
//        a.setLocoPacketId(locoPacketId);
//        a.setSourcePktType(sourcePktType);
//        return a;
//    }
//
//    private String getEmergencySeverity(int code) {
//        switch (code) {
//            case 1: return "WARNING";   // Unusual Stoppage
//            default: return "CRITICAL"; // SoS, Roll Back, Head On, Rear End, Parting SoS
//        }
//    }
//
//    private void saveAlerts(List<KavachAlert> alerts) {
//        if (!alerts.isEmpty()) {
//            alertRepository.saveAll(alerts);
//            for (KavachAlert a : alerts) {
//                logger.warn("[{}] ⚠ ALERT: [{}] {} | loco={}, stn={}, speed={}, mode={}, severity={}",
//                        M, a.getAlertCategory(), a.getAlertMessage(),
//                        a.getLocoId(), a.getStationId(), a.getTrainSpeed(),
//                        a.getLocoMode(), a.getSeverity());
//            }
//        }
//    }
}
