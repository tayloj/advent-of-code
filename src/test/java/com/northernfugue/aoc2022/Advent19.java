package com.northernfugue.aoc2022;

import com.northernfugue.aoc2021.AbstractDay;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class Advent19 extends AbstractDay {

  public Advent19() {
    super(33, 42, 1616, 8990);
  }

  @Override
  public List<Integer> dayN(Path p) throws IOException {
    int part1 = 0;
    int part2 = 1;
    for (String line : Files.readAllLines(p)) {
      int[] b = Arrays.stream(line.split("[^0-9]+")).skip(1).mapToInt(Integer::parseInt).toArray();
      Sim ss = new Sim(new int[][] {{b[1], 0, 0, 0}, {b[2], 0, 0, 0}, {b[3], b[4], 0, 0}, {b[5], 0, b[6], 0}});
      part1 += b[0] * ss.run(24);
      if (b[0] < 4) { // product of first 3, and they start at 1
        part2 *= ss.run(32);
      }
    }
    return Arrays.asList(part1, part2);
  }

  static class Sim {

    private final int[][] costs;

    Sim(int[][] costs) {
      this.costs = costs;
    }

    public int run(int t) {
      int[] best = {0};
      int[] rate = new int[costs.length];
      rate[0] = 1;
      run(t, rate, new int[costs.length], best);
      return best[0];
    }

    private void run(int t, int[] rate, int[] wallet, int[] best) {
      if (t == 0) {
        best[0] = Math.max(best[0], wallet[3]);
        return;
      }

      for (int i = 0, n = costs.length; i < n; i++) {
        // If this isn't the last machine type (that produces the reward)
        // and there's already enough to cover any cost on the next turn,
        // don't bother making more of this type of machine.
        int ii = i;
        if (i != n - 1 && Arrays.stream(costs).allMatch(cost -> cost[ii] <= rate[ii])) {
          continue;
        }

        int[] cost = costs[i];
        int dt = availableTime(cost, wallet, rate);
        if (dt < t) {
          inc(wallet, rate, dt + 1);
          dec(wallet, cost, 1);
          rate[i]++;
          run(t - dt - 1, rate, wallet, best);
          rate[i]--;
          inc(wallet, cost, 1);
          dec(wallet, rate, dt + 1);
        } else {
          inc(wallet, rate, t);
          run(0, rate, wallet, best);
          dec(wallet, rate, t);
        }
      }
    }

    /**
     * Returns time units in which a cost will become affordable.
     * 
     * @param cost the cost
     * @param wallet the current wallet
     * @param rate the per unit rate of increase
     * @return the time delay
     */
    private static int availableTime(int[] cost, int[] wallet, int[] rate) {
      int best = 0;
      for (int i = 0, n = cost.length; i < n; i++) {
        if (wallet[i] >= cost[i]) {
          continue;
        } else if (cost[i] > 0 && rate[i] == 0) {
          return Integer.MAX_VALUE;
        } else {
          best = Math.max(best, divUp(cost[i] - wallet[i], rate[i]));
        }
      }
      return best;
    }

    /** Returns least z such that {@code z * y >= x}. */
    private static int divUp(int x, int y) {
      int z = x / y;
      return z * y == x ? z : (z + 1);
    }

    private static void inc(int[] a, int[] d, int m) {
      for (int i = 0, n = a.length; i < n; i++) {
        a[i] += d[i] * m;
      }
    }

    private static void dec(int[] a, int[] d, int m) {
      for (int i = 0, n = a.length; i < n; i++) {
        a[i] -= d[i] * m;
      }
    }
  }
}
