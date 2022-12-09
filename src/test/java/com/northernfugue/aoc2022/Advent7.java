package com.northernfugue.aoc2022;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class Advent7 {

  @Test
  public void day7() throws Exception {
    Map<String, Integer> sampleSizes = getDirectorySizes(Util.input("day7/sample.txt"));
    assertEquals(584, sampleSizes.get("//a/e").intValue());
    assertEquals(94853, sampleSizes.get("//a").intValue());
    assertEquals(24933642, sampleSizes.get("//d").intValue());
    assertEquals(48381165, sampleSizes.get("/").intValue());

    assertEquals(95437, part1(sampleSizes));
    assertEquals(24933642, part2(sampleSizes));

    Map<String, Integer> inputSizes = getDirectorySizes(Util.input("day7/input.txt"));
    assertEquals(1517599, part1(inputSizes));
    assertEquals(2481982, part2(inputSizes));
  }

  private static int part1(Map<String, Integer> sizes) {
    return sizes.values().stream()
        .mapToInt(x -> x)
        .filter(x -> x <= 100_000)
        .sum();
  }

  private static int part2(Map<String, Integer> sizes) {
    int total = 70_000_000;
    int needed = 30_000_000;
    int used = sizes.get("/");
    return sizes.values().stream().mapToInt(x -> x)
        .filter(wouldBeFreed -> (total - used + wouldBeFreed) > needed)
        .min()
        .orElseThrow(NoSuchElementException::new);
  }

  private static Map<String, Integer> getDirectorySizes(Path outputTrace) throws IOException {
    Map<String, Integer> directorySizes = new HashMap<>();
    Deque<String> directoryStack = new ArrayDeque<>();

    try (BufferedReader reader = Files.newBufferedReader(outputTrace)) {
      String line;
      while (null != (line = reader.readLine())) {
        if (line.charAt(0) == '$') {
          String command = line.substring(2);
          if (command.startsWith("cd ")) {
            String dir = command.substring(3);
            if ("..".equals(dir)) {
              directoryStack.pop();
            } else if ("/".equals(dir)) {
              directoryStack.clear();
              directoryStack.push("/");
            } else {
              directoryStack.push(directoryStack.peek() + "/" + dir);
            }
            directorySizes.putIfAbsent(directoryStack.peek(), 0);
          } else if (command.startsWith("ls")) {
            // do listing...
          } else {
            throw new IllegalArgumentException("Unknown command in line: " + command);
          }
        } else {
          String field1 = line.substring(0, line.indexOf(' '));
          if (!"dir".equals(field1)) {
            int size = Integer.parseInt(field1);
            Deque<String> copy = new ArrayDeque<>(directoryStack);
            while (!copy.isEmpty()) {
              directorySizes.compute(copy.pop(), (ignore, oldSize) -> oldSize + size);
            }
          }
        }
      }
    }
    return directorySizes;
  }
}
