package com.northernfugue.aoc2022;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

public class Advent20 {

  @Test(timeout = 2_000)
  public void day20() throws IOException {
    long samplePart1 = day20(Util.inputForClass(Advent20.class, "sample.txt"), 1, 1);
    assertEquals(3, samplePart1);

    long samplePart2 = day20(Util.inputForClass(Advent20.class, "sample.txt"), 811589153, 10);
    assertEquals(1623178306L, samplePart2);

    long inputPart1 = day20(Util.inputForClass(Advent20.class, "input.txt"), 1, 1);
    assertEquals(10707L, inputPart1);

    long inputPart2 = day20(Util.inputForClass(Advent20.class, "input.txt"), 811589153, 10);
    assertEquals(2488332343098L, inputPart2);
  }

  public long day20(Path p, int key, int reps) throws IOException {
    /*
     * Read in the nodes. We keep a list of all the nodes in the original order, and the original
     * head of the ring, and a reference to the node that contains 0.
     */
    List<RingNode> nodes = new ArrayList<>();
    RingNode head = new RingNode();

    RingNode zero = null;
    nodes.add(head);
    try (Scanner s = new Scanner(p)) {
      long e = s.nextLong() * key;
      head.normalizedElement = e;
      head.originalElement = e;
      if (head.normalizedElement == 0) {
        zero = head;
      }

      RingNode curr = head;
      while (s.hasNextInt()) {
        curr.next = new RingNode();
        nodes.add(curr.next);
        curr.next.prev = curr;
        e = s.nextLong() * key;
        curr.next.normalizedElement = e;
        curr.next.originalElement = e;
        if (curr.next.normalizedElement == 0) {
          zero = curr.next;
        }
        curr = curr.next;
      }
      curr.next = head;
      head.prev = curr;
    }

    /*
     * Normalize the nodes so that we can always just move forward, and no farther than necessary.
     */
    long size = nodes.size();
    for (RingNode node : nodes) {
      node.normalizedElement %= (size - 1);
      if (node.normalizedElement < 0) {
        node.normalizedElement += (size - 1);
      }
    }

    /*
     * Advance all the nodes, however many times requested.
     */
    for (int t = 0; t < reps; t++) {
      for (RingNode node : nodes) {
        node.advance(node.normalizedElement);
      }
    }

    /*
     * Add up the nodes at 1000, 2000, and 3000.
     */
    return Stream.iterate(zero.at(1000), n -> n.at(1000))
        .limit(3)
        .mapToLong(x -> x.originalElement)
        .sum();
  }

  static class RingNode {
    /**
     * The original element, used for summation at the end.
     */
    long originalElement;

    /**
     * The normalized element, adjusted modulo the size of the list and always positive, so that
     * advances only need to be rightward.
     */
    long normalizedElement;

    /** The previous node. */
    RingNode prev;

    /** The next node. */
    RingNode next;

    /**
     * Advances a node {@code n} hops.
     *
     * @param hops the number of hops
     */
    void advance(long hops) {
      if (hops == 0) {
        return;
      }
      RingNode x = at(hops); // insert ourselves after x;
      RingNode y = x.next;

      RingNode a = prev;
      RingNode b = this;
      RingNode c = next;

      a.next = c;
      c.prev = a;

      x.next = b;
      b.prev = x;

      b.next = y;
      y.prev = b;
    }

    /**
     * Returns the node n hops ahead.
     *
     * @param hops the n
     * @return the node
     */
    RingNode at(long hops) {
      RingNode curr = this;
      for (long i = 0; i < hops; i++) {
        curr = curr.next;
      }
      return curr;
    }
  }
}
