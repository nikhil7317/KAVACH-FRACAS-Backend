package com.railbit.tcasanalysis.DTO;

import lombok.Getter;
import lombok.Setter;

import javax.management.relation.RoleInfo;
import java.time.LocalDateTime;

@Getter
@Setter
public class OemRemarksResponseDTO {

    private Long id;
    private Long kavachAlertDetailsId;
    private String ticketNo;
    private UserInfo createdUser;
    private String ticketRemarks;
    private LocalDateTime createdAt;



    @Getter
    @Setter
    public static class UserInfo {
        private Long id;
        private String name;
        private DesignationInfo designation;
        private RoleInfo role;


        @Getter
        @Setter
        public static class RoleInfo {   // 👈 ADD THIS CLASS
            private Integer id;
            private String name;
        }
        @Getter
        @Setter
        public static class DesignationInfo {
            private Integer id;
            private String name;
            private String title;
        }
    }
}