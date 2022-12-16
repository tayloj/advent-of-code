package com.northernfugue.aoc2022;

import com.northernfugue.aoc2021.AbstractDay;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Based on <a href="https://github.com/juanplopes/advent-of-code-2022/blob/main/day16.py">the
 * excellent solution by juanplopes</a>.
 */
public class Advent16 extends AbstractDay {

  public Advent16() {
    super(1651, 1707, 1820, 2602);
  }

  class DP {

    private final ShortestPaths<Valve> dists;
    private final List<Valve> unstuckValves;

    public DP(Map<String, Valve> vs) {
      this.dists =
          new ShortestPaths<>(vs.values(), v -> v.tunnelNames.stream().map(vs::get)::iterator);
      this.unstuckValves = vs.values().stream()
          .filter(v -> v.rate != 0)
          .collect(Collectors.toList());
    }

    public Map<Set<Valve>, Integer> value(
        Set<Valve> open,
        Valve u,
        int timeRemaining,
        int flowSoFar,
        Map<Set<Valve>, Integer> bestFlowByValveSet) {

      bestFlowByValveSet.merge(open, flowSoFar, Math::max);

      for (Valve v : unstuckValves) {
        int timeAfter = timeRemaining - dists.getDist(u, v) - 1;
        if (open.contains(v) || timeAfter <= 0) {
          continue;
        }
        value(adjoin(v, open), v, timeAfter, timeAfter * v.rate + flowSoFar, bestFlowByValveSet);
      }

      return bestFlowByValveSet;
    }
  }

  static class ShortestPaths<T> {

    private final Map<T, Map<T, Integer>> weights = new HashMap<>();

    public ShortestPaths(Iterable<T> nodes, Function<T, Iterable<T>> neighbors) {
      for (T x : nodes) {
        setDist(0, x, x);
        for (T y : neighbors.apply(x)) {
          setDist(1, x, y);
        }
      }
      for (T k : nodes) {
        for (T i : nodes) {
          for (T j : nodes) {
            setDist(Math.min(add(getDist(i, k), getDist(k, j)), getDist(i, j)), i, j);
          }
        }
      }
    }

    private int add(int x, int y) {
      return x == Integer.MAX_VALUE || y == Integer.MAX_VALUE
          ? Integer.MAX_VALUE
          : Math.addExact(x, y);
    }

    private Integer setDist(int d, T x, T y) {
      return weights.computeIfAbsent(x, ign -> new HashMap<>()).put(y, d);
    }

    public int getDist(T x, T y) {
      return weights.computeIfAbsent(x, ign -> new HashMap<>())
          .computeIfAbsent(y, ign2 -> Integer.MAX_VALUE);
    }
  }


  static class Valve {
    private static final Pattern linePattern =
        Pattern.compile("Valve (.*) has flow rate=(.*); tunnels? leads? to valves? (.*)");
    String name;
    int rate;
    List<String> tunnelNames;
    List<Valve> tunnels;

    public static Valve readFromString(String line) {
      Valve v = new Valve();
      Matcher m = linePattern.matcher(line);
      if (!m.matches()) {
        throw new IllegalStateException("bad line: " + line);
      }
      v.name = m.group(1);
      v.rate = Integer.parseInt(m.group(2));
      v.tunnelNames = Arrays.asList(m.group(3).split(", "));
      return v;
    }
  }

  @Override
  public List<Integer> dayN(Path p) throws IOException {
    // read and normalize the graph
    Map<String, Valve> vs = Files.readAllLines(p).stream()
        .map(Valve::readFromString)
        .collect(Collectors.toMap(x -> x.name, x -> x));

    Integer[] result = {0, 0};

    result[0] = new DP(new HashMap<>(vs))
        .value(Collections.emptySet(), vs.get("AA"), 30, 0, new HashMap<>())
        .values().stream().mapToInt(x -> x).max().orElse(-1);

    Map<Set<Valve>, Integer> p2 = new DP(new HashMap<>(vs))
        .value(Collections.emptySet(), vs.get("AA"), 26, 0, new HashMap<>());

    result[1] = p2.entrySet().stream()
        .flatMapToInt(kv1 -> p2.entrySet().stream()
            .filter(kv2 -> disjoint(kv1.getKey(), kv2.getKey()))
            .mapToInt(kv2 -> kv1.getValue() + kv2.getValue()))
        .max().orElse(-1);

    return Arrays.asList(result);
  }

  private static <T> boolean disjoint(Set<T> a, Set<T> b) {
    Set<T> ab = new HashSet<>(a);
    ab.addAll(b);
    return ab.size() == a.size() + b.size();
  }

  private static <T> Set<T> adjoin(T element, Set<T> s) {
    Set<T> ss = new HashSet<>(s);
    ss.add(element);
    return ss;
  }
}
