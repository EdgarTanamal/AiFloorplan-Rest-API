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

public class LeonardoApiService {
  final String host = "https://cloud.leonardo.ai";

  final String apiToken = "<api token>";

  final String leadingPrompt = "A highly detailed, 2D floorplan blueprint of a house, showcasing the precise size of each room with ruler measurements denoted in bold, sans-serif font, drawn in the style of Kelly Wearstler, featuring a blend of bold lines, geometric shapes, and ornate patterns, with a predominantly white and gray color scheme accented with pops of rich jewel tones, ";

  public List<FloorplanResponse> generateFloorplans(String prompt) throws Exception {
    var pathname = "/api/rest/v1/generations";
    var httpClient = HttpClient.newBuilder().build();

    var payload = new JSONObject()
        .put("modelId", "6b645e3a-d64f-4341-a6d8-7a3690fbf042")
        .put("styleUUID", "111dc692-d470-4eec-b791-3475abac4c46")
        .put("contrast", 3.5)
        .put("num_images", 1)
        .put("width", 1120)
        .put("height", 1120)
        .put("alchemy", true)
        .put("enhancePrompt", false)
        .put("prompt", leadingPrompt + prompt)
        .toString();

    var request = HttpRequest.newBuilder()
        .POST(HttpRequest.BodyPublishers.ofString(payload))
        .uri(URI.create(host + pathname))
        .header("Content-Type", "application/json")
        .header("Accept", "application/json")
        .header("Authorization", "Bearer " + apiToken)
        .build();

    var response = httpClient.send(request,
        HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() != 200) {
      throw new RuntimeException("Failed to get a valid response: " +
          response.body());
    }

    // Parse the JSON response which contains the generation id
    JSONObject jsonResponseId = new JSONObject(response.body()).getJSONObject("sdGenerationJob");

    request = HttpRequest.newBuilder()
        .GET()
        .uri(URI.create(host + pathname + "/" + jsonResponseId.getString("generationId")))
        .header("Content-Type", "application/json")
        .header("Accept", "application/json")
        .header("Authorization", "Bearer " + apiToken)
        .build();

    var responseImages = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    if (responseImages.statusCode() != 200) {
      throw new RuntimeException("Failed to get a valid response: " + responseImages.body());
    }

    // Parse the JSON response which contains generated images
    JSONObject jsonResponseImages = new JSONObject(responseImages.body()).getJSONObject("generations_by_pk");

    // Extract the "generated_images" array
    JSONArray dataArray = jsonResponseImages.getJSONArray("generated_images");

    List<FloorplanResponse> floorplanList = new ArrayList<>();

    String floorplanInfo = String.format("%s\n\n[image/jpeg: %s x %s]", prompt,
        jsonResponseImages.getInt("imageWidth"), jsonResponseImages.getInt("imageHeight"));

    for (int i = 0; i < dataArray.length(); i++) {
      JSONObject data = dataArray.getJSONObject(i);

      FloorplanResponse floorplan = new FloorplanResponse();
      floorplan.setFloorplanId(0);
      floorplan.setImageData(Util.downloadImage(data.getString("url")));
      floorplan.setPrompt(floorplanInfo);

      floorplanList.add(floorplan);
    }

    return floorplanList;
  }
}
