package com.aifloorplan.aifloorplanrestapi.dto;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FloorplanResponse {
  private int floorplanId;

  private byte[] imageData;

  private String prompt;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Timestamp createTime;
}
