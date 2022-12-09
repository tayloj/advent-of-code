package com.northernfugue.aoc2022;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class Advent1 {

  @Test(timeout = 10_000)
  public void part1Example() throws URISyntaxException, IOException {
    Elf e1 = part1("day1/sample-input.txt").findFirst().get();
    assertEquals(4, e1.n);
    assertEquals(24000, e1.total());

    assertEquals(66616, part1("day1/input.txt").findFirst().get().total());


    assertEquals(199172, part1("day1/input.txt").limit(3).mapToInt(Elf::total).sum());

  }

  class Elf {

    private final int n;

    Elf(int n) {
      this.n = n;
    }

    @Override
    public String toString() {
      return String.format("Elf(%s, %s, %s)", n, total(), cals);
    }

    List<Integer> cals = new ArrayList<>();

    int total() {
      return cals.stream().mapToInt(x -> x).sum();
    }
  }

  private Stream<Elf> part1(String path) throws IOException {

    List<Elf> elves = new ArrayList<>();


    Path p;
    try {
      p = Util.input(path);
    } catch (Exception e) {
      throw new IOException(e);
    }

    int n = 0;
    try (BufferedReader rr = Files.newBufferedReader(p)) {
      String line = rr.readLine();
      while (null != (line = rr.readLine())) {
        Elf e = new Elf(++n);
        elves.add(e);
        do {
          e.cals.add(Integer.parseInt(line));
          line = rr.readLine();
        } while (line != null && !line.isEmpty());
      }
    }

    return elves.stream().sorted(Comparator.comparing(Elf::total).reversed());
  }

}
