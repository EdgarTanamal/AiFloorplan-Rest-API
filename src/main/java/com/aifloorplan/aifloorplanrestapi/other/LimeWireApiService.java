package com.aifloorplan.aifloorplanrestapi.other;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.aifloorplan.aifloorplanrestapi.dto.FloorplanResponse;

public class LimeWireApiService {
  final String host = "https://api.limewire.com";

  final String apiToken = "<apiToken>";

  final String leadingPrompt = "2D Floorplan Blueprint of House, Show Size of Each Room with Ruler Number, Draw It In Style of Kelly Wearstler, The Blueprint Must Include: ";

  public List<FloorplanResponse> generateFloorplans(String prompt) throws Exception {
    var httpClient = HttpClient.newBuilder().build();

    var payload = new JSONObject()
        .put("prompt", leadingPrompt + prompt)
        .put("aspect_ratio", "1:1")
        .put("samples", 1)
        .toString();

    var pathname = "/api/image/generation";
    var request = HttpRequest.newBuilder()
        .POST(HttpRequest.BodyPublishers.ofString(payload))
        .uri(URI.create(host + pathname))
        .header("Content-Type", "application/json")
        .header("X-Api-Version", "v1")
        .header("Accept", "application/json")
        .header("Authorization", "Bearer " + apiToken)
        .build();

    var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() != 200) {
      throw new RuntimeException("Failed to get a valid response: " + response.body());
    }

    List<FloorplanResponse> floorplanList = new ArrayList<>();

    // Parse the JSON response
    JSONObject jsonResponse = new JSONObject(response.body());

    // Extract the "data" array
    JSONArray dataArray = jsonResponse.getJSONArray("data");

    for (int i = 0; i < dataArray.length(); i++) {
      // Extract the "asset_url" value
      JSONObject data = dataArray.getJSONObject(i);

      String floorplanInfo = String.format("%s\n\n[%s: %s x %s]", prompt,
          data.getString("type"),
          data.getInt("width"), data.getInt("height"));

      FloorplanResponse floorplan = new FloorplanResponse();
      floorplan.setFloorplanId(0);
      floorplan.setImageData(Util.downloadImage(data.getString("asset_url")));
      floorplan.setPrompt(floorplanInfo);

      floorplanList.add(floorplan);
    }

    return floorplanList;
  }
}
