package s15;

public class MinPathDyn {

  public static int minPath(int[][] t) {
    int n = t.length, m = t[0].length;
    int[][] minPathSol = new int[n][m];
    minPathSol[0][0] = t[0][0];

    for (int i = 1; i < n; i++) { // remplir la premi�re colonne
      minPathSol[i][0] = minPathSol[i - 1][0] + t[i][0];
    }
    for (int j = 1; j < m; j++) { // remplir la premi�re ligne
      minPathSol[0][j] = minPathSol[0][j - 1] + t[0][j];
    }
    for (int i = 1; i < n; i++) {
      for (int j = 1; j < m; j++) {
        // comparer cellule du dessus et cellule � gauche
        int up = minPathSol[i - 1][j];
        int left = minPathSol[i][j - 1];
        if (up < left) // alors on prends up
          minPathSol[i][j] = up + t[i][j];
        else
          // alors on prends left
          minPathSol[i][j] = left + t[i][j];
      }

    }
    return minPathSol[n - 1][m - 1];
  }

  public static boolean subSetSumProgDyn(int k, int[] values) {
    boolean[] sols = new boolean[k + 1];  // tableau de solutions interm�diaires
    // rempli de false
    sols[0] = true;
    for (int i = 0; i < values.length; i++) {
      int currentValue = values[i];
      for (int j = k; j > 0; j--) {
        if ((j - currentValue >= 0) && (sols[j - currentValue]))
          sols[j] = true;
      }
    }
    return sols[k];
  }
}
