package com.railbit.tcasanalysis.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmsResponse {

    @JsonProperty("ResponseCode")
    private String responseCode;

    @JsonProperty("ResponseMessage")
    private String responseMessage;

    @JsonProperty("TxId")
    private String txId;

    @JsonProperty("SmsEncoding")
    private String smsEncoding;

    @JsonProperty("SmsLength")
    private int smsLength;

    @JsonProperty("BalanceUsed")
    private int balanceUsed;

    @JsonProperty("TotalMobileNumberSubmitted")
    private int totalMobileNumberSubmitted;

}
