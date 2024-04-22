package com.aifloorplan.aifloorplanrestapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class LoginResponse {
  private int userId;

  private String token;
}
