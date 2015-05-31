package com.alexpchu.YTC;

import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.io.IOException;

class QSPair {
  public String key;
  public String value;
  public QSPair(String s) {
    String[] kv = s.split("=");
    key = kv[0];
    try {
      value = URLDecoder.decode(kv[1], "UTF-8");
    } catch (UnsupportedEncodingException e) {
      System.err.println("Assertion failed: Querystring should be in a supported format.");
      assert(false);
    }
  }
}
public class QSParser {
  public static ArrayList<QSPair> parse(String input) {
    ArrayList<QSPair> out = new ArrayList<QSPair>();
    for (String fragment : input.split("&")) {
      out.add(new QSPair(fragment));
    }
    return out;
  }
  public static void main(String [] args) {
    try {
      for (QSPair i : parse(YoutubeConverter.GetVideoInfo("la1nmYUjLdo"))) {
        System.out.println(String.format("%s : %s", i.key, i.value));
      }
    } catch (IOException e) {
    }
  }
}
