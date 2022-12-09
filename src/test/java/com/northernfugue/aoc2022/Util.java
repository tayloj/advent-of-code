package com.northernfugue.aoc2022;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Util {

  private Util() {
    // utility
  }

  static Map<String, Path> inputs = new HashMap<>();

  public static Path input(String s) throws Exception {
    if (!inputs.containsKey(s)) {
      Path p = Paths.get(Util.class.getClassLoader().getResource(s).toURI());
      inputs.put(s, p);
      return p;
    }else {
      return inputs.get(s);
    }
  }


}
