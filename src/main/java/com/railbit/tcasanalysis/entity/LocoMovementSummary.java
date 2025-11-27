package com.railbit.tcasanalysis.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity(name = "locomovementsummary")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocoMovementSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime eventTime;

    private LocalDateTime reportFromTime;

    private LocalDateTime reportToTime;

    private int oemId;

    private int locoId;

    private BigDecimal travelledMeter;

    private int totalTimeMin;
}
