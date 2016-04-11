package s07;

public class Exercise01 {

  public static void main(String[] args) {
    int[] t = { -5, 0, 1, -1, -5 };
    System.out.println("The smallest value is -5");
    try {
      System.out.println("The first function returns : " + minimum(t));
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("The second function returns : "
        + minimum(t, 0, t.length - 1));
  }

  public static int minimum(int[] t) throws Exception {
    if (t.length == 0) {
      // if length == 0 --> no values
      throw new Exception("The table is empty.");
    } else if (t.length == 1) {
      return t[0]; // length of 1 --> returns the unique value
    }

    // creates a table of length t-1 --> t[0] : cursor
    int[] t2 = new int[t.length - 1];
    for (int i = 1; i < t.length; i++) {
      t2[i - 1] = t[i]; // copies the values from t to t3
    }

    // recursive call --> the table becomes smaller
    int minValue = minimum(t2);

    if (minValue <= t[0]) {
      return minValue;
    }
    return t[0];
  }

  public static int minimum(int[] t, int left, int right) {
    // stop condition --> left == right
    if (left == right) {
      return t[left];
    }

    // gets the middle of the table
    int middlePosition = (left + right) / 2;
    // 2 recursive calls --> left - middlePosition <> middlePosition + 1 - right
    int minimumLeft = minimum(t, left, middlePosition);
    int minimumRight = minimum(t, middlePosition + 1, right);

    if (minimumLeft < minimumRight)
      return minimumLeft;
    return minimumRight;
  }
}