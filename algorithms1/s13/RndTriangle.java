package s13;

import java.util.Random;

public class RndTriangle {
  public static void main(String[] args) {
    int nbOfExperiments = 100000;
    Random r = new Random();
    if (args.length > 0)
      nbOfExperiments = Integer.parseInt(args[0]);
    System.out.println(rndTriangleAvgArea(r, nbOfExperiments));
  }

  // ============================================================
  public static double rndTriangleAvgArea(Random r, int nbOfExperiments) {
    // the average of the areas and the points (3x)
    double avgArea = 0.0, x1 = 0.0, y1 = 0.0, x2 = 0.0, y2 = 0.0, x3 = 0.0, y3 = 0.0;
    for (int i = 0; i < nbOfExperiments; i++) {
      // gets a random position for each point
      x1 = r.nextDouble();
      y1 = r.nextDouble();
      x2 = r.nextDouble();
      y2 = r.nextDouble();
      x3 = r.nextDouble();
      y3 = r.nextDouble();
      // adds the area to the average
      avgArea += (1 / 2.0 * Math.abs(x1 * (y2 - y3) + x2 * (y3 - y1) + x3
          * (y1 - y2)));
    }
    // returns the average divided by the number of experiments
    return avgArea / nbOfExperiments;
  }
}