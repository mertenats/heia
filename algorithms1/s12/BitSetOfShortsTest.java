package s12;

import java.util.BitSet;
import java.util.Random;

public class BitSetOfShortsTest {
  static final short D = 1000;

  // ------------------------------------------------------------
  static void rndAddRm(Random r, BitSetOfShorts s, BitSet bs, short i) {
    if (r.nextBoolean()) {
      s.add((short) (i - D));
      bs.set(i);
      // System.out.println("--added "+i+": "+s+"  "+bs);
    } else {
      s.remove((short) (i - D));
      bs.clear(i);
      // System.out.println("rmed  "+i+": "+s+" "+bs);
    }
  }

  // ------------------------------------------------------------
  static boolean areSetEqual(BitSetOfShorts s, BitSet bs) {
    if (s.size() != bs.cardinality()) {
      System.out.println("Size does not match");
      return false;
    }
    BitSetOfShortsItr itr = new BitSetOfShortsItr(s);
    while (itr.hasMoreElements()) {
      short e = itr.nextElement();
      if (!bs.get(e + D)) {
        System.out.println("bug at element : " + e);
        return false;
      }
    }
    for (int i = 0; i < bs.length(); i++) {
      if (bs.get(i) != s.contains((short) (i - D))) {
        System.out.println("bug at element : " + (i - D));
        return false;
      }
    }
    return true;
  }

  // ------------------------------------------------------------
  // testSet : Simple test method for the Set specification.
  // It only verifies that an arbitrary sequence of add/remove
  // results in a correct set.
  // prm : n is the maximum size of the tested set (typically 30000).

  public static void testSet(short n) {
    BitSetOfShorts s = new BitSetOfShorts();
    BitSet bs = new BitSet();
    Random r = new Random();
    long seed = r.nextInt(1000);
    r.setSeed(seed);
    System.out.println("Using seed " + seed);
    testAddRm(s, bs, n, r);
    testUnion(n, r);
    testIntersection(n, r);
  }

  public static void testAddRm(BitSetOfShorts s, BitSet bs, short n, Random r) {
    int a = 1;
    int i = 0;
    for (i = 0; i < 10; i++) {
      if (!areSetEqual(s, bs))
        throw new RuntimeException("Error in add/remove. BitSetOfShorts:"
            + s.toString());
      rndAddRm(r, s, bs, (short) (r.nextInt(n)));
    }
    while (a < n) {
      for (i = 0; i < 4 * a; i++) {
        rndAddRm(r, s, bs, (short) (r.nextInt(n)));
      }
      if (!areSetEqual(s, bs))
        throw new RuntimeException("Error in add/remove. BitSetOfShorts:"
            + s.toString());
      for (i = 0; i < a; i++) {
        int j = r.nextInt(n);
        s.add((short) (j - D));
        bs.set(j);
      }
      a = (int) ((a + 1) * 1.8);
      // System.out.print(".");
    }
    if (!areSetEqual(s, bs))
      throw new RuntimeException("Error in add/remove. BitSetOfShorts:"
          + s.toString());
  }

  public static void testUnion(short n, Random r) {
    BitSetOfShorts s1 = new BitSetOfShorts();
    BitSet bs1 = new BitSet();
    BitSetOfShorts s2 = new BitSetOfShorts();
    BitSet bs2 = new BitSet();

    int i = 0;
    for (i = 0; i < 5; i++) {
      testAddRm(s1, bs1, n, r);
      testAddRm(s2, bs2, n, r);
      s1.union(s2);
      bs1.or(bs2);
      if (!areSetEqual(s1, bs1))
        throw new RuntimeException("Error in union !");
    }
  }

  public static void testIntersection(short n, Random r) {
    BitSetOfShorts s1 = new BitSetOfShorts();
    BitSet bs1 = new BitSet();
    BitSetOfShorts s2 = new BitSetOfShorts();
    BitSet bs2 = new BitSet();

    int i = 0;
    for (i = 0; i < 5; i++) {
      testAddRm(s1, bs1, n, r);
      testAddRm(s2, bs2, n, r);
      s1.intersection(s2);
      bs1.and(bs2);
      if (!areSetEqual(s1, bs1))
        throw new RuntimeException("Error in intersection !");
    }
  }

  // ------------------------------------------------------------
  public static void main(String[] args) {
    short n = 10000;
    if (args.length > 1) {
      System.out.println("Usage : SetOfStringsTest [n]");
      System.exit(-1);
    }
    if (args.length == 1)
      n = Short.parseShort(args[0]);
    testSet(n);
    System.out.println("\nTest passed successfully");
  }
  // ------------------------------------------------------------
}