package com.railbit.tcasanalysis.DTO;


import com.railbit.tcasanalysis.entity.KavachAlert;
import com.railbit.tcasanalysis.entity.KavachAlertDetails;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KavachAlertFullDetailsDto {
    private KavachAlert kavachAlert;
    private KavachAlertDetails kavachAlertDetails;
}