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
  // final String apiToken =
  // "lmwr_sk_uWNTJrKaQ4_aVb6NbHGhyBCsCMXI7JtKDcYcHrEHeUD7reC2";
  final String apiToken = "lmwr_sk_NwcTplq6vq_9kubEPG7X9p2P4wR8Tt1ko4UykraH4OyfxJ6G";

  public List<FloorplanResponse> generateFloorplans(String prompt) throws Exception {
    var httpClient = HttpClient.newBuilder().build();

    var payload = String.join("\n", "{", " \"prompt\": \"" + prompt + "\",", " \"aspect_ratio\": \"1:1\",",
        " \"samples\": 2", "}");

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

    // String link =
    // "https://ai-studio-assets.limewire.media/u/350d7653-8b6f-4f5d-b5a8-ba229ddd3eaa/image/52695019-b063-4fca-9b65-7b1c213a3338?Expires=1715650178&Signature=WMhgfoRsb8PBwn3OsI6hlSXOSDdaksrigAqu8E1kLnGBSLtxSPmqhhjQ06utQhkdzsR4ilqjSuRnUGueLXHOfyU5N8CrVd9w45vm3xm-q2HndNaYu0-kPM-SFKymUuI7FZFthFtSTMwQvhyeZmP2WIYuuESPCAm4Uu7bpDFtwS9RoxfYkRkFgK3-s~uJ5h9mF00rq~~8R2KhkfYUpWQEAvqhHjaoLhzTY5Gz4KIMVs1s3cD0CcH~fMvRycZsLBsv~S0pUrt~JGIC2FM-FZ6vqka4Al5IkguMzwZigH7TtQwDkolpJYG3elTO0v0wqa1vx1V5wbWsrONasIHQERABXQ__&Key-Pair-Id=K1U52DHN9E92VT";
    // FloorplanResponse floorplan = new FloorplanResponse();
    // floorplan.setFloorplanId(0);
    // floorplan.setImageData(Util.downloadImage(link));
    // floorplan.setPrompt("Floorplan example");

    // floorplanList.add(floorplan);

    return floorplanList;
  }
}
