package s12;

import java.util.BitSet;
import java.util.Random;

public class MyBitSetTest {
  // ------------------------------------------------------------
  static void rndAddRm(Random r, MyBitSet s, BitSet bs, int i) {
    if (r.nextBoolean())
      if (s.get(i) != bs.get(i))
        throw new RuntimeException("bad answer for get()");
    if (r.nextBoolean()) {
      s.set(i);
      bs.set(i);
      // System.out.println("--added "+i+": "+s+"  "+bs);
    } else {
      s.clear(i);
      bs.clear(i);
      // System.out.println("rmed  "+i+": "+s+" "+bs);
    }
  }

  // ------------------------------------------------------------
  static boolean areSetEqual(MyBitSet s, BitSet bs) {
    if (bs.length() != s.length()) {
      System.out.println("wrong length : " + s.length() + " " + bs.length());
      return false;
    }
    if (bs.cardinality() != s.cardinality()) {
      System.out.println("wrong cardinality : " + s.cardinality() + " "
          + bs.cardinality());
      return false;
    }
    for (int i = 0; i < bs.length(); i++) {
      if (bs.get(i) != s.get(i)) {
        System.out.println("\nSetOf : " + s);
        System.out.println("BitSet: " + bs);
        System.out.println("Size: " + s.size());
        System.out.println("missing element : " + i);
        return false;
      }
    }
    int i;
    for (i = 0; i < bs.length(); i++) {
      if (bs.nextSetBit(i) != s.nextSetBit(i)) {
        System.out.println("nextSetBit(" + i + ") " + bs.nextSetBit(i) + " "
            + s.nextSetBit(i));
        return false;
      }
    }
    if (bs.nextSetBit(i) != s.nextSetBit(i)) {
      System.out.println("nextSetBit(" + i + ") " + bs.nextSetBit(i) + " "
          + s.nextSetBit(i));
      return false;
    }
    return true;
  }

  // ------------------------------------------------------------
  // testSet : Simple test method for the Set specification.
  // It verifies that an arbitrary sequence of add/remove
  // results in a correct set.
  // It verifies also the OR, AND and XOR methods
  // prm : n is the maximum size of the tested set (typically 100000).

  public static void testSet(int n) {
    MyBitSet s = new MyBitSet();
    BitSet bs = new BitSet();
    Random r = new Random();
    long seed = r.nextInt(1000);
    r.setSeed(seed);
    System.out.println("Using seed " + seed);
    testAddRm(s, bs, r, n);
    testOr(r, 100);
    testAnd(r, 100);
    testXor(r, 100);
  }

  public static void testOr(Random r, int n) {
    int i = 0;
    for (i = 0; i < 5; i++) {
      MyBitSet s1 = new MyBitSet();
      MyBitSet s2 = new MyBitSet();
      BitSet bs1 = new BitSet();
      BitSet bs2 = new BitSet();

      testAddRm(s1, bs1, r, n);
      testAddRm(s2, bs2, r, n / 2);
      s1.or(s2);
      bs1.or(bs2);
      if (!areSetEqual(s1, bs1))
        throw new RuntimeException("Error in OR  !");
      testAddRm(s1, bs1, r, n);
      s2.or(s1);
      bs2.or(bs1);
      if (!areSetEqual(s2, bs2))
        throw new RuntimeException("Error in OR!");
    }
  }

  public static void testAnd(Random r, int n) {
    int i = 0;
    for (i = 0; i < 5; i++) {
      MyBitSet s1 = new MyBitSet();
      MyBitSet s2 = new MyBitSet();
      BitSet bs1 = new BitSet();
      BitSet bs2 = new BitSet();

      testAddRm(s1, bs1, r, n);
      testAddRm(s2, bs2, r, n / 2);
      s1.and(s2);
      bs1.and(bs2);
      if (!areSetEqual(s1, bs1))
        throw new RuntimeException("Error in AND  !");
      testAddRm(s1, bs1, r, n);
      s2.and(s1);
      bs2.and(bs1);
      if (!areSetEqual(s2, bs2))
        throw new RuntimeException("Error in AND!");
    }
  }

  public static void testXor(Random r, int n) {
    int i = 0;
    for (i = 0; i < 5; i++) {
      MyBitSet s1 = new MyBitSet();
      MyBitSet s2 = new MyBitSet();
      BitSet bs1 = new BitSet();
      BitSet bs2 = new BitSet();

      testAddRm(s1, bs1, r, n);
      testAddRm(s2, bs2, r, n);
      s1.xor(s2);
      bs1.xor(bs2);
      if (!areSetEqual(s1, bs1))
        throw new RuntimeException("Error in XOR !");
      testAddRm(s1, bs1, r, n);
      s2.xor(s1);
      bs2.xor(bs1);
      if (!areSetEqual(s2, bs2))
        throw new RuntimeException("Error in AND!");
    }
  }

  public static void testAddRm(MyBitSet s, BitSet bs, Random r, int n) {
    int a = 1;
    int i = 0;
    for (i = 0; i < 10; i++) {
      if (!areSetEqual(s, bs))
        throw new RuntimeException("Error in add/remove !" + s.toString()
            + bs.toString());
      rndAddRm(r, s, bs, r.nextInt(n));
    }
    while (a < n) {
      for (i = 0; i < 4 * a; i++) {
        rndAddRm(r, s, bs, r.nextInt(n));
      }
      if (!areSetEqual(s, bs))
        throw new RuntimeException("Error in add/remove !");
      for (i = 0; i < a; i++) {
        int j = r.nextInt(n);
        s.set(j);
        bs.set(j);
      }
      a = (int) ((a + 1) * 1.8);
      System.out.print(".");
    }
    if (!areSetEqual(s, bs))
      throw new RuntimeException("Error in add/remove !");
  }

  // ------------------------------------------------------------
  public static void main(String[] args) {
    int n = 10000;
    if (args.length > 1) {
      System.out.println("Usage : SetOfStringsTest [n]");
      System.exit(-1);
    }
    if (args.length == 1)
      n = Integer.parseInt(args[0]);
    testSet(n);
    System.out.println("\nTest passed successfully");
  }
  // ------------------------------------------------------------
}
