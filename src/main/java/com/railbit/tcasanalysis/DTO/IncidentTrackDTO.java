package com.railbit.tcasanalysis.DTO;

import com.railbit.tcasanalysis.entity.User;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class IncidentTrackDTO {

    private Long id;
    private LocalDateTime incidentCreatedAt;
    private String ticketRemarks;
    private String ticketStatus;

    // ✅ ADDED: needed by frontend to check if the track creator is OEM
    // The frontend scans incidentTracks[].createdUser.designation.id
    // to detect OEM_REMARK entries — without this field the check always fails.
    private UserSummaryDTO createdUser;

    @Getter
    @Setter
    public static class UserSummaryDTO {
        private Long id;
        private String name;
        private DesignationSummaryDTO designation;

        @Getter
        @Setter
        public static class DesignationSummaryDTO {
            private Integer id;
            private String name;
            private String title;
        }
    }
}