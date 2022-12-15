package com.northernfugue.aoc2022;

import com.northernfugue.aoc2021.AbstractDay;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Advent15 extends AbstractDay {

  public Advent15() {
    super(26, 56000011, 6078701, (int) 12567351400528L);
  }

  @Override
  public List<Integer> dayN(Path p) throws IOException {
    return p.toString().contains("sample")
        ? day(p, 10, 20)
        : day(p, 2_000_000, 4_000_000);
  }

  private static List<Integer> read(Path p) throws IOException {
    List<Integer> result = new ArrayList<>();
    Matcher m = Pattern.compile("Sensor at x=(.*), y=(.*): closest beacon is at x=(.*), y=(.*)")
        .matcher("");
    for (String line : Files.readAllLines(p)) {
      if (!m.reset(line).matches()) {
        throw new IllegalStateException();
      }
      for (int i = 1; i < 5; i++) {
        result.add(Integer.parseInt(m.group(i)));
      }
    }
    return result;
  }

  private int manhattanDist(int ax, int ay, int bx, int by) {
    return Math.abs(bx - ax) + Math.abs(by - ay);
  }

  private List<Integer> day(Path p, int y, int bound) throws IOException {

    List<Integer> data = read(p);

    List<int[]> records = new ArrayList<>();
    Set<List<Integer>> beacons = new HashSet<>();
    Set<List<Integer>> sensors = new HashSet<>();

    Iterator<Integer> ds = data.iterator();
    while (ds.hasNext()) {
      int sx = ds.next(), sy = ds.next();
      int bx = ds.next(), by = ds.next();
      records.add(new int[] {sx, sy, manhattanDist(sx, sy, bx, by)});
      sensors.add(Arrays.asList(sx, sy));
      beacons.add(Arrays.asList(bx, by));
    }

    class Helper {
      boolean couldHideBeacon(int x, int y) {
        List<Integer> xy = Arrays.asList(x, y);
        if (sensors.contains(xy) || beacons.contains(xy)) {
          return false;
        }
        for (int[] record : records) {
          if (manhattanDist(x, y, record[0], record[1]) <= record[2]) {
            return false;
          }
        }
        return true;
      }
    }
    Helper h = new Helper();

    // Check the requested row for part1
    int count = 0;
    int east = records.stream().mapToInt(x -> x[0] + x[2]).max().getAsInt();
    int west = records.stream().mapToInt(x -> x[0] - x[2]).min().getAsInt();
    for (int x = west; x < east + 1; x++) {
      List<Integer> xy = Arrays.asList(x, y);
      if (!beacons.contains(xy) &&
          !sensors.contains(xy) &&
          !h.couldHideBeacon(x, y)) {
        count++;
      }
    }

    // check the perimeter of each beacon
    long freq = -1;
    for (int[] record : records) {
      int sx = record[0], sy = record[1], d = record[2];
      if (0 < (freq = perimeter(sx, sy, d + 1)
          .filter(xy -> 0 <= xy[0] && xy[0] <= bound)
          .filter(xy -> 0 <= xy[1] && xy[1] <= bound)
          .filter(xy -> h.couldHideBeacon(xy[0], xy[1]))
          .map(loc -> BigInteger.valueOf(loc[1])
              .add(BigInteger.valueOf(4_000_000)
                  .multiply(BigInteger.valueOf(loc[0]))))
          .map(BigInteger::longValueExact)
          .findAny().orElse(-1L))) {
        break;
      }
    }

    // casting truncates, but that's checked above, too
    return Arrays.asList(count, (int) freq);
  }

  private Stream<int[]> perimeter(int x, int y, int d) {
    return IntStream.range(0, d + 1).mapToObj(dx -> Arrays.stream(new int[][] {
        {x - dx, y - (d - dx)},
        {x - dx, y + (d - dx)},
        {x + dx, y - (d - dx)},
        {x + dx, y + (d - dx)}
    })).flatMap(s -> s);
  }
}
