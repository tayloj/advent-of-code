package com.northernfugue.aoc2022;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Advent6 {


  private final Set<Character> window = new HashSet<>();

  @Test
  public void day6() throws Exception {
    assertEquals(Arrays.asList(7, 5, 6, 10, 11), day6(4, Util.input("day6/sample.txt")));
    assertEquals(Arrays.asList(19, 23, 23, 29, 26), day6(14, Util.input("day6/sample.txt")));
    assertEquals(Arrays.asList(1262), day6(4, Util.input("day6/input.txt")));
    assertEquals(Arrays.asList(3444), day6(14, Util.input("day6/input.txt")));
  }

  public List<Integer> day6(int wLength, Path input) throws IOException {
    try (BufferedReader reader = Files.newBufferedReader(input);
        Stream<String> lines = reader.lines()) {
      return lines.map(line -> findMarker(line, wLength)).collect(Collectors.toList());
    }
  }

  synchronized public int findMarker(String line, int wLength) {
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
