package s04;

public class Sorting {
  public static void main(String[] args) {
    int[] t = { 10, 2, 1, 8, 7, 5, 0, 2, 3, 4, 6, 9 };
    // int[] u = { 2, 3, 4, 6, 7, 8 };
    // insertionSort(t);
    // for (int i = 0; i < t.length; i++)
    // if (t[i] != u[i]) {
    // System.out.println("Something is wrong...");
    // return;
    // }
    // int[] v = { 5 };
    // insertionSort(v);
    // int[] w = {};
    // insertionSort(w);
    // System.out.println("\nMini-test passed successfully...");
    // selectionSort(t);
    System.out.println("\nTableau non trié : ");
    for (int i = 0; i < t.length; i++) {
      System.out.print(t[i] + " ");
    }
    selectionSort(t);
    // shellSort(t);
    System.out.println("\nTableau  trié : ");

    for (int i = 0; i < t.length; i++) {
      System.out.print(t[i] + " ");
    }
  }

  // ------------------------------------------------------------
  public static void selectionSort(int[] a) {
    // i : the current position in the tab
    // j : the position of the checked case
    // for (int i = 0; i < a.length - 1; i++) {
    // int kP = i;; // position of the smallest value
    // for (int j = i; j < a.length; j++) {
    // if (a[j] < a[i]) {
    // kP = j; // stores smallest position
    // }
    // }
    // int tmp = a[i]; // swaps value and position
    // a[i] = a[kP];
    // a[kP] = tmp;
    //
    // }
    for (int i = 0; i < a.length -1; i++) {
      int sPos = i;
      for (int j = i; j < a.length; j++) {
        // if (a[i] > a[j]) {
        if (a[sPos] > a[j]) {
          sPos = j;
        }
        int tmp = a[sPos];
        a[sPos] = a[i];
        a[i] = tmp;
      }
    }
  }

  // ------------------------------------------------------------
  public static void shellSort(int[] a) {
    int k = (int) a.length / 3;
    for (int i = k; i > 0; i--) {
      // nbLoop: number of execution(s) for each k value (if k=3;3x)
      int nbLoop = 0;
      while (nbLoop < k) {
        // tab for the tmp values
        int[] tmp = new int[(int) a.length / k];
        int tabIndex = 0;
        for (int j = nbLoop; j < a.length; j += k) {
          tmp[tabIndex] = a[j];
          tabIndex++;
        }
        insertionSort(tmp); // sorts the values
        tabIndex = 0;
        for (int j = nbLoop; j < a.length; j += k) {
          // introduces the values in their new positions
          a[j] = tmp[tabIndex];
          tabIndex++;
        }
        nbLoop++;
      }
      k--;
    }
  }

  // ------------------------------------------------------------
  public static void insertionSort(int[] a) {
    int i, j, v;

    for (i = 1; i < a.length; i++) {
      v = a[i]; // v is the element to insert
      j = i;
      while (j > 0 && a[j - 1] > v) {
        a[j] = a[j - 1]; // move to the right
        j--;
      }
      a[j] = v; // insert the element
    }
  }
  // ------------------------------------------------------------
}
