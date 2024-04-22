package com.aifloorplan.aifloorplanrestapi.dto;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FloorplanRequest {
  @Min(value = 1, message = "User id is required")
  private int userId;

  @Min(value = 1, message = "Floorplan id is required")
  private int floorplanId;
}
