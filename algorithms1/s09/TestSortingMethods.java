package s09;

public class TestSortingMethods {
  private static boolean isSortingResultCorrect(int[] givenInput,
      int[] observedOutput) {
    // tests the size of the arrays
    if (givenInput.length != observedOutput.length)
      return false;

    // tests if the array is correctly sorted
    for (int i = 0; i < observedOutput.length - 1; i++) {
      if (observedOutput[i] > observedOutput[i + 1])
        return false;
    }

    // tests if all the elements are present
    Boolean[] givenInputFlags = new Boolean[givenInput.length];
    for (int i = 0; i < givenInputFlags.length; i++) {
      givenInputFlags[i] = false;
    }
    for (int i = 0; i < givenInput.length; i++) {
      for (int j = 0; j < observedOutput.length; j++) {
        if (observedOutput[j] == givenInput[i]) {
          if (givenInputFlags[i] != true) {
            givenInputFlags[i] = true;
            break;
          }
        }
      }
    }
    // if a number isn't flagged as "true" --> false
    for (int i1 = 0; i1 < givenInputFlags.length; i1++) {
      if (givenInputFlags[i1] != true)
        return false;
    }
    return true;
  }

  public static boolean sort(String sortingMethod, int[] givenTab) {
    int[] givenTabCopy = givenTab.clone();
    switch (sortingMethod) {
      case "sort00":
        try {
          BuggySorting.sort00(givenTabCopy);
        } catch (Exception e) {
          e.printStackTrace();
          return false;
        }
        if (isSortingResultCorrect(givenTab, givenTabCopy) == false) {
          return false;
        }
        break;
      case "sort01":
        try {
          BuggySorting.sort01(givenTabCopy);
        } catch (Exception e) {
          e.printStackTrace();
          return false;
        }
        if (isSortingResultCorrect(givenTab, givenTabCopy) == false) {
          return false;
        }
        break;
      case "sort02":
        try {
          BuggySorting.sort01(givenTabCopy);
        } catch (Exception e) {
          e.printStackTrace();
          return false;
        }
        if (isSortingResultCorrect(givenTab, givenTabCopy) == false) {
          return false;
        }
        break;
      case "sort03":
        try {
          BuggySorting.sort01(givenTabCopy);
        } catch (Exception e) {
          e.printStackTrace();
          return false;
        }
        if (isSortingResultCorrect(givenTab, givenTabCopy) == false) {
          return false;
        }
        break;
      case "sort04":
        try {
          BuggySorting.sort01(givenTabCopy);
        } catch (Exception e) {
          e.printStackTrace();
          return false;
        }
        if (isSortingResultCorrect(givenTab, givenTabCopy) == false) {
          return false;
        }
        break;
      case "sort05":
        try {
          BuggySorting.sort01(givenTabCopy);
        } catch (Exception e) {
          e.printStackTrace();
          return false;
        }
        if (isSortingResultCorrect(givenTab, givenTabCopy) == false) {
          return false;
        }
        break;
      case "sort06":
        try {
          BuggySorting.sort01(givenTabCopy);
        } catch (Exception e) {
          e.printStackTrace();
          return false;
        }
        if (isSortingResultCorrect(givenTab, givenTabCopy) == false) {
          return false;
        }
        break;
      case "sort07":
        try {
          BuggySorting.sort01(givenTabCopy);
        } catch (Exception e) {
          e.printStackTrace();
          return false;
        }
        if (isSortingResultCorrect(givenTab, givenTabCopy) == false) {
          return false;
        }
        break;
      case "sort08":
        try {
          BuggySorting.sort01(givenTabCopy);
        } catch (Exception e) {
          e.printStackTrace();
          return false;
        }
        if (isSortingResultCorrect(givenTab, givenTabCopy) == false) {
          return false;
        }
        break;
      case "sort09":
        try {
          BuggySorting.sort01(givenTabCopy);
        } catch (Exception e) {
          e.printStackTrace();
          return false;
        }
        if (isSortingResultCorrect(givenTab, givenTabCopy) == false) {
          return false;
        }
        break;
      default:
        System.out.println("Sorting method unknown!");
        break;
    }
    return true;
  }

  public static void main(String[] args) {
    int[] t00 = {};
    int[] t01 = { 0 };
    int[] t02 = new int[1000];
    int[] t03 = new int[10000];
    int[] t04 = { 2, -4, 3, 4, 4, 1 };
    int[] t05 = { 100, -100, 6, 7, 0 };
    for (int i = 0; i < t02.length; i++) {
      t02[i] = 0;
    }
    for (int i = 0; i < t03.length; i++) {
      t03[i] = i % 10;
    }
    final String[] sortingMethods = { "sort00", "sort01", "sort02", "sort03",
        "sort04", "sort05", "sort06", "sort07", "sort08", "sort09" };
    Boolean[][] methodsFlag = new Boolean[10][6];
    for (int i = 0; i < methodsFlag.length; i++) {
      for (int j = 0; j < methodsFlag[i].length; j++) {
        methodsFlag[i][j] = true;
      }
    }

    for (int i = 0; i < methodsFlag.length; i++) {
      if (sort(sortingMethods[i], t00.clone()) == false) {
        methodsFlag[i][0] = false;
      }
      if (sort(sortingMethods[i], t01.clone()) == false) {
        methodsFlag[i][1] = false;
      }
      if (sort(sortingMethods[i], t02.clone()) == false) {
        methodsFlag[i][2] = false;
      }
      if (sort(sortingMethods[i], t03.clone()) == false) {
        methodsFlag[i][3] = false;
      }
      if (sort(sortingMethods[i], t04.clone()) == false) {
        methodsFlag[i][4] = false;
      }
      if (sort(sortingMethods[i], t05.clone()) == false) {
        methodsFlag[i][5] = false;
      }
    }

    // prints the results
    for (int i = 0; i < methodsFlag.length; i++) {
      Boolean flag = true;
      for (int j = 0; j < methodsFlag[i].length; j++) {
        if (methodsFlag[i][j] == false) {
          flag = false;
          break;
        }
      }
      System.out.println("Sorting method 0" + i + " works : " + flag);
    }
  }
}