package com.railbit.tcasanalysis.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LastTripDateNoDTO {
    private LocalDate lastTripDate;
    private Integer tripNo;
}
