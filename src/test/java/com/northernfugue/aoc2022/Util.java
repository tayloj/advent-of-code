package com.northernfugue.aoc2022;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Util {

  private Util() {
    // utility
  }

  public static Path input(String s) throws Exception {
    return Paths.get(Util.class.getClassLoader().getResource(s).toURI());
  }


}
