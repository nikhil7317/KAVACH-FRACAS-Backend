package com.railbit.tcasanalysis.DTO.reports;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.railbit.tcasanalysis.entity.IssueCategory;
import com.railbit.tcasanalysis.entity.PossibleRootCause;
import com.railbit.tcasanalysis.entity.RootCauseSubCategory;
import com.railbit.tcasanalysis.util.serializers.EpochToLocalDateDeserializer;
import com.railbit.tcasanalysis.util.serializers.LocalDateEpochSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class OpenTicketReportDTO {

    Long incidentId;
    String zoneCode;
    String divisionCode;
    String ticketNo;
    IssueCategory issue;
    PossibleRootCause rootCause;
    RootCauseSubCategory rootCauseSubCategory;
    Long incidentCount;
    String incidentDescription;
    String ticketDescription;
    @JsonSerialize(using = LocalDateEpochSerializer.class)
    @JsonDeserialize(using = EpochToLocalDateDeserializer.class)
    private LocalDate firstIncidentDate;
    String assignTo;
    @JsonSerialize(using = LocalDateEpochSerializer.class)
    @JsonDeserialize(using = EpochToLocalDateDeserializer.class)
    private LocalDate targetDate;
    Long daysPending;

    public OpenTicketReportDTO(Long incidentId,String zoneCode, String divisionCode, String ticketNo, Long incidentCount, String incidentDescription, String ticketDescription, LocalDate firstIncidentDate, String assignTo, LocalDate targetDate, Long daysPending) {
        this.incidentId = incidentId;
        this.zoneCode = zoneCode;
        this.divisionCode = divisionCode;
        this.ticketNo = ticketNo;
        this.incidentCount = incidentCount;
        this.incidentDescription = incidentDescription;
        this.ticketDescription = ticketDescription;
        this.firstIncidentDate = firstIncidentDate;
        this.assignTo = assignTo;
        this.targetDate = targetDate;
        this.daysPending = daysPending;
    }
}
