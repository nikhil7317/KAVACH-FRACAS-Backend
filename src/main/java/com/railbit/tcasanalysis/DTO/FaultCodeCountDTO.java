package com.railbit.tcasanalysis.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class FaultCodeCountDTO {
    int code;
    String msg;
    int count;
}
