package com.northernfugue.aoc2022;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

public class Advent7 {

  @Test
  public void day7() throws Exception {
    Map<Path, Integer> sampleSizes = getDirectorySizes(Util.input("day7/sample.txt"));
    assertEquals(584, sampleSizes.get(Paths.get("/a/e")).intValue());
    assertEquals(94853, sampleSizes.get(Paths.get("/a")).intValue());
    assertEquals(24933642, sampleSizes.get(Paths.get("/d")).intValue());
    assertEquals(48381165, sampleSizes.get(Paths.get("/")).intValue());

    assertEquals(95437, part1(sampleSizes));
    assertEquals(24933642, part2(sampleSizes));

    Map<Path, Integer> inputSizes = getDirectorySizes(Util.input("day7/input.txt"));
    assertEquals(1517599, part1(inputSizes));
    assertEquals(2481982, part2(inputSizes));
  }

  private static int part1(Map<Path, Integer> sizes) {
    return sizes.values().stream().mapToInt(x -> x).filter(x -> x <= 100_000).sum();
  }

  private static int part2(Map<Path, Integer> sizes) {
    int total = 70_000_000;
    int needed = 30_000_000;
    int used = sizes.get(Paths.get("/"));
    return sizes.values().stream().mapToInt(x -> x)
        .filter(wouldBeFreed -> (total - used + wouldBeFreed) > needed)
        .min()
        .orElseThrow(NoSuchElementException::new);
  }

  private static Map<Path, Integer> getDirectorySizes(Path outputTrace) throws IOException {
    Map<Path, Integer> directorySizes = new HashMap<>();
    Path cwd = Paths.get("/");
    try (BufferedReader r = Files.newBufferedReader(outputTrace)) {
      for (String line = r.readLine(); line != null; line = r.readLine()) {
        if (line.startsWith("$")) {
          if (line.startsWith("$ cd ")) {
            directorySizes.putIfAbsent(cwd = cwd.resolve(line.substring(5)).normalize(), 0);
          }
        } else if (!line.startsWith("dir")) {
          int size = Integer.parseInt(line.substring(0, line.indexOf(' ')));
          Stream.iterate(cwd, Path::getParent).limit(1 + cwd.getNameCount())
              .forEach(ancestor -> directorySizes.compute(ancestor, (ignore, old) -> old + size));
        }
      }
    }
    return directorySizes;
  }
}
