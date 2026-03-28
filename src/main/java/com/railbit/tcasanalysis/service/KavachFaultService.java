package com.railbit.tcasanalysis.service;

import com.railbit.tcasanalysis.entity.KavachAlert;
import com.railbit.tcasanalysis.entity.KavachFaultEntry;
import com.railbit.tcasanalysis.entity.KavachFaultPacket;
import com.railbit.tcasanalysis.repository.KavachAlertRepository;

import com.railbit.tcasanalysis.repository.KavachFaultPacketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Parses KAVACH Fault Message (MSG_TYPE=0x19, Annexure-G G.4.9).
 *
 * Layout:
 *   SOF(2) TYPE(1) LEN(2) SEQ(2) SUB_ID(3) NMS(2) VER(1) DATE(3) TIME(3)
 *   SUB_TYPE(1) FAULT_COUNT(1)
 *   [MODULE_ID(1) FAULT_TYPE(1) FAULT_CODE(2)] × F
 *   CRC(4)
 *
 * Key differences from other packets:
 *   - SOF can be 0xAAAA or 0xBBBB
 *   - KAVACH Subsystem ID is 3 bytes (not 2)
 *   - Subsystem Type: 0x11=Stationary, 0x22=Onboard, 0x33=TSRMS
 */
@Service
public class KavachFaultService {

    private static final Logger logger = LoggerFactory.getLogger(KavachFaultService.class);
    private static final String M = "FaultService";

    @Autowired
    private KavachFaultPacketRepository repository;

    @Autowired
    private KavachAlertRepository alertRepository;

    public void parseFaultPacket(String hexValue, String srcIp, String srcPort) {
        try {
            // SOF is first 4 hex chars (AAAA or BBBB)
            String sof = hexValue.substring(0, 4);

            int msgLength = hexToDecimal(hexValue.substring(6, 10));
            int totalHexLength = (2 + msgLength) * 2;

            if (hexValue.length() < totalHexLength) {
                logger.error("[{}] Size mismatch! Expected: {}, Got: {}", M, totalHexLength, hexValue.length());
                return;
            }

            KavachFaultPacket pkt = new KavachFaultPacket();
            pkt.setHexData(hexValue);
            pkt.setSrcIp(srcIp);
            pkt.setSrcPort(srcPort);
            pkt.setSof(sof);

            // Field 2: Message Type
            pkt.setMsgType(hexValue.substring(4, 6));
            // Field 3: Message Length
            pkt.setMsgLength(msgLength);
            // Field 4: Message Sequence
            pkt.setMsgSequence(hexToDecimal(hexValue.substring(10, 14)));

            // Field 5: KAVACH Subsystem ID (3 bytes = 6 hex) — pos 14-20
            pkt.setKavachSubsystemId(hexToDecimal(hexValue.substring(14, 20)));

            // Field 6: NMS System ID (2 bytes) — pos 20-24
            pkt.setNmsSystemId(hexToDecimal(hexValue.substring(20, 24)));

            // Field 7: System Version (1 byte) — pos 24-26
            pkt.setSystemVersion(hexToDecimal(hexValue.substring(24, 26)));

            // Field 8: Date (3 bytes) — pos 26-32
            int dd = hexToDecimal(hexValue.substring(26, 28));
            int mm = hexToDecimal(hexValue.substring(28, 30));
            int yy = hexToDecimal(hexValue.substring(30, 32));

            // Field 9: Time (3 bytes) — pos 32-38
            int hh = hexToDecimal(hexValue.substring(32, 34));
            int mi = hexToDecimal(hexValue.substring(34, 36));
            int ss = hexToDecimal(hexValue.substring(36, 38));

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                pkt.setAtDate(sdf.parse(String.format("20%02d-%02d-%02d %02d:%02d:%02d", yy, mm, dd, hh, mi, ss)));
            } catch (Exception e) {
                pkt.setAtDate(new Date());
            }

            // Field 10: KAVACH Subsystem Type (1 byte) — pos 38-40
            int subType = hexToDecimal(hexValue.substring(38, 40));
            pkt.setSubsystemType(subType);
            pkt.setSubsystemTypeStr(decodeSubsystemType(subType));

            // Field 11: Total Fault Codes (1 byte) — pos 40-42
            int faultCount = hexToDecimal(hexValue.substring(40, 42));
            pkt.setTotalFaultCodes(faultCount);

            // Field 15: CRC (last 4 bytes)
            pkt.setCrcHex(hexValue.substring(totalHexLength - 8, totalHexLength));
            pkt.setCrcValid(true); // TODO: validate with CRC32Generator

            // Parse fault entries — pos 42 onwards, each 4 bytes (8 hex chars)
            List<KavachAlert> alerts = new ArrayList<>();
            String faultsHex = hexValue.substring(42, totalHexLength - 8);

            int pos = 0;
            for (int i = 0; i < faultCount; i++) {
                if (pos + 8 > faultsHex.length()) {
                    logger.warn("[{}] Fault #{}: truncated at pos {}", M, i + 1, pos);
                    break;
                }

                KavachFaultEntry entry = new KavachFaultEntry();

                // Field 12: Module ID (1 byte)
                entry.setModuleId(hexToDecimal(faultsHex.substring(pos, pos + 2)));
                pos += 2;

                // Field 13: Fault Code Type (1 byte)
                int faultType = hexToDecimal(faultsHex.substring(pos, pos + 2));
                entry.setFaultCodeType(faultType);
                entry.setFaultCodeTypeStr(faultType == 1 ? "Fault Code" : faultType == 2 ? "Recovery Code" : "Unknown (" + faultType + ")");
                pos += 2;

                // Field 14: Fault Code (2 bytes)
                String faultCodeHex = faultsHex.substring(pos, pos + 4);
                entry.setFaultCode(hexToDecimal(faultCodeHex));
                entry.setFaultCodeHex(faultCodeHex.toUpperCase());
                pos += 4;

                pkt.addFaultEntry(entry);

                logger.info("[{}] Fault #{}: module={}, type={}, code=0x{}",
                        M, i + 1, entry.getModuleId(), entry.getFaultCodeTypeStr(), entry.getFaultCodeHex());

                // Generate alert for fault codes (not recovery)
                if (faultType == 1) {
                    KavachAlert alert = new KavachAlert();
                    alert.setEventTime(pkt.getAtDate());
                    alert.setStationId(subType == 0x11 ? pkt.getKavachSubsystemId() : null);
                    alert.setLocoId(subType == 0x22 ? pkt.getKavachSubsystemId() : null);
                    alert.setAlertCategory("FAULT");
                    alert.setAlertCode(entry.getFaultCode());
                    alert.setAlertMessage("Fault: Module=" + entry.getModuleId()
                            + " Code=0x" + entry.getFaultCodeHex()
                            + " Source=" + pkt.getSubsystemTypeStr());
                    alert.setSeverity("CRITICAL");
                    alert.setSourcePktType("FAULT_MSG_" + pkt.getSubsystemTypeStr().toUpperCase().replace(" ", "_"));
                    alerts.add(alert);
                }
            }

            repository.save(pkt);

            if (!alerts.isEmpty()) {
                alertRepository.saveAll(alerts);
                logger.warn("[{}] Generated {} fault alerts from subsystem {} (ID={})",
                        M, alerts.size(), pkt.getSubsystemTypeStr(), pkt.getKavachSubsystemId());
            }

            logger.info("[{}] Saved Fault: subsystem={} ({}), faults={}, alerts={}",
                    M, pkt.getSubsystemTypeStr(), pkt.getKavachSubsystemId(),
                    faultCount, alerts.size());

        } catch (Exception e) {
            logger.error("[{}] Error: {}", M, e.getMessage(), e);
        }
    }

    private String decodeSubsystemType(int type) {
        switch (type) {
            case 0x11: return "Stationary KAVACH";
            case 0x22: return "Onboard KAVACH";
            case 0x33: return "TSRMS";
            default:   return "Unknown (0x" + Integer.toHexString(type) + ")";
        }
    }

    private int hexToDecimal(String hex) { return Integer.parseInt(hex, 16); }
}
