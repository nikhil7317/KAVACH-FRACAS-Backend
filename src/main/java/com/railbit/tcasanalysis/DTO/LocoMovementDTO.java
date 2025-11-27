package com.railbit.tcasanalysis.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LocoMovementDTO {

    private Long id;

    private LocalDate date;
    private LocalTime time;
    private String stnCode;
    private String locoID;
    private String firmName;
    private String stnFrameNum;
    private String packetType;
    private String locoFrameNum;
    private String absLocation;
    private String trainLen;
    private String locoSpeed;
    private String locoDir;
    private String emrStatus;
    private String locoMode;
    private String rfid;
    private String tin;
    private String brakeMode;
    private String locoRandNum;
    private String frameOffset;
    private String locoSOS;
    private String typOfSig;
    private String sigDir;
    private String lineNum;
    private String curSigAsp;
    private String nxtSigAsp;
    private String ma;
    private String gradient;
    private String appSigDist;
    private String toSpeed;
    private String toDistance;
    private String toReleaseDist;
    private String allocFreqPair;
    private String allocTDMASlot;
    private String stnRandNum;
    private String emrGenSOS;
    private String emrLocoSOS;
    private String profileId;
    private String staticProfileInfoUptoMA;
    private String crcStatus;

    private String locoNum;
    private String stnNum;

    private String zoneId;
    private String divId;
    private String kv;
    private String nmsId;
    private String oemId;

    private String destinationLocoCount;
    private String destinationLocoId;
    private String destToLocoSOS;
    private String referenceFrameNumber;
    private String trainLenInfo;
    private String appStnId;
    private String dstToTurnOut;
}