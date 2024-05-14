package com.aifloorplan.aifloorplanrestapi.other;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class Util {
  public static byte[] downloadImage(String imageUrl) throws IOException {
    URL url = new URL(imageUrl);
    URLConnection connection = url.openConnection();
    try (InputStream inputStream = connection.getInputStream();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

      byte[] buffer = new byte[4096];
      int bytesRead;
      while ((bytesRead = inputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, bytesRead);
      }
      return outputStream.toByteArray();
    }
  }
}
