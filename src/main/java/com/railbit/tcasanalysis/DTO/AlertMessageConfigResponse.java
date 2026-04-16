package com.railbit.tcasanalysis.DTO;



import lombok.Builder;
import lombok.Data;
import java.util.Date;

@Data
@Builder
public class AlertMessageConfigResponse {
    private Long    id;
    private String  alertCategory;
    private String  alertMessage;
    private Boolean enabled;
    private Date    createdAt;
    private Date    updatedAt;
}