package com.aifloorplan.aifloorplanrestapi.dto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChatResponse {
  private String chat;

  private List<FloorplanResponse> floorplans = new ArrayList<>();

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Timestamp createTime;
}
