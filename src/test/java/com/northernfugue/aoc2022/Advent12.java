package com.northernfugue.aoc2022;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.stream.IntStream;

public class Advent12 {

  @Test(timeout = 5_000)
  public void day12() throws IOException {
    int[] s = day12(Util.inputForClass(Advent12.class, "sample.txt"));
    assertEquals(31, s[0]);
    assertEquals(29, s[1]);

    int[] i = day12(Util.inputForClass(Advent12.class, "input.txt"));
    assertEquals(339, i[0]);
    assertEquals(332, i[1]);
  }

  public int[] day12(Path p) throws IOException {
    List<String> lines = Files.readAllLines(p);
    char[][] map =
        lines.stream().map(line -> line.toCharArray())
            .toArray(r -> new char[r][lines.get(0).length()]);

    int[] start = find('S', map);
    int[] goal = find('E', map);
    map[start[0]][start[1]] = 'a';
    map[goal[0]][goal[1]] = 'z';
    int[][] distance = distance(map, goal);

    // part1 is the distance to 'S' from 'E'
    int part1 = distance[start[0]][start[1]];

    // part2 is the minimum distance to any 'a' from 'E'
    int part2 = IntStream.range(0, map.length)
        .flatMap(r -> IntStream.range(0, map[0].length).filter(c -> map[r][c] == 'a')
            .map(c -> distance[r][c]))
        .filter(x -> x != -1)
        .min().orElse(Integer.MIN_VALUE);

    return new int[] {part1, part2};
  }

  /**
   * Find the the distance to a destination point from every point in a map.
   *
   * @param map the map
   * @param destination the destination
   * @return the distances
   */
  private int[][] distance(char[][] map, int[] destination) {
    int rmax = map.length;
    int cmax = map[0].length;

    boolean visited[][] = new boolean[rmax][cmax];
    int distance[][] = new int[rmax][cmax];
    for (int[] r : distance) {
      Arrays.fill(r, -1);
    }

    PriorityQueue<int[]> q = new PriorityQueue<>(Comparator.comparingInt(e -> e[0]));
    // add a fake 0-length edge leading to the start point
    q.add(new int[] {0, destination[0], destination[1], destination[0], destination[1]});
    while (!q.isEmpty()) {
      int[] e = q.poll();
      int tr = e[1];
      int tc = e[2];

      if (!(0 <= tr && tr < rmax && 0 <= tc && tc < cmax)) {
        continue; // outside of the grid
      }

      if (visited[tr][tc]) {
        continue; // already been here
      }

      int sr = e[3];
      int sc = e[4];
      int newHeight = map[tr][tc];
      int oldHeight = map[sr][sc];
      if (!(newHeight >= oldHeight - 1)) {
        continue; // can't go more than one step
      }

      int td = e[0];
      visited[tr][tc] = true;
      distance[tr][tc] = td;

      q.add(new int[] {td + 1, tr + 1, tc + 0, tr, tc});
      q.add(new int[] {td + 1, tr - 1, tc + 0, tr, tc});
      q.add(new int[] {td + 1, tr + 0, tc + 1, tr, tc});
      q.add(new int[] {td + 1, tr + 0, tc - 1, tr, tc});
    }
    return distance;
  }

  private static int[] find(char target, char[][] grid) {
    return IntStream.range(0, grid.length).mapToObj(r -> r)
        .flatMap(r -> IntStream.range(0, grid[r].length)
            .filter(c -> grid[r][c] == target)
            .mapToObj(c -> new int[] {r, c}))
        .findFirst()
        .orElseThrow(NoSuchElementException::new);
  }
}
