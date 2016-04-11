package s04;

import s03.List;
import s03.ListItr;

public class Bubble {
  static void bubbleSortList(List l) {
    if (l.isEmpty())
      return;
    ListItr li = new ListItr(l);
    boolean goOn = true;
    while (goOn) {
      goOn = false;
      li.goToNext();
      for (int i = 0; i < l.size() - 1; i++) {
        if (li.pred.elt > li.succ.elt) {
          // if pred.ell > succ.elt --> bubbleSort()
          // if the function swaps values --> the loop continues
          goOn = bubbleSwapped(li);
        }
        li.goToNext();
      }
      li.goToFirst();
    }
  }

  // ---------------------------------------------
  // Swaps between left and right element if needed
  // Returns true if swap occurred
  static boolean bubbleSwapped(ListItr li) {
    if (li.isFirst() || li.isLast())
      return false; // no swap --> returns false
    int node1 = li.consultAfter();
    li.removeAfter();
    li.goToPrev();
    int node2 = li.consultAfter();
    li.removeAfter();
    // swaps the nodes --> returns true
    li.insertAfter(node2);
    li.insertAfter(node1);
    return true;
  }

  // ---------------------------------------------
  public static void main(String[] args) {
    List l = new List();
    ListItr li = new ListItr(l);
    int[] t = { 4, 3, 9, 2, 1, 8, 0 };
    int[] r = { 0, 1, 2, 3, 4, 8, 9 };
    for (int i = 0; i < t.length; i++) {
      li.insertAfter(t[i]);
      li.goToNext();
    }
    bubbleSortList(l);
    li = new ListItr(l);
    for (int i = 0; i < r.length; i++) {
      if (li.isLast() || li.consultAfter() != r[i]) {
        System.out.println("Oups... something is wrong");
        System.exit(-1);
      }
      li.goToNext();
    }
    if (!li.isLast()) {
      System.out.println("Oups... too much elements");
      System.exit(-1);
    }
    System.out.println("Test passed successfully");
  }
}