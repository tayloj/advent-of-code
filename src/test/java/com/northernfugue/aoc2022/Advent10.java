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
    int t = 0, x = 1, signal = 0, col;

    void tick() {
      if (0 == (col = t % 40)) {
        System.out.println();
      }
      System.out.print(((x - 1) <= col && col <= (x + 1)) ? '#' : '.');
      if ((++t) % 40 == 20) {
        signal += (t * x);
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
      return signal;
    }
  }
}
