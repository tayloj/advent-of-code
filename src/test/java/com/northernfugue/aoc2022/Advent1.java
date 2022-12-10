package com.northernfugue.aoc2022;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.IntStream;

public class Advent1 {

  @Test(timeout = 10_000)
  public void part1Example() throws Exception {
    assertEquals(24000, caloriesDescending(Util.input("day1/sample-input.txt")).findFirst().getAsInt());

    assertEquals(66616, caloriesDescending(Util.input("day1/input.txt")).findFirst().getAsInt());
    assertEquals(199172, caloriesDescending(Util.input("day1/input.txt")).limit(3).sum());
  }

  private IntStream caloriesDescending(Path p) throws IOException {
    Deque<int[]> cals = new ArrayDeque<>();
    try (BufferedReader reader = Files.newBufferedReader(p)) {
      for (String line = ""; line != null; line = reader.readLine()) {
        if (line.isEmpty()) {
          cals.push(new int[] {0});
        } else {
          cals.peek()[0] += Integer.parseInt(line);
        }
      }
    }
    return cals.stream().mapToInt(x -> -x[0]).sorted().map(x -> -x);
  }
}
