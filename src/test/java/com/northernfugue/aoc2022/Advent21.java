package com.northernfugue.aoc2022;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

public class Advent21 {

  @Test
  public void day21() throws IOException {
    long[] s = day21(Util.inputForClass(Advent21.class, "sample.txt"));
    assertEquals(152L, s[0]);
    assertEquals(301L, s[1]);

    long[] i = day21(Util.inputForClass(Advent21.class, "input.txt"));
    assertTrue(0 < i[0]);
    assertTrue(0 < i[1]);
  }

  public static long[] day21(Path p) throws IOException {
    Map<String, Expr> rules = Files.readAllLines(p).stream().map(Expr::parse)
        .collect(Collectors.toMap(expr -> expr.name, expr -> expr));
    for (Expr e : rules.values()) {
      e.lhs = rules.get(e.lhsName);
      e.rhs = rules.get(e.rhsName);
    }

    Expr root = rules.get("root");
    long part1 = root.eval();
    long part2 = isolate("humn", root).eval();
    return new long[] {part1, part2};
  }

  /**
   * Returns the name of an equation that has isolated {@code name} on the left.
   *
   * @param name the name to isolate
   * @param e this equation
   * @return the equation name
   */
  public static Expr isolate(String name, Expr e) {
    // ll op rr == r
    class Rewriter {
      /** Isolates name on the left, starting with {@code a = b o c}. */
      Expr solve(Expr a, Expr b, char o, Expr c) {
        Expr boc = new Expr(null, b.name, o, c.name);
        boc.lhs = b;
        boc.rhs = c;

        Expr ee = new Expr(null, a.name, '=', boc.name);
        ee.lhs = a;
        ee.rhs = boc;

        return isolate(name, ee);
      }

      Expr solve() {
        if (name.equals(e.lhsName)) {
          return e.rhs;
        } else if (name.equals(e.rhsName)) {
          return e.lhs;
        }

        /*
         * Extract the sides of the equation, and normalize so the term is in the new left.
         */
        Expr x = e.lhs.contains(name) ? e.lhs : e.rhs;
        boolean isolateLeft = x.lhs != null && x.lhs.contains(name);

        // @formatter:off
        if (isolateLeft) {
          switch (x.op) {
            case '+': return solve(x.lhs, e.rhs, '-', x.rhs); // ll + lr = r ::> ll = r - lr
            case '-': return solve(x.lhs, e.rhs, '+', x.rhs); // ll - lr = r ::> ll = r + lr
            case '*': return solve(x.lhs, e.rhs, '/', x.rhs); // ll * lr = r ::> ll = r / lr
            case '/': return solve(x.lhs, e.rhs, '*', x.rhs); // ll / lr = r ::> ll = r * lr
          }
        } else {
          switch (x.op) {
            case '+': return solve(x.rhs, e.rhs, '-', x.lhs); // ll + lr = r ::> lr = r - ll
            case '-': return solve(x.rhs, x.lhs, '-', e.rhs); // ll - lr = r ::> lr = ll - r
            case '*': return solve(x.rhs, e.rhs, '/', x.lhs); // ll * lr = r ::> lr = r / ll
            case '/': return solve(x.rhs, x.lhs, '/', e.rhs); // ll / lr = r ::> lr = ll / r
          }
        }
        // @formatter:on
        throw new IllegalStateException("bad op: " + x.op);
      }
    }
    return new Rewriter().solve();
  }

  static class Expr {
    /** expression is a constant value if op is the null char. */
    long value;
    final String name, lhsName, rhsName;
    Expr lhs, rhs;
    char op;

    Expr(String name, long value) {
      this(name, value, null, (char) 0, null);
    }

    Expr(String name, String left, char op, String right) {
      this(name, Long.MIN_VALUE, left, op, right);
    }

    Expr(String name, long value, String left, char op, String right) {
      this.value = value;
      this.name = name;
      this.lhsName = left;
      this.op = op;
      this.rhsName = right;
    }

    @Override
    public String toString() {
      if (op == 0) {
        return Long.toString(value);
      } else {
        return String.format("(%s %s %s)", lhs, op, rhs);
      }
    }

    static Expr parse(String line) {
      String name = line.substring(0, 4);
      String rule = line.substring(6);
      if (!rule.contains(" ")) {
        return new Expr(name, Long.parseLong(rule));
      } else {
        String[] parts = rule.split(" ");
        return new Expr(name, parts[0], parts[1].charAt(0), parts[2]);
      }
    }

    long eval() {
      if (this.op == 0) {
        return this.value;
      } else {
        long l = this.lhs.eval(), r = this.rhs.eval();
        // @formatter:off
        switch (this.op) {
          case '+': return l + r;
          case '-': return l - r;
          case '*': return l * r;
          case '/': return l / r;
          default:  throw new IllegalArgumentException("bad rule: " + this.name);
        }
        // @formatter:on
      }
    }

    boolean contains(String name) {
      return name.equals(this.name) || (op != 0 && (lhs.contains(name) || rhs.contains(name)));
    }
  }
}
