package com.railbit.tcasanalysis.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FaultPacketDTO {

    private Long id;              // Maps to `lm.get("id")`
    private LocalDate date;       // Maps to `lm.get("date")`
    private LocalTime time;       // Maps to `lm.get("time")`
    private String station_name;   // Maps to `stnCodeJoin.get("name")`
    private Integer fault_code;     // Maps to `faultCodeJoin.get("code")`
    private String fault_name;     // Maps to `faultCodeJoin.get("faultName")`
    private String firm;          // Maps to `faultCodeJoin.get("firm")`
    private Long crc;           // Maps to `lm.get("crc")`
    private String hex_value;      // Maps to `lm.get("hexValue")`
    private Integer msg_length;    // Maps to `lm.get("msgLength")`
    private Integer msg_type;      // Maps to `lm.get("msgType")`
    private Integer start_frame;   // Maps to `lm.get("startFrame")`
    private Integer tcas_subsys_type; // Maps to `lm.get("tcasSubsysType")`
    private Integer tcas_subsys_id; // Maps to `lm.get("tcasSubsysId")`
    private Integer total_fault_code; // Maps to `lm.get("totalFaultCode")`

    public FaultPacketDTO(Long id, LocalDate date, LocalTime time, String station_name, Integer fault_code, String fault_name, String firm, Long crc, String hex_value, Integer msg_length, Integer msg_type, Integer start_frame, Integer tcas_subsys_type, Integer total_fault_code) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.station_name = station_name;
        this.fault_code = fault_code;
        this.fault_name = fault_name;
        this.firm = firm;
        this.crc = crc;
        this.hex_value = hex_value;
        this.msg_length = msg_length;
        this.msg_type = msg_type;
        this.start_frame = start_frame;
        this.tcas_subsys_type = tcas_subsys_type;
        this.total_fault_code = total_fault_code;
    }
}
