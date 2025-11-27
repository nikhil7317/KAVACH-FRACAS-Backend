package com.railbit.tcasanalysis.entity.jwtEntity;


import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtRequest implements Serializable {

    private String username;
    private String password;
    private String captchaToken;
    private String splCode;

}
