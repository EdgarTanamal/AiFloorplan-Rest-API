package com.aifloorplan.aifloorplanrestapi.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserRequest {

  @Size(min = 10, message = "Password must be at least 10 characters")
  @Size(max = 50, message = "Password must be at most 50 characters")
  private String password;

  private boolean isPremium;

}