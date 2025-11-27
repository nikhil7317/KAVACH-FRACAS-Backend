package com.railbit.tcasanalysis.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.sql.In;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {
  private String token;
  private String type = "Bearer";
  private Integer id;
  private String username;
  private String email;
  private List<String> roles;
  private String emp_code;
  private String name;
  private String mobile;

  public JwtResponse(String token,Integer id, String username, String email, List<String> roles, String name) {
    this.token = token;
    this.id = id;

    this.username = username;
    this.email = email;
    this.roles = roles;
    this.name = name;
  }



}
