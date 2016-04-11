package s06;

import java.util.Iterator;
import java.util.Random;
import java.util.TreeMap;

// ------------------------------------------------------------
// ------------------------------------------------------------
// ------------------------------------------------------------
public class ShortToStringMapTest {
  static class S2SMap {
    TreeMap<Short, String> t = new TreeMap<Short, String>();

    // ------------------------------------------------------------
    public S2SMap() {
    }

    public void put(short key, String img) {
      t.put(key, img);
    }

    public String get(short key) {
      return t.get(key);
    }

    public void remove(short e) {
      t.remove(e);
    }

    public boolean containsKey(short k) {
      return t.containsKey(k);
    }

    public boolean isEmpty() {
      return size() == 0;
    }

    public int size() {
      return t.size();
    }

    public void union(S2SMap m) {
      t.putAll(m.t);
    }

    // ------------------------------------------------------------
    public void intersection(S2SMap s) {
      S2SMap a = new S2SMap();
      Iterator<Short> ti = s.t.keySet().iterator();
      while (ti.hasNext()) {
        short e = ti.next();
        if (containsKey(e))
          a.put(e, s.get(e));
      }
      t = a.t;
    }
  }

  // ------------------------------------------------------------
  // ------------------------------------------------------------
  static void rndAddRm(Random r, ShortToStringMap s, S2SMap g, short i) {
    if (r.nextBoolean()) {
      String v = "" + r.nextInt(10);
      s.put(i, v);
      g.put(i, v);
    } else {
      s.remove(i);
      g.remove(i);
    }
  }

  // ------------------------------------------------------------
  static boolean areSetEqual(ShortToStringMap s, S2SMap g) {
    int l = 0;
    short lastKey = 10;
    if (g.size() > 0)
      lastKey = g.t.lastKey();
    for (short i = 0; i <= lastKey; i++) {
      if (g.containsKey(i) != s.containsKey(i)) {
        System.out.println("\nMap is : " + s);
        System.out.println("should be: " + g.t);
        System.out.println("Size: " + s.size());
        System.out.println("bad element : " + i);
        return false;
      }
      if (s.containsKey(i)) {
        l++;
        if (!s.get(i).equals(g.get(i))) {
          System.out.println("bad image for element: " + i);
          return false;
        }
      }
    }
    if (l != s.size()) {
      System.out.println("\nMap is : " + s);
      System.out.println("should be: " + g.t);
      System.out.println("Size: " + s.size() + " " + g.size());
      System.out.println("too much elements...");
      return false;
    }
    return true;
  }

  // ------------------------------------------------------------
  public static void testUnion(int n, Random r) {
    ShortToStringMap s1 = new ShortToStringMap();
    ShortToStringMap s2 = new ShortToStringMap();
    S2SMap bs1 = new S2SMap();
    S2SMap bs2 = new S2SMap();
    timerSet(n);
    while (!timerOver()) {
      testAddRm(s1, bs1, r, 1);
      testAddRm(s2, bs2, r, 1);
      s1.union(s2);
      bs1.union(bs2);
      if (!areSetEqual(s1, bs1))
        throw new RuntimeException("Error in union !");
    }
  }

  // ------------------------------------------------------------
  public static void testIntersection(int n, Random r) {
    ShortToStringMap s1 = new ShortToStringMap();
    ShortToStringMap s2 = new ShortToStringMap();
    S2SMap bs1 = new S2SMap();
    S2SMap bs2 = new S2SMap();

    timerSet(n);
    while (!timerOver()) {
      testAddRm(s1, bs1, r, 1);
      testAddRm(s2, bs2, r, 1);
      s1.intersection(s2);
      bs1.intersection(bs2);
      if (!areSetEqual(s1, bs1))
        throw new RuntimeException("Error in intersection !");
    }
  }

  // ------------------------------------------------------------
  public static void testAddRm(ShortToStringMap s, S2SMap bs, Random r, int n) {
    int i = 0;
    for (i = 0; i < 10; i++) {
      if (!areSetEqual(s, bs))
        throw new RuntimeException("oups !");
      rndAddRm(r, s, bs, (short) (r.nextInt(10000)));
    }
    timerSet(n);
    while (!timerOver()) {
      rndAddRm(r, s, bs, (short) (r.nextInt(10000)));
      if (!areSetEqual(s, bs))
        break;
    }
    if (!areSetEqual(s, bs))
      throw new RuntimeException("Error in add/remove/contains !");
  }

  // ------------------------------------------------------------
  public static void testItr(ShortToStringMap s) {
    // ---- test itr
    int x = 0;
    ShortToStringMap s2 = new ShortToStringMap();
    ShortToStringMapItr ai = new ShortToStringMapItr(s);
    short e = 0;
    while (ai.hasMoreKeys()) {
      e = ai.nextKey();
      x++;
      s2.put(e, "");
      if (!s.containsKey(e))
        throw new RuntimeException("oups ! The iterator gives an absent elt");
    }
    if (x != s.size() && x != s2.size())
      throw new RuntimeException("Error in iterator !");
  }

  // ------------------------------------------------------------
  // testSet : Simple test method for the Set specification.
  // It verifies that an arbitrary sequence of add/remove
  // results in a correct set.
  // It verifies the union and the intersection
  // prm : n is the time in tenths of seconds.

  public static void testSet(int n) {
    ShortToStringMap s = new ShortToStringMap();
    S2SMap bs = new S2SMap();
    Random r = new Random();
    long seed = r.nextInt(1000);
    r.setSeed(seed);
    System.out.println("Using seed " + seed);
    testAddRm(s, bs, r, n / 2);
    testItr(s);
    testUnion(n / 4, r);
    testIntersection(n / 4, r);
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
    int nt = 30;
    if (args.length == 1)
      nt = Integer.parseInt(args[0]);
    testSet(nt);
    System.out.println("\nTest passed successfully !");
  }
}
