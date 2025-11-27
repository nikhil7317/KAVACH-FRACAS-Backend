package com.railbit.tcasanalysis.DTO;

import lombok.Data;
import java.util.List;

@Data
public class TrafficData {
    private List<Long> timestamps;
    private List<List<Double>> values;   // supports ANY number of DS
    private List<String> dsNames;        // DS names from RRD header
}

