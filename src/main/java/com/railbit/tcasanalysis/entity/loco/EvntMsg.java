package com.railbit.tcasanalysis.entity.loco;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@ToString
public class EvntMsg {
    private String issueMsg;    // The message for the issue
    private Date startTime; // The start time of the event
    private Date endTime;   // The end time of the event
    private String issueName;   // The name of the issue (optional field based on usage)
    private String issueCategory;
    public EvntMsg() {}
}
