package com.northernfugue.aoc2022;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

public class Advent10 {

  @Test(timeout = 5_000)
  public void day10() throws IOException {
    assertEquals(13140, new Device().run(Util.input("day10/sample.txt")));
    assertEquals(13820, new Device().run(Util.input("day10/input.txt")));
  }

  static class Device {
    int x = 1, t, c, s;

    void tick() {
      System.out.format("%s%s", (0 == (c = t % 40)) ? "\n" : "", Math.abs(x - c) < 2 ? '#' : '.');
      if ((++t) % 40 == 20) {
        s += (t * x);
      }
    }

    public int run(Path p) throws IOException {
      try (Scanner scanner = new Scanner(p)) {
        while (scanner.hasNext()) {
          tick(); // noop or first tick of addx
          if ("addx".equals(scanner.next())) {
            tick(); // second tick of addx
            x += scanner.nextInt();
          }
        }
      }
      return s;
    }
  }
}
