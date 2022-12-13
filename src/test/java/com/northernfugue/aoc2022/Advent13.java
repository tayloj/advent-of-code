package com.northernfugue.aoc2022;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Advent13 {

  @Test
  public void day13() throws IOException {
    assertEquals(Arrays.asList(13, 140),
        day13(Util.inputForClass(Advent13.class, "sample.txt")));

    assertEquals(Arrays.asList(5013, 25038),
        day13(Util.inputForClass(Advent13.class, "input.txt")));
  }

  public List<Integer> day13(Path p) throws IOException {
    List<List<Object>> pairs = readPairs(p);

    int sumOfIndicesOfInOrderPairs = 0;
    for (int i = 0, n = pairs.size(); i < n; i++) {
      if (-1 == compare(pairs.get(i).get(0), pairs.get(i).get(1))) {
        sumOfIndicesOfInOrderPairs += (i + 1); // 1-based indexing
      }
    }

    List<Object> two = parseList("[[2]]"), six = parseList("[[6]]");
    pairs.add(Arrays.asList(two, six));
    List<Object> allPairs = pairs.stream().flatMap(List::stream)
        .sorted(Advent13::compare)
        .collect(Collectors.toCollection(ArrayList::new));
    int productOfIndicesOfTwoAndSixWhenOrdered =
        (1 + allPairs.indexOf(two)) * (1 + allPairs.indexOf(six));

    return Arrays.asList(sumOfIndicesOfInOrderPairs, productOfIndicesOfTwoAndSixWhenOrdered);
  }

  private static List<List<Object>> readPairs(Path p) throws IOException {
    List<List<Object>> result = new ArrayList<>();
    try (BufferedReader reader = Files.newBufferedReader(p)) {
      String line = null;
      while (null != (line = reader.readLine())) {
        result.add(Arrays.asList(parseList(line), parseList(reader.readLine())));
        reader.readLine(); // consume blank
      }
    }
    return result;
  }

  private static List<Object> toList(Object x) {
    @SuppressWarnings("unchecked")
    List<Object> result = x instanceof List ? List.class.cast(x) : Collections.singletonList(x);
    return result;
  }

  private static int compare(Object left, Object right) {
    // case 1
    if (left instanceof Integer && right instanceof Integer) {
      return ((Integer) left).compareTo((Integer) right);
    }

    Iterator<Object> ll = toList(left).iterator(), rr = toList(right).iterator();
    while (true) {
      boolean hasLeft = ll.hasNext(), hasRight = rr.hasNext();
      if (!hasLeft && !hasRight) {
        return 0;
      } else if (!hasLeft && hasRight) {
        return -1;
      } else if (hasLeft && !hasRight) {
        return 1;
      } else {
        int cmp = compare(ll.next(), rr.next());
        if (cmp != 0) {
          return cmp;
        }
      }
    }
  }

  @Test
  public void testParseList() {
    assertEquals(Arrays.asList(1, 2, 3), parseList("[1,2,3]"));
    assertEquals(Arrays.asList(1, Arrays.asList(2, 4), 3), parseList("[1,[2,4],3]"));
    assertEquals(Arrays.asList(Arrays.asList(2)), parseList("[[2]]"));
  }

  public static List<Object> parseList(String s) {
    String justNumbers = s.replace(',', ' ').replace('[', ' ').replace(']', ' ');
    Deque<List<Object>> stack = new ArrayDeque<>();
    stack.push(new ArrayList<>());
    for (int i = 1, n = s.length() - 1; i < n; i++) {
      switch (s.charAt(i)) {
        case ',':
          break;
        case ']':
          stack.pop();
          break;
        case '[':
          List<Object> newList = new ArrayList<>();
          stack.peek().add(newList);
          stack.push(newList);
          break;
        default:
          int end = justNumbers.indexOf(' ', i + 1);
          stack.peek().add(Integer.parseInt(justNumbers.substring(i, end)));
          i = end - 1;
      }
    }
    return stack.pop();
  }
}
