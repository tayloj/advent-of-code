package com.northernfugue.aoc2022;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Advent23 {

  private static final Comparator<int[]> byRowThenCol =
      Comparator.<int[]>comparingInt(x -> x[0])
          .thenComparingInt(x -> x[1]);

  private static final int[][] MASKS = {
      {-1, -1, -1, +0, -1, +1},
      {+1, -1, +1, +0, +1, +1},
      {-1, -1, +0, -1, +1, -1},
      {-1, +1, +0, +1, +1, +1},
  };

  @Test(timeout = 5_000)
  public void day23() throws IOException {
    day23(Util.inputForClass(Advent23.class, "sample-small.txt"));
    day23(Util.inputForClass(Advent23.class, "sample.txt"));
    day23(Util.inputForClass(Advent23.class, "input.txt"));
  }

  void day23(Path p) throws IOException {
    Set<int[]> elves = new TreeSet<int[]>(byRowThenCol);
    List<String> lines = Files.readAllLines(p);
    for (int i = 0, n = lines.size(); i < n; i++) {
      int j = -1;
      while (-1 != (j = lines.get(i).indexOf('#', j + 1))) {
        elves.add(new int[] {i, j});
      }
    }

    Map<int[], Set<int[]>> moves = new TreeMap<>(byRowThenCol);
    int rounds = -1, elvesMoved;
    do {
      elvesMoved = 0;
      rounds++;

      for (int[] elf : elves) {
        int coverage = coverage(elf, elves);
        if (coverage != 0) {
          for (int row = elf[0], col = elf[1], dir = 0, n = MASKS.length; dir < n; dir++) {
            int m = (dir + rounds) % n;
            boolean clearInDirection = (coverage & (1 << m)) == 0;
            if (clearInDirection) {
              int[] proposedMove = new int[] {row + MASKS[m][2], col + MASKS[m][3]};
              if (!elves.contains(proposedMove)) {
                moves.computeIfAbsent(proposedMove, ignore -> new TreeSet<int[]>(byRowThenCol))
                    .add(elf);
                break;
              }
            }
          }
        }
      }

      for (Map.Entry<int[], Set<int[]>> move : moves.entrySet()) {
        Set<int[]> competition = move.getValue();
        if (competition.size() < 2) {
          elvesMoved++;
          elves.add(move.getKey());
          elves.removeAll(competition);
        }
      }
      moves.clear();

      // part 1
      if (rounds == 9) {
        IntSummaryStatistics rr = elves.stream().mapToInt(x -> x[0]).summaryStatistics();
        IntSummaryStatistics cc = elves.stream().mapToInt(x -> x[1]).summaryStatistics();
        int empties =
            (1 + rr.getMax() - rr.getMin()) * (1 + cc.getMax() - cc.getMin()) - elves.size();
        System.out.println("empties after 10: " + empties);
      }
    } while (elvesMoved > 0);

    // part 2
    System.out.println("stopped after: " + (rounds + 1));
  }

  private static int coverage(int[] e, Set<int[]> elves) {
    int coverage = 0;
    for (int row = e[0], col = e[1], i = 0, n = MASKS.length; i < n; i++) {
      int[] mask = MASKS[i];
      if (elves.contains(new int[] {row + mask[0], col + mask[1]}) ||
          elves.contains(new int[] {row + mask[2], col + mask[3]}) ||
          elves.contains(new int[] {row + mask[4], col + mask[5]})) {
        coverage |= (1 << i);
      }
    }
    return coverage;
  }
}
