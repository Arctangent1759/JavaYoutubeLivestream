package com.alexpchu.YTC;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.io.IOException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class YoutubeConverter {
  static final String s = "http://youtube.com/get_video_info?video_id=%s&el=player_embedded";
  public static String GetVideoInfo(String id) throws IOException {
    HttpURLConnection conn;
    try {
      conn = (HttpURLConnection)(new URL(String.format(s,id)).openConnection());
    } catch (MalformedURLException e) {
      System.err.println("Assertion failed: url should always be valid.");
      assert(false);
      return "";
    }
    boolean redirect = false;
    switch (conn.getResponseCode()) {
      case (HttpURLConnection.HTTP_OK):
        break;
      case (HttpURLConnection.HTTP_MOVED_TEMP):  // Fallthrough
      case (HttpURLConnection.HTTP_MOVED_PERM):  // Fallthrough
      case (HttpURLConnection.HTTP_SEE_OTHER):  // Fallthrough
        redirect = true;
        break;
      default:
        System.err.println("Assertion failed: Unexpected http response code.");
        assert(false);
    }
    if (redirect) {
      String newUrl = conn.getHeaderField("Location");
      String cookies = conn.getHeaderField("Set-Cookie");
      conn = (HttpURLConnection) new URL(newUrl).openConnection();
      conn.setRequestProperty("Cookie", cookies);
      conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
      conn.addRequestProperty("User-Agent", "Mozilla");
      conn.addRequestProperty("Referer", "google.com");
    }
    BufferedReader in = new BufferedReader(
        new InputStreamReader(conn.getInputStream()));
    String inputLine;
    StringBuffer html = new StringBuffer();

    while ((inputLine = in.readLine()) != null) {
      html.append(inputLine);
    }
    in.close();
    return html.toString();
  }
  public static ArrayList<String> GetStreams(String id) throws IOException {
    ArrayList<String> out = new ArrayList<String>();
    ArrayList<QSPair> parsed_info = QSParser.parse(GetVideoInfo(id));
    ArrayList<QSPair> stream_map = null;
    for (QSPair p : parsed_info) {
      if (p.key.equals("url_encoded_fmt_stream_map")) {
        for (QSPair q : QSParser.parse(p.value)) {
          if (q.key.equals("url")) {
            out.add(q.value);
          }
        }
      }
    }
    return out;
  }
  public static String GetStream(String id) throws IOException {
    return GetStreams(id).get(0);
  }
  public static void main(String [] args) {
    String input = "la1nmYUjLdo";
    if (args.length >= 1) {
      input = args[0];
    }
    try {
      System.out.println(GetStream(input));
    } catch (IOException e) {
      System.err.println("IO error.");
    }
  }
}
