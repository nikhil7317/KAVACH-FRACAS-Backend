package com.railbit.tcasanalysis.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ShedRemarksResponseDTO {

    private Long id;
    private Long locoFailureId;
    private String ticketNo;
    private String ticketRemarks;
    private LocalDateTime createdAt;
    private UserInfo createdUser;

    @Getter
    @Setter
    public static class UserInfo {
        private Long id;
        private String name;
        private DesignationInfo designation;
        private RoleInfo role;

        @Getter
        @Setter
        public static class DesignationInfo {
            private Integer id;
            private String name;
            private String title;
        }

        @Getter
        @Setter
        public static class RoleInfo {
            private Integer id;
            private String name;
        }
    }
}