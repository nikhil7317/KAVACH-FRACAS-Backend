package com.railbit.tcasanalysis.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketDetailsDTO {
    private String ticketNo;
    private Boolean status;
    private String assignTo;
    private String description;
    private DivisionDTO division;
    private ZoneDTO zone;
    private LocalDateTime closeDateTime;
    private LocalDate tripDate;
    private List<String> locoNo;
    private String name;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DivisionDTO {
        private String code;
        private String name;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ZoneDTO {
        private String code;
        private String name;
    }
}


