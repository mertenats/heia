package s09;

public class CheckSort {

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
    for (int i1 = 0; i1 < givenInputFlags.length; i1++) {
      if (givenInputFlags[i1] != true)
        return false;
    }
    return true;
  }

  private static void displaysArrays(boolean isCorrectlySorted,
      int sortingMethod) {
    // displays the result of the comparison and the used method
    System.out.println("Sorted correctly :\t" + isCorrectlySorted
        + "\t sorting method :\t" + sortingMethod);
  }

  public static void main(String[] args) {
    // tableau 1
    // int[] sortedInput = {};
    // tableau 2
    // int[] sortedInput = {0};
    // tableau 3
    // int[] sortedInput = { 2, -1, 3, 4, 5, 4, 6 };
    // tableau 4
    // int[] sortedInput = new int[100000];
    // for (int i = 0; i < sortedInput.length; i++) {
    // sortedInput[i] = i % 10;
    // }
    // tableau 5
    // int[] sortedInput = new int[100000];
    // for (int i = 0; i < sortedInput.length; i++) {
    // if (i % 2 == 0)
    // sortedInput[i] = Integer.MAX_VALUE;
    // else
    // sortedInput[i] = Integer.MIN_VALUE;
    // }
    // tableau 6
    int[] sortedInput = new int[1000000];
    for (int i = sortedInput.length - 1; i > 0; i--) {
      sortedInput[i] = Integer.MIN_VALUE
          + (int) (Math.random() * Integer.MAX_VALUE);
    }

    int[] givenInput0 = sortedInput.clone();
    int[] givenInput1 = givenInput0.clone();
    int[] givenInput2 = givenInput0.clone();
    int[] givenInput3 = givenInput0.clone();
    int[] givenInput4 = givenInput0.clone();
    int[] givenInput5 = givenInput0.clone();
    int[] givenInput6 = givenInput0.clone();
    int[] givenInput7 = givenInput0.clone();
    int[] givenInput8 = givenInput0.clone();
    int[] givenInput9 = givenInput0.clone();

    BuggySorting.sort00(givenInput0);
    displaysArrays(isSortingResultCorrect(sortedInput, givenInput0), 0);
    BuggySorting.sort01(givenInput1);
    displaysArrays(isSortingResultCorrect(sortedInput, givenInput1), 1);
    BuggySorting.sort02(givenInput2);
    displaysArrays(isSortingResultCorrect(sortedInput, givenInput2), 2);
    BuggySorting.sort03(givenInput3);
    displaysArrays(isSortingResultCorrect(sortedInput, givenInput3), 3);
    BuggySorting.sort04(givenInput4);
    displaysArrays(isSortingResultCorrect(sortedInput, givenInput4), 4);
    BuggySorting.sort05(givenInput5);
    displaysArrays(isSortingResultCorrect(sortedInput, givenInput5), 5);
    BuggySorting.sort06(givenInput6);
    displaysArrays(isSortingResultCorrect(sortedInput, givenInput6), 6);
    BuggySorting.sort07(givenInput7);
    displaysArrays(isSortingResultCorrect(sortedInput, givenInput7), 7);
    BuggySorting.sort08(givenInput8);
    displaysArrays(isSortingResultCorrect(sortedInput, givenInput8), 8);
    BuggySorting.sort09(givenInput9);
    displaysArrays(isSortingResultCorrect(sortedInput, givenInput9), 9);
  }
}