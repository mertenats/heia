package s14;

import java.util.BitSet;
import java.util.Random;

public class SetOfStringsTest {
  // ------------------------------------------------------------
  static void rndAddRm(Random r, SetOfStrings s, BitSet bs, int i) {
    if (r.nextBoolean()) {
      s.add("" + i);
      bs.set(i);
    } else {
      s.remove("" + i);
      bs.clear(i);
    }
  }

  // ------------------------------------------------------------
  static boolean areSetEqual(SetOfStrings s, BitSet bs) {
    int l = 0;
    for (int i = 0; i < bs.length(); i++) {
      if (bs.get(i) != s.contains("" + i)) {
        System.out.println("\nSetOf : " + s);
        System.out.println("BitSet: " + bs);
        System.out.println("Size: " + s.size());
        System.out.println("conflicting element : " + i);
        return false;
      }
      if (s.contains("" + i))
        l++;
    }
    if (l != s.size()) {
      System.out.println("\nSetOf : " + s);
      System.out.println("BitSet: " + bs);
      System.out.println("Size: " + s.size());
      System.out.println("bad size...");
      return false;
    }
    return true;
  }

  // ------------------------------------------------------------
  public static void testItr(SetOfStrings s) {
    // ---- test itr
    int x = 0;
    SetOfStrings s2 = new SetOfStrings();
    SetOfStringsItr ai = new SetOfStringsItr(s);
    String e = "";
    while (ai.hasMoreElements()) {
      e = ai.nextElement();
      x++;
      s2.add(e);
      if (!s.contains(e))
        throw new RuntimeException("oups ! The iterator gives an absent elt");
    }
    if (x != s.size() && x != s2.size())
      throw new RuntimeException("Error in iterator !");
  }

  // ------------------------------------------------------------
  // testSet : Simple test method for the Set specification.
  // It verifies that an arbitrary sequence of add/remove
  // results in a correct set.
  // It verifies the union and intersection methods
  // prm : n is the time in tenths of seconds.

  public static void testSet(int n) {
    SetOfStrings s = new SetOfStrings(1);
    BitSet bs = new BitSet();
    Random r = new Random();
    long seed = r.nextInt(1000);
    r.setSeed(seed);
    System.out.println("Using seed " + seed);
    System.out.println("Testing add/remove... ");
    testAddRm(s, bs, r, n / 2);
    System.out.println("Testing iterator... ");
    testItr(s);
    System.out.println("Testing union... ");
    testUnion(n / 4, r);
    System.out.println("Testing intersection... ");
    testIntersection(n / 4, r);
  }

  public static void testAddRm(SetOfStrings s, BitSet bs, Random r, int n) {
    int i = 0;
    for (i = 0; i < 10; i++) {
      if (!areSetEqual(s, bs))
        throw new RuntimeException("Error in add/remove !");
      rndAddRm(r, s, bs, r.nextInt(10000));
    }
    timerSet(n);
    while (!timerOver()) {
      rndAddRm(r, s, bs, r.nextInt(10000));
      if (!areSetEqual(s, bs))
        throw new RuntimeException("Error in add/remove !");
    }
    if (!areSetEqual(s, bs))
      throw new RuntimeException("Error in add/remove !");
  }

  public static void testUnion(int n, Random r) {
    SetOfStrings s1 = new SetOfStrings();
    BitSet bs1 = new BitSet();
    SetOfStrings s2 = new SetOfStrings();
    BitSet bs2 = new BitSet();

    timerSet(n);
    while (!timerOver()) {
      testAddRm(s1, bs1, r, 1);
      testAddRm(s2, bs2, r, 1);
      s1.union(s2);
      bs1.or(bs2);
      if (!areSetEqual(s1, bs1))
        throw new RuntimeException("Error in union !");
    }
  }

  public static void testIntersection(int n, Random r) {
    SetOfStrings s1 = new SetOfStrings();
    BitSet bs1 = new BitSet();
    SetOfStrings s2 = new SetOfStrings();
    BitSet bs2 = new BitSet();

    timerSet(n);
    while (!timerOver()) {
      testAddRm(s1, bs1, r, 1);
      testAddRm(s2, bs2, r, 1);
      s1.intersection(s2);
      bs1.and(bs2);
      if (!areSetEqual(s1, bs1))
        throw new RuntimeException("Error in intersection !");
    }
  }

  // ------------------------------------------------------------
  static long endTime;

  static void timerSet(long duration) {
    endTime = 100 * duration + System.currentTimeMillis();
  }

  static boolean timerOver() {
    return System.currentTimeMillis() >= endTime;
  }

  // ------------------------------------------------------------
  public static void main(String[] args) {
    int n = 30;
    if (args.length == 1) {
      n = Integer.parseInt(args[0]);
    }
    testSet(n);
    System.out.println("\nTest passed successfully");
  }
  // ------------------------------------------------------------
}
