package com.railbit.tcasanalysis.DTO;



import java.util.Date;

public class LocoTelemetryDTO {
    private Integer locoId;
    private Date timestamp;           // atDate from main packet or frameTime from access request
    private Integer trainSpeed;      // from accessRequestPackets
    private String latitudeDeg;      // from accessRequestPackets
    private String longitudeDeg;     // from accessRequestPackets
    private String packetSource;     // "accessRequest" or "regularPacket" - tells which array had data

    // Constructor
    public LocoTelemetryDTO(Integer locoId, Date timestamp, Integer trainSpeed,
                            String latitudeDeg, String longitudeDeg, String packetSource) {
        this.locoId = locoId;
        this.timestamp = timestamp;
        this.trainSpeed = trainSpeed;
        this.latitudeDeg = latitudeDeg;
        this.longitudeDeg = longitudeDeg;
        this.packetSource = packetSource;
    }

    // Getters
    public Integer getLocoId() { return locoId; }
    public Date getTimestamp() { return timestamp; }
    public Integer getTrainSpeed() { return trainSpeed; }
    public String getLatitudeDeg() { return latitudeDeg; }
    public String getLongitudeDeg() { return longitudeDeg; }
    public String getPacketSource() { return packetSource; }
}