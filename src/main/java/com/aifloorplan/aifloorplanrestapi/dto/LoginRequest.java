package com.aifloorplan.aifloorplanrestapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequest {
  @Size(max = 255, message = "Email must be at most 255 characters")
  @NotEmpty(message = "Email is required")
  @Email(message = "Email is not valid")
  private String email;

  @Size(min = 10, message = "Password must be at least 10 characters")
  @Size(max = 50, message = "Password must be at most 50 characters")
  @NotEmpty(message = "Password is required")
  private String password;

}
