package s06;

import java.util.BitSet;
import java.util.Random;
//TODO better Random...
// ------------------------------------------------------------
// ------------------------------------------------------------
// ------------------------------------------------------------
public class SetOfShortsTest{
  static void rndAddRm(Random r, SetOfShorts s, BitSet bs, int i) {
    if (r.nextBoolean()) {
      s.add( (short) i);
      bs.set(i);
    }
    else {
      s.remove( (short) i);
      bs.clear(i);
    }
  }
  // ------------------------------------------------------------
  static boolean areSetEqual(SetOfShorts s, BitSet bs) {
    int l = 0;
    for (int i = 0; i < bs.length(); i++) {
      if (bs.get(i) != s.contains( (short) i)) {
        System.out.println("\nSetOf : " + s);
        System.out.println("BitSet: " + bs);
        System.out.println("Size: " + s.size());
        System.out.println("conflicting element : " + i);
        return false;
      }
      if (s.contains( (short) i))
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
  
  public static void testUnion(int n, Random r) {
    SetOfShorts s1 = new SetOfShorts();
    SetOfShorts s2 = new SetOfShorts();
    BitSet bs1 = new BitSet();
    BitSet bs2 = new BitSet();
    timerSet(n);
    while(!timerOver()) {
      testAddRm(s1, bs1, r, 1);
      testAddRm(s2, bs2, r, 1);
      s1.union(s2);
      bs1.or(bs2);
      if (!areSetEqual(s1, bs1))
        throw new RuntimeException("Error in union !");
    }
  }
  
  public static void testIntersection(int n, Random r) {
    SetOfShorts s1 = new SetOfShorts();
    SetOfShorts s2 = new SetOfShorts();
    BitSet bs1 = new BitSet();
    BitSet bs2 = new BitSet();
    
    timerSet(n);
    while(!timerOver()) {
      testAddRm(s1, bs1, r, 1);
      testAddRm(s2, bs2, r, 1);
      s1.intersection(s2);
      bs1.and(bs2);
      if (!areSetEqual(s1, bs1))
        throw new RuntimeException("Error in intersection !");
    }
  }
  
  public static void testAddRm(SetOfShorts s, BitSet bs, Random r, int n) {
    int i = 0;
    for (i = 0; i < 10; i++) {
      if (!areSetEqual(s, bs))
        throw new RuntimeException("oups !");
      rndAddRm(r, s, bs, r.nextInt(10000));
    }
    timerSet(n);
    while(!timerOver()) {
      rndAddRm(r, s, bs, r.nextInt(10000));
      if (!areSetEqual(s, bs))
        break;
    }
    if (!areSetEqual(s, bs))
      throw new RuntimeException("Error in add/remove/contains !");
  }
  
  public static void testItr(SetOfShorts s) {
    // ---- test itr
    int x = 0;
    SetOfShorts s2 = new SetOfShorts();
    SetOfShortsItr ai = new SetOfShortsItr(s);
    short e = 0;
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
  //           It verifies that an arbitrary sequence of add/remove
  //           results in a correct set.
  //           It verifies the union and the intersection
  //     prm : n is the time in tenths of seconds.
  
  public static void testSet(int n) {
    SetOfShorts s = new SetOfShorts();
    BitSet bs = new BitSet();
    Random r = new Random();
    long seed = r.nextInt(1000);
    r.setSeed(seed);
    System.out.println("Using seed "+seed);
    testAddRm(s, bs, r, n/2);
    testItr(s);
    testUnion(n/4, r);
    testIntersection(n/4, r);
  }
  //------------------------------------------------------------
  static long endTime;
  static void timerSet(long duration) { 
    endTime = 100*duration+System.currentTimeMillis();
  }
  static boolean timerOver() {return System.currentTimeMillis()>=endTime;}
  // ------------------------------------------------------------
  public static void main(String[] args) {
    int nt = 30;
    if (args.length == 1)
      nt = Integer.parseInt(args[0]);
    testSet(nt);
    System.out.println("\nTest passed successfully !");
  }
}
