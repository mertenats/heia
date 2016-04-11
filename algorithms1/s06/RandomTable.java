package s06;

import java.util.Arrays;
import java.util.Random;

public class RandomTable {
  static Random r = new Random();

  public static short[] randomTable(short m, short n) {
    short[] result;
    // creates a empty set of shorts + iterator
    SetOfShorts setOfShorts = new SetOfShorts();
    SetOfShortsItr iterator = new SetOfShortsItr(setOfShorts);

    // as long as the set of shorts is smaller than m
    for (int i = 0; i < m; i++) {
      short randomNb; // will contain a random number
      randomNb = (short) r.nextInt(n); // generates a random number
      while (setOfShorts.contains(randomNb)) {
        // if the number already exists, it gerates one again
        randomNb = (short) r.nextInt(n);
      }
      setOfShorts.add(randomNb); // adds the number to the collection
    }

    int i = 0;
    result = new short[m]; // will contains the sorted shorts
    while (iterator.hasMoreElements()) {
      // gets the short from the collection + inserts it in the array
      short tmpShort = iterator.nextElement();
      result[i++] = tmpShort;
    }

    Arrays.sort(result); // sorts the array
    return result;
  }

  static void testRandomTable(short m, short n) {
    short[] s = randomTable(m, n);
    int i;
    if (m != s.length)
      throw new RuntimeException("Size of array is not correct");
    if (s.length > 0 && s[0] < 0)
      throw new RuntimeException("Elements must be in [0..n[");
    for (i = 0; i < s.length - 1; i++) {
      if (s[i] >= s[i + 1])
        throw new RuntimeException(
            "Array should be sorted and contain distinct numbers\n["
                + stringFromArray(s) + "]");
    }
    System.out.println("\nTest passed successfully !");
    for (i = 0; i < m; i++)
      System.out.print(" " + s[i]);
  }

  static String stringFromArray(short[] s) {
    String str = "";
    for (int i = 0; i < s.length; i++) {
      str = str + s[i] + " ";
    }
    return str;
  }

  public static void main(String[] args) {
    short m = 10;
    short n = 50;
    if (args.length == 2) {
      m = Short.parseShort(args[0]);
      n = Short.parseShort(args[1]);
    }
    testRandomTable(m, n);
  }
}