package com.railbit.tcasanalysis.DTO;

import com.railbit.tcasanalysis.entity.*;
import com.railbit.tcasanalysis.entity.loco.Shed;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//Customer DTO to store his details.
public class UserDTO {
    private Long id;
    private String name;
    private String contact;
    private String password;
    private String email;
    private Boolean status;
    private Role role;
    private Long adminId;
    private Boolean readPermission;
    private Boolean writePermission;
    private Boolean openPermission;
    private Boolean closePermission;
    private Shed shed;
    private Firm firm;
    private Division division;
    private Zone zone;
    private Designation designation;
    private String fullDesignation;
    private String otp;
}