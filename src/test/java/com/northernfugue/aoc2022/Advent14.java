package com.northernfugue.aoc2022;

import com.northernfugue.aoc2021.AbstractDay;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class Advent14 extends AbstractDay {

  public Advent14() {
    super(24, 93, 592, 30367);
  }

  @Override
  public List<Integer> dayN(Path p) throws IOException {
    return Arrays.asList(dayN(p, false), dayN(p, true));
  }

  private int dayN(Path p, boolean part2) throws IOException {

    int left = Integer.MAX_VALUE, right = Integer.MIN_VALUE, bottom = Integer.MIN_VALUE;

    List<List<int[]>> paths = new ArrayList<>();
    try (BufferedReader reader = Files.newBufferedReader(p)) {
      String line;
      while (null != (line = reader.readLine())) {
        List<int[]> path = new ArrayList<>();
        String[] points = line.split(Pattern.quote(" -> "));
        for (String point : points) {
          String[] nums = point.split(Pattern.quote(","));
          int column = Integer.parseInt(nums[0]);
          int row = Integer.parseInt(nums[1]);
          // swap order for ROW x COL indexing
          path.add(new int[] {row, column});
          left = Math.min(left, column);
          right = Math.max(right, column);
          bottom = Math.max(bottom, row);
        }
        paths.add(path);
      }
    }

    /*
     * 1000 is "big enough" here, but this could possibly be done analytically, since the fallen
     * sand will be a full upward pointing triangle, except for the rock paths, and gaps underneath,
     * and those probably just have downward pointing triangles of space underneath.
     */
    if (part2) {
      bottom += 2;
      left -= bottom;
      right += bottom;
      paths.add(Arrays.asList(new int[] {bottom, left}, new int[] {bottom, right}));
    }

    int[] shift = {0, -left};

    // Shift left for 0-based indexing
    for (List<int[]> path : paths) {
      for (int[] point : path) {
        inc(point, shift);
      }
    }

    // allocate a grid
    int rr = 1 + bottom;
    int cc = 1 + right - left;
    char[][] grid = new char[rr][cc];
    for (char[] row : grid) {
      Arrays.fill(row, '.');
    }

    // establish the origin
    int[] origin = inc(new int[] {0, 500}, shift);
    grid[origin[0]][origin[1]] = '+';

    // fill grid with segments
    for (List<int[]> path : paths) {
      Iterator<int[]> points = path.iterator();
      int[] start = Arrays.copyOf(points.next(), 2);
      while (points.hasNext()) {
        int[] end = Arrays.copyOf(points.next(), 2);
        int[] delta = vec(end, start);
        int[] endEx = plus(end, delta);
        while (!Arrays.equals(start, endEx)) {
          grid[start[0]][start[1]] = '#';
          inc(start, delta);
        }
        start = end;
      }
    }

    // drop sand
    int[][] dirs = {{1, 0}, {1, -1}, {1, 1}};
    int grains = 0;
    try {
      for (int[] sand = Arrays.copyOf(origin, 2); 'o' != at(grid, sand); sand =
          Arrays.copyOf(origin, 2)) {
        freefall: while (true) {
          for (int[] dir : dirs) {
            if ('.' == at(grid, plus(sand, dir))) {
              inc(sand, dir);
              continue freefall;
            }
          }
          grid[sand[0]][sand[1]] = 'o';
          grains++;
          break; // outer while only happens once per grain
        }
      }
    } catch (ArrayIndexOutOfBoundsException e) {
      // OK, we're done
    }
    return grains;
  }

  private char at(char[][] grid, int[] p) {
    return grid[p[0]][p[1]];
  }

  private int[] inc(int[] point, int[] delta) {
    for (int i = 0, n = point.length; i < n; i++) {
      point[i] += delta[i];
    }
    return point;
  }

  private int[] plus(int[] a, int[] b) {
    return new int[] {a[0] + b[0], a[1] + b[1]};
  }

  private int[] vec(int[] end, int[] begin) {
    return new int[] {Integer.signum(end[0] - begin[0]), Integer.signum(end[1] - begin[1])};
  }
}
