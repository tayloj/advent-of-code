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

public class Advent5 {

  @Test
  public void day5() throws Exception {
    assertEquals("CMZ", day5(Util.input("day5/sample-input.txt"), false));
    assertEquals("MCD", day5(Util.input("day5/sample-input.txt"), true));
    assertEquals("GFTNRBZPF", day5(Util.input("day5/input.txt"), false));
    assertEquals("VRQWPDSGP", day5(Util.input("day5/input.txt"), true));
  }

  /**
   * Reads a simulated environment from an input file and executes the commands.
   *
   * @param path the input file
   * @param moveMany whether the crane moves multiple boxes at once
   * @return a string of the contents from the top of each stack
   * @throws IOException if an I/O error occurs
   */
  public String day5(Path path, boolean moveMany) throws IOException {

    Map<Integer, Deque<Character>> stacks = new HashMap<>();

    try (BufferedReader reader = Files.newBufferedReader(path)) {
      String line;

      while (null != (line = reader.readLine()) && line.contains("[")) {
        for (int stack = 0, col = 1, n = line.length(); col < n; stack++, col += 4) {
          char content = line.charAt(col);
          if (content != ' ') {
            stacks.computeIfAbsent(stack, ignore -> new ArrayDeque<>()).add(content);
          }
        }
      }

      Deque<Character> temp = new ArrayDeque<>();
      while (null != (line = reader.readLine())) {
        if (line.startsWith("m")) {
          int f = line.indexOf(" from ");
          int t = line.indexOf(" to ");
          int howMany = Integer.parseInt(line.substring(5, f));
          Deque<Character> from = stacks.get(Integer.parseInt(line.substring(f + 6, t)) - 1);
          Deque<Character> to = stacks.get(Integer.parseInt(line.substring(t + 4)) - 1);
          if (moveMany) {
            transfer(howMany, from, temp);
            transfer(howMany, temp, to);
          } else {
            transfer(howMany, from, to);
          }
        }
      }

      char[] tops = new char[stacks.size()];
      for (int i = 0; i < tops.length; i++) {
        tops[i] = stacks.get(i).peek();
      }
      return new String(tops);
    }
  }

  private static <T> void transfer(int n, Deque<? extends T> from, Deque<? super T> to) {
    for (int i = 0; i < n; i++) {
      to.push(from.pop());
    }
  }
}
