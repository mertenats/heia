package s13;

import java.util.Random;

public class RndFlipCoin {
  public static void main(String[] args) {
    int nbOfExperiments = 100000;
    int n = 4, m = 1;
    Random r = new Random();
    testFlipCoin(r, m, n, nbOfExperiments);
  }

  // ------------------------------------------------------------
  public static boolean flipCoin(Random r, int m, int n) {
    return (r.nextInt(n) < m);
  }

  // ============================================================
  static void testFlipCoin(Random r, int m, int n, int nbOfTests) {
    int nbOfTrue = 0;
    for (int i = 0; i < nbOfTests; i++) {
      if (flipCoin(r, m, n))
        nbOfTrue++;
    }
    System.out.println("There was " + nbOfTrue + " TRUE among " + nbOfTests
        + " flips of coin");
  }
}
