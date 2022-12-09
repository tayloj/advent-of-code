package com.northernfugue.aoc2022;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

public class Advent8 {

  @Test
  public void day8() throws Exception {
    int[] sample = day8(Util.input("day8/sample.txt"));
    assertEquals(21, sample[0]);
    assertEquals(8, sample[1]);

    int[] input = day8(Util.input("day8/input.txt"));
    assertEquals(1713, input[0]);
    assertEquals(268464, input[1]);
  }

  public int[] day8(Path path) throws IOException {
    char[][] forest = surveyForest(path);
  
    int totalVisibility = totalVisibility(forest);
    
    int maxScenicScore = IntStream.range(0, forest.length)
        .flatMap(row -> IntStream.range(0, forest.length).map(col -> scenicScore(forest, row, col)))
        .max().orElseThrow(NoSuchElementException::new);
  
    return new int[] {totalVisibility, maxScenicScore};
  }

  private int countUntil(IntPredicate test, int start, int end, int delta) {
    int sum = 0;
    for (int i = start; i != end; i += delta) {
      sum++;
      if (test.test(i)) {
        break;
      }
    }
    return sum;
  }

  private int scenicScore(char[][] forest, int row, int col) {
    int n = forest.length;
    char h = forest[row][col];

    int vNorth = countUntil(north -> forest[north][col] >= h, row - 1, -1, -1);
    int vSouth = countUntil(south -> forest[south][col] >= h, row + 1, +n, +1);

    int vEast = countUntil(east -> forest[row][east] >= h, col + 1, +n, +1);
    int vWest = countUntil(west -> forest[row][west] >= h, col - 1, -1, -1);

    return vNorth * vEast * vSouth * vWest;
  }

  private char[][] surveyForest(Path p) throws IOException {
    try (BufferedReader r = Files.newBufferedReader(p)) {
      String line = r.readLine();
      int n = line.length();
      char[][] g = new char[n][n];
      int row = 0;
      while (line != null) {
        System.arraycopy(line.toCharArray(), 0, g[row], 0, n);
        row++;
        line = r.readLine();
      }
      return g;
    }
  }

  private int totalVisibility(char[][] forest) {
    int n = forest.length;
    int[][] vis = new int[n][n]; // initialized to 0

    for (int row = 0; row < n; row++) {
      // left to right
      int max = forest[row][0] - 1;
      for (int col = 0; col < n; col++) {
        int next = forest[row][col];
        if (next > max) {
          vis[row][col]++;
          max = next;
        }
      }

      // right to left
      max = forest[row][n - 1] - 1;
      for (int col = n - 1; col > -1; col--) {
        int next = forest[row][col];
        if (next > max) {
          vis[row][col]++;
          max = next;
        }
      }
    }

    for (int col = 0; col < n; col++) {
      // top to bottom
      int max = forest[0][col] - 1;
      for (int row = 0; row < n; row++) {
        int next = forest[row][col];
        if (next > max) {
          vis[row][col]++;
          max = next;
        }
      }

      // bottom to top
      max = forest[n - 1][col] - 1;
      for (int row = n - 1; row > -1; row--) {
        int next = forest[row][col];
        if (next > max) {
          vis[row][col]++;
          max = next;
        }
      }
    }

    int totalVis = n * 4 - 4; // outside minus double counted corners
    for (int row = 1; row < n - 1; row++) {
      for (int col = 1; col < n - 1; col++) {
        totalVis += (vis[row][col] > 0 ? 1 : 0);
      }
    }
    return totalVis;
  }
}
