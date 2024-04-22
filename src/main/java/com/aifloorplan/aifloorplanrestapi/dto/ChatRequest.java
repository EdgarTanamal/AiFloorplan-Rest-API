package com.aifloorplan.aifloorplanrestapi.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChatRequest {
  @Size(max = 500, message = "Chat must be at most 500 characters")
  @NotEmpty(message = "Chat is required")
  private String chat;

  private int chatgroupId;

  private int userId;
}
