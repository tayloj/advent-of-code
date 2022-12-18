package com.northernfugue.aoc2022;

import com.northernfugue.aoc2021.AbstractDay;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Stream;

public class Advent18 extends AbstractDay {

  public Advent18() {
    super(64, 58, 4548, 2588);
  }

  @Override
  public List<Integer> dayN(Path p) throws IOException {
    Map<Integer, Map<Integer, Set<Integer>>> xy = new TreeMap<>();
    Map<Integer, Map<Integer, Set<Integer>>> xz = new TreeMap<>();
    Map<Integer, Map<Integer, Set<Integer>>> yz = new TreeMap<>();
    List<int[]> cubes = new ArrayList<>();
    for (String line : Files.readAllLines(p)) {
      /*
       * increasing all coordinates by 1 means we're guaranteed to have space in the 0-planes, and
       * can flood from there.
       */
      int[] xyz =
          Arrays.stream(line.split(",")).mapToInt(Integer::parseInt).map(n -> n + 1).toArray();
      cubes.add(xyz);
      xy.computeIfAbsent(xyz[0], ignore -> new TreeMap<>())
          .computeIfAbsent(xyz[1], ignore -> new TreeSet<>()).add(xyz[2]);
      xz.computeIfAbsent(xyz[0], ignore -> new TreeMap<>())
          .computeIfAbsent(xyz[2], ignore -> new TreeSet<>()).add(xyz[1]);
      yz.computeIfAbsent(xyz[1], ignore -> new TreeMap<>())
          .computeIfAbsent(xyz[2], ignore -> new TreeSet<>()).add(xyz[0]);
    }

    IntSummaryStatistics xxx = cubes.stream().mapToInt(c -> c[0]).summaryStatistics();
    IntSummaryStatistics yyy = cubes.stream().mapToInt(c -> c[1]).summaryStatistics();
    IntSummaryStatistics zzz = cubes.stream().mapToInt(c -> c[2]).summaryStatistics();

    int[] adj = {0};

    Stream.of(xy, xz, yz).forEach(d1d2 -> {
      d1d2.forEach((d1, d2s) -> {
        d2s.forEach((d2, d3s) -> {
          Set<Integer> air = new HashSet<>();
          if (!d3s.isEmpty()) {
            Iterator<Integer> d3it = d3s.iterator();
            int a = d3it.next(), b = -1;
            air.add(-(a - 1));
            while (d3it.hasNext()) {
              b = d3it.next();
              if (b == a + 1) {
                adj[0]++;
              } else {
                air.add(-(a + 1));
                air.add(-(b - 1));
              }
              a = b;
            }
            air.add(-(b + 1));
          }
          d3s.addAll(air);
        });
      });
    });

    int part1_exposedFaces = cubes.size() * 6 - adj[0] * 2;


    int xmax = xxx.getMax() + 2, ymax = yyy.getMax() + 2, zmax = zzz.getMax() + 2;

    int[][][] space = new int[xmax][ymax][zmax];
    // mark lava with MAX_VALUE
    xy.forEach((x, ys) -> ys.forEach((y, zs) -> zs
        .stream().filter(z -> z > 0)
        .forEach(z -> space[x][y][z] = 2)));

    int[] pm = {-1, +1};

    int part2_exposedExternalFaces = 0;

    Deque<int[]> points = new ArrayDeque<>();
    points.add(new int[] {0, 0, 0}); // guaranteed to be "outside"
    while (!points.isEmpty()) {
      int[] point = points.pop();
      int curr = space[point[0]][point[1]][point[2]];
      if (curr > 0) {
        continue; // already labelled (with label or otherwise);
      } else {
        space[point[0]][point[1]][point[2]] = 1;
      }
      for (int i = 0; i < 3; i++) {
        for (int d : pm) {
          int[] pp = Arrays.copyOf(point, 3);
          pp[i] += d;
          if (0 <= pp[0] && pp[0] < xmax &&
              0 <= pp[1] && pp[1] < ymax &&
              0 <= pp[2] && pp[2] < zmax) {
            if (space[pp[0]][pp[1]][pp[2]] == 2) {
              part2_exposedExternalFaces++;
            } else {
              points.add(pp);
            }
          }
        }
      }
    }

    return Arrays.asList(part1_exposedFaces, part2_exposedExternalFaces);
  }
}
