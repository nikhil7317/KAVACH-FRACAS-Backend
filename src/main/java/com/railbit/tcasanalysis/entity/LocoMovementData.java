package com.railbit.tcasanalysis.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.railbit.tcasanalysis.entity.loco.Loco;
import com.railbit.tcasanalysis.util.serializers.EpochToLocalDateTimeDeserializer;
import com.railbit.tcasanalysis.util.serializers.LocalDateTimeToEpochSerializer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@Entity(name = "locomovementdata")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocoMovementData implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonProperty("date")
    @Column(name = "date")
    private LocalDate date;

    @JsonFormat(pattern = "HH:mm:ss")
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    @JsonProperty("time")
    @Column(name = "time")
    private LocalTime time;

    @ManyToOne
    @JoinColumn(name="stnCode",referencedColumnName = "id")
    @JsonIgnore
    private Station stnCode;

    @ManyToOne
    @JoinColumn(name="locoID",referencedColumnName = "id")
    @JsonIgnore
    private Loco locoID;

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

    @CreationTimestamp
    @JsonSerialize(using = LocalDateTimeToEpochSerializer.class)
    @JsonDeserialize(using = EpochToLocalDateTimeDeserializer.class)
    private LocalDateTime createdDateTime=LocalDateTime.now();
}
