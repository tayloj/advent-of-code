package com.northernfugue.aoc2022;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class Advent9 {

  private static int[][] deltas = new int['U' + 1][]; // enough space!
  static {
    deltas['U'] = new int[] {+1, +0};
    deltas['D'] = new int[] {-1, +0};
    deltas['L'] = new int[] {+0, -1};
    deltas['R'] = new int[] {+0, +1};
  }

  @Test(timeout = 2_000)
  public void day9() throws Exception {
    assertEquals(13, day9(2, Util.input("day9/sample.txt")));
    assertEquals(1, day9(10, Util.input("day9/sample.txt")));
    assertEquals(36, day9(10, Util.input("day9/sample2.txt")));
    assertEquals(6175, day9(2, Util.input("day9/input.txt")));
    assertEquals(2578, day9(10, Util.input("day9/input.txt")));
  }

  public int day9(int nk, Path p) throws IOException {
    int[][] knots = new int[nk][2]; // coords initialized to 0
    Set<Integer> tailPositions = new HashSet<>();
    tailPositions.add(knots[nk - 1][0] + knots[nk - 1][1] * 1_000_000);
    try (BufferedReader br = Files.newBufferedReader(p)) {
      String line;
      while (null != (line = br.readLine())) {
        for (int dir[] = deltas[line.charAt(0)],
            n = Integer.parseInt(line.substring(2)); n > 0; n--) {
          inc(knots[0], dir); // move head
          for (int k = 1; k < nk; k++) {
            chase(knots[k], knots[k - 1]);
          }
          tailPositions.add(knots[nk - 1][0] + knots[nk - 1][1] * 1_000_000);
        }
      }
    }
    return tailPositions.size();
  }

  private static void inc(int[] coord, int[] delta) {
    coord[0] += delta[0];
    coord[1] += delta[1];
  }

  private static void chase(int[] tail, int[] head) {
    int dRow = head[0] - tail[0], dCol = head[1] - tail[1];
    int manhattanDistance = Math.abs(dRow) + Math.abs(dCol);
    boolean sameRowOrCol = tail[0] == head[0] || tail[1] == head[1];
    if (manhattanDistance > 2 || (sameRowOrCol && manhattanDistance > 1)) {
      inc(tail, new int[] {Integer.signum(dRow), Integer.signum(dCol)});
    }
  }
}
