package com.aifloorplan.aifloorplanrestapi.dto;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChatgroupResponse {
  private int chatgroupId;

  private String chat;

  private Timestamp createTime;
}
