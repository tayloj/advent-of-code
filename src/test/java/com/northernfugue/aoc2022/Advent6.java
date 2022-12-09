package com.northernfugue.aoc2022;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

public class Advent6 {


  @Test
  public void day6() throws Exception {
    assertEquals(Arrays.asList(7, 5, 6, 10, 11), day6(4, Util.input("day6/sample.txt")));
    assertEquals(Arrays.asList(19, 23, 23, 29, 26), day6(14, Util.input("day6/sample.txt")));
    assertEquals(Arrays.asList(1262), day6(4, Util.input("day6/input.txt")));
    assertEquals(Arrays.asList(3444), day6(14, Util.input("day6/input.txt")));
  }

  public List<Integer> day6(int wLength, Path input) throws IOException {
    Set<Character> window = new HashSet<>();
    List<Integer> result = new ArrayList<>();
    try (BufferedReader reader = Files.newBufferedReader(input)) {
      String line;
      while (null != (line = reader.readLine())) {
        result.add(findMarker(line, wLength, window));
      }
    }
    return result;
  }

  synchronized public int findMarker(String line, int wLength, Set<Character> window) {
    outer: for (int b = 0, e = wLength, n = line.length() + 1; e < n; b++, e++) {
      window.clear();
      for (int i = b; i < e; i++) {
        if (!window.add(line.charAt(i))) {
          continue outer;
        }
      }
      return e;
    }
    throw new NoSuchElementException();
  }
}
