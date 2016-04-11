package s13;

import java.util.Random;

public class RndMajority {
  public static void main(String[] args) {
    int nbOfExperiments = 10000;
    double risk = 1.0 / 1000;
    Random r = new Random();
    testMajority(nbOfExperiments, r, risk);
  }

  // ============================================================
  public static boolean hasMajority(Random r, int[] t, double risk) {
    int rndNb = 0;
    int appearanceNb = 0; // number of appearance(s)
    do {
      rndNb = r.nextInt(t.length); // gets a random number [0, 999]
      appearanceNb = 0;
      for (int i = 0; i < t.length; i++)
        // if the contained value at i is the same --> increases appearanceNb
        if (t[rndNb] == t[i])
          appearanceNb++;
      // if the number of appearances is bigger than t.length / 2 --> returns T
      if (appearanceNb > (t.length / 2))
        return true;
      // if false --> increases the risk and starts a new lap
      risk *= 2;
    } while (risk < 1);
    return false;
  }

  // ============================================================
  static final int MAX_VALUE = 1000;

  public static int[] generateMajorityArray(int n, Random r) {
    // Generate an array that is majoritary with a random value
    int[] t = new int[n];
    int m = r.nextInt(MAX_VALUE);
    // generate array of m
    for (int i = 0; i < n; i++) {
      if (i < n / 2 - 1)
        t[i] = r.nextInt(MAX_VALUE);
      else
        t[i] = m;
    }
    shuffle(r, t);
    return t;
  }

  // ============================================================
  public static int[] generateNotMajorityArray(int n, Random r) {
    assert (n > 1);
    int[] t = new int[n];
    int m = r.nextInt(MAX_VALUE);
    int i;
    t[0] = m - 1;
    for (i = 1; i < n; i++)
      if (i - 1 < n / 2)
        t[i] = m;
      else
        t[i] = m + 1;
    shuffle(r, t);
    return t;
  }

  // ============================================================
  public static void testMajority(int n, Random r, double risk) {
    int[] t;
    int mistakes = 0;
    // test with some majority arrays
    for (int i = 0; i < n; i++) {
      t = generateMajorityArray(n, r);
      if (!hasMajority(r, t, risk))
        mistakes++;
    }
    // test with some not majority arrays
    for (int i = 0; i < n; i++) {
      t = generateNotMajorityArray(n, r);
      if (hasMajority(r, t, risk))
        throw new RuntimeException(
            "This array has no majority, but your program has detected it as majority:\n["
                + toString(t) + "]");
    }
    System.out.println("\nTest passed successfully !");
    System.out.println("\n Mistake ratio : " + (mistakes / (double) n));
  }

  // ============================================================
  static String toString(int[] s) {
    String str = "";
    for (int i = 0; i < s.length; i++) {
      str = str + s[i] + " ";
    }
    return str;
  }

  // ============================================================
  static void shuffle(Random r, int[] t) {
    for (int i = 1; i < t.length; i++) {
      int j = r.nextInt(i + 1);
      int a = t[i];
      t[i] = t[j];
      t[j] = a;
    }
  }
}
