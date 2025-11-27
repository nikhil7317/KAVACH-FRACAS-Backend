package com.railbit.tcasanalysis.DTO.incident;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.railbit.tcasanalysis.entity.TcasBreakingInspection;
import com.railbit.tcasanalysis.util.serializers.EpochToLocalDateDeserializer;
import com.railbit.tcasanalysis.util.serializers.EpochToLocalDateTimeDeserializer;
import com.railbit.tcasanalysis.util.serializers.LocalDateEpochSerializer;
import com.railbit.tcasanalysis.util.serializers.LocalDateTimeToEpochSerializer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class IncidentTicketDTO {

    private Long id;
    private String ticketNo;
    private String description;
    private String assignTo;

    private List<String> firms;
    private List<IncidentDTO> incidents;

    @JsonSerialize(using = LocalDateEpochSerializer.class)
    @JsonDeserialize(using = EpochToLocalDateDeserializer.class)
    private LocalDate targetDate;

    @JsonSerialize(using = LocalDateTimeToEpochSerializer.class)
    @JsonDeserialize(using = EpochToLocalDateTimeDeserializer.class)
    private LocalDateTime closureDateTime;

    @JsonSerialize(using = LocalDateTimeToEpochSerializer.class)
    @JsonDeserialize(using = EpochToLocalDateTimeDeserializer.class)
    private LocalDateTime createdDateTime;

}
