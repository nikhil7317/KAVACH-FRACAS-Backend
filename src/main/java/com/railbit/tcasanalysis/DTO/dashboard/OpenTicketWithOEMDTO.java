package com.railbit.tcasanalysis.DTO.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenTicketWithOEMDTO {

    private String firm;
    private Long openTickets;
    private List<String> ticketNoList;

}
