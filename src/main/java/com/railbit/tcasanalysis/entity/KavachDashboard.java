package com.railbit.tcasanalysis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "kavach_dashboard")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KavachDashboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sno;

    private String kavach_loco;

    private String kavach_station;

    private Float total_route;

    private Float total_km;

    private LocalTime total_hours;  // Using LocalTime for hour:min:sec

    private Integer total_trips;

    private Integer trips_with_issue;

    private Integer trips_without_issue;

    private LocalDateTime time_stamp;
}
