package com.aifloorplan.aifloorplanrestapi.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ServerResponse {
  private boolean success;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private List<String> messages;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Map<String, Object> payload;

  public void addMessages(String message) {
    if (messages == null) {
      this.messages = new ArrayList<>();
    }

    this.messages.add(message);
  }

  public void addPayloadField(String fieldName, Object value) {
    if (payload == null) {
      this.payload = new HashMap<>();
    }

    this.payload.put(fieldName, value);
  }

}