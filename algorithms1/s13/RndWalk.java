package s13;

import java.util.Random;

public class RndWalk {
  public static void main(String[] args) {
    int nbOfExperiments = 100000;
    int n = 30;
    int leftChoicePercentage = 55;
    Random r = new Random();
    System.out.println(rndWalkMirrorAvgLength(r, n, leftChoicePercentage,
        nbOfExperiments));
  }

  // ============================================================
  static double rndWalkMirrorAvgLength(Random r, int pointToReach,
      int leftChoicePercentage, int nbOfExperiments) {
    int x, nbOfSteps = 0;
    int total = 0;
    double durationTime = System.nanoTime();
    for (int i = 0; i < nbOfExperiments; i++) {
      x = 0;
      nbOfSteps = 0;
      while (x != pointToReach) {
        // gets a random number [0, 99]
        // if leftChoicePercentage == 50 --> is true if [0, 49]
        if (r.nextInt(100) < leftChoicePercentage) {
          if (x != 0)
            x--; // goes one step to the left
        } else
          // if nextInt() > leftChoicePercentage --> goes one step to the right
          x++;
        nbOfSteps++;
      }
      total += nbOfSteps;
    }
    durationTime = ((System.nanoTime() - durationTime) * Math.pow(10, -9));
    System.out.println(durationTime);
    return total / (double) nbOfExperiments;
  }
}
