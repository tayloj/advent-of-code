package com.northernfugue.aoc2022;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.BufferedReader;
import java.nio.file.Files;

public class Advent3 {

  enum Play {
    Rock, Paper, Scissor;

    static Play parseAsPlay(char c) {
      return Play.values()["ABCXYZ".indexOf(c) % 3];
    }

    static int score(Play them, Play us) {
      if (them == us) {
        return 3;
      } else if (((them.ordinal() + 1) % 3) == us.ordinal()) {
        return 6;
      } else {
        return 0;
      }
    }
  }

  enum Outcome {
    Lose, Draw, Win;

    static Outcome parseAsOutcome(char c) {
      return Outcome.values()["XYZ".indexOf(c) % 3];
    }

    static Play playForOutcome(Outcome o, Play them) {
      int delta = o == Lose
          ? -1
          : o == Draw
              ? 0
              : 1;

      return Play.values()[(them.ordinal() + 3 + delta) % 3];
    }
  }

  @Test
  public void check() {
    assertTrue(3 == Play.score(Play.Rock, Play.Rock));
    assertTrue(0 == Play.score(Play.Paper, Play.Rock));
    assertTrue(6 == Play.score(Play.Scissor, Play.Rock));

    assertTrue(6 == Play.score(Play.Rock, Play.Paper));
    assertTrue(3 == Play.score(Play.Paper, Play.Paper));
    assertTrue(0 == Play.score(Play.Scissor, Play.Paper));

    assertTrue(0 == Play.score(Play.Rock, Play.Scissor));
    assertTrue(6 == Play.score(Play.Paper, Play.Scissor));
    assertTrue(3 == Play.score(Play.Scissor, Play.Scissor));
  }

  @Test
  public void checkParseAsPlay() {
    assertSame(Play.Rock, Play.parseAsPlay('X'));
    assertSame(Play.Paper, Play.parseAsPlay('Y'));
    assertSame(Play.Scissor, Play.parseAsPlay('Z'));

    assertSame(Play.Rock, Play.parseAsPlay('A'));
    assertSame(Play.Paper, Play.parseAsPlay('B'));
    assertSame(Play.Scissor, Play.parseAsPlay('C'));
  }

  @Test
  public void checkParseAsOutcome() {
    assertSame(Outcome.Lose, Outcome.parseAsOutcome('X'));
    assertSame(Outcome.Draw, Outcome.parseAsOutcome('Y'));
    assertSame(Outcome.Win, Outcome.parseAsOutcome('Z'));
  }

  @Test
  public void checkPlayForOutcome() {
    assertSame(Play.Scissor, Outcome.playForOutcome(Outcome.Lose, Play.Rock));
    assertSame(Play.Rock, Outcome.playForOutcome(Outcome.Draw, Play.Rock));
    assertSame(Play.Paper, Outcome.playForOutcome(Outcome.Win, Play.Rock));

    assertSame(Play.Rock, Outcome.playForOutcome(Outcome.Lose, Play.Paper));
    assertSame(Play.Paper, Outcome.playForOutcome(Outcome.Draw, Play.Paper));
    assertSame(Play.Scissor, Outcome.playForOutcome(Outcome.Win, Play.Paper));

    assertSame(Play.Paper, Outcome.playForOutcome(Outcome.Lose, Play.Scissor));
    assertSame(Play.Scissor, Outcome.playForOutcome(Outcome.Draw, Play.Scissor));
    assertSame(Play.Rock, Outcome.playForOutcome(Outcome.Win, Play.Scissor));
  }

  static class Game {

    private int total = 0;

    void play(Play them, Play us) {
      total += us.ordinal() + 1;
      total += Play.score(them, us);
    }
  }

  @Test
  public void example1() {
    Game g = new Game();
    g.play(Play.Rock, Play.Paper);
    g.play(Play.Paper, Play.Rock);
    g.play(Play.Scissor, Play.Scissor);
    assertEquals(15, g.total);
  }

  @Test(timeout = 5_000)
  public void part1() throws Exception {
    Game g = new Game();
    try (BufferedReader rr = Files.newBufferedReader(Util.input("day3/input.txt"))) {
      String line = null;
      while (null != (line = rr.readLine())) {
        String[] parts = line.split(" ");
        Play them = Play.parseAsPlay(parts[0].charAt(0));
        Play us = Play.parseAsPlay(parts[1].charAt(0));
        g.play(them, us);
      }
    }
    assertEquals(11386, g.total);
  }

  @Test
  public void example2() {
    Game g = new Game();
    g.play(Play.Rock, Outcome.playForOutcome(Outcome.Draw, Play.Rock));
    g.play(Play.Paper, Outcome.playForOutcome(Outcome.Lose, Play.Paper));
    g.play(Play.Scissor, Outcome.playForOutcome(Outcome.Win, Play.Scissor));
    assertEquals(12, g.total);
  }

  @Test(timeout = 5_000)
  public void part2() throws Exception {
    Game g = new Game();
    try (BufferedReader rr = Files.newBufferedReader(Util.input("day3/input.txt"))) {
      String line = null;
      while (null != (line = rr.readLine())) {
        String[] parts = line.split(" ");
        Play them = Play.parseAsPlay(parts[0].charAt(0));
        Outcome desiredOutcome = Outcome.parseAsOutcome(parts[1].charAt(0));
        Play us = Outcome.playForOutcome(desiredOutcome, them);
        g.play(them, us);
      }
    }
    assertEquals(13600, g.total);
  }

}
