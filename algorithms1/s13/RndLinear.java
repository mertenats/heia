package s13;

import java.util.Random;

public class RndLinear {
  public static void main(String[] args) {
    int nbOfExperiments = 100000;
    int n = 10;
    Random r = new Random();
    testLinear(r, n, nbOfExperiments);
  }

  // ============================================================
  public static int rndLinear(Random r, int n) {
    // gets a random value between [0, n * (n + 1) / 2]
    int x = r.nextInt(n * (n + 1) / 2);
    int sum = 0;
    int i;
    for (i = 1; i < n; i++) {
      sum += i; // adds 1, 2, 3, 4, ...
      // stops the loop if sum > x and returns the index of its (i)
      if (x < sum)
        break;
    }
    return i;
  }

  // ============================================================
  static void testLinear(Random r, int n, int nbOfExperiments) {
    int[] t = new int[n + 1];
    for (int i = 0; i < nbOfExperiments; i++)
      t[rndLinear(r, n)]++;
    System.out.println(0 + " : " + t[0]);
    for (int i = 1; i < n + 1; i++)
      System.out.println(i + " : " + (double) t[i] / nbOfExperiments);
  }
}
