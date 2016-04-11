package s07;

import java.io.*;

import proglib.SimpleIO;

public class ExternalSorting {
  public static void main(String[] args) {
    String filename = SimpleIO.fileChooser(); // gets the file to sort
    if (args.length > 0)
      filename = args[0];
    mergeSort2(filename);
  }

  private static boolean isMonotone(String crt, String prev) {
    if (crt == null)
      return false;
    if (prev == null)
      return true;
    // return true if the two words are in the good order
    return (crt.compareTo(prev) >= 0);
  }

  private static void merge(String a, String b, String c) throws IOException {
    BufferedReader fa = new BufferedReader(new FileReader(a)); // tmp1
    BufferedReader fb = new BufferedReader(new FileReader(b)); // tmp2
    PrintWriter fc = new PrintWriter(new FileWriter(c)); // source file
    String sa = fa.readLine(); // reads the first line in file tmp1
    String saPrev = sa;
    String sb = fb.readLine(); // reads the first line in file tmp2
    String sbPrev = sb;
    // as long as the tmp files aren't at their end ...
    while (sa != null || sb != null) {
      // if needed, go to the next two monotone squences
      if (!isMonotone(sa, saPrev) && !isMonotone(sb, sbPrev)) {
        saPrev = sa;
        sbPrev = sb;
      }
      if (!isMonotone(sb, sbPrev) || isMonotone(sa, saPrev)
          && sa.compareTo(sb) <= 0) {
        fc.println(sa);
        saPrev = sa;
        sa = fa.readLine();
      } else {
        fc.println(sb);
        sbPrev = sb;
        sb = fb.readLine();
      }
    }
    fa.close();
    fb.close();
    fc.close();
  }

  private static int split(String a, String b, String c) throws IOException {
    BufferedReader fa = new BufferedReader(new FileReader(a)); // source file
    PrintWriter fb = new PrintWriter(new FileWriter(b)); // file tmp1
    PrintWriter fc = new PrintWriter(new FileWriter(c)); // file tmp2

    String actualString = fa.readLine(); // reads the first line of
    String previousString = actualString; // previous == actual string
    int numberOfMonotonies = 1;

    // as long as the source file isn't at the end
    while (actualString != null) {
      if (isMonotone(actualString, previousString)) {
        // modulo 2 --> writes the line in tmp file 1 or 2
        if (numberOfMonotonies % 2 != 0) {
          fb.println(actualString); // writes in the temporary file b
        } else {
          fc.println(actualString); // writes in the temporary file c
        }
        previousString = actualString;
        actualString = fa.readLine(); // gets the next line from the source file
      } else {
        numberOfMonotonies++;
        previousString = actualString;
      }
    }

    fa.close();
    fb.close();
    fc.close();
    return numberOfMonotonies;
  }

  public static void mergeSort2(String filename) {
    String tmp1 = filename + ".tmp1"; // somewhat...
    String tmp2 = filename + ".tmp2"; // ...dangerous...
    int monotoneSeq;
    try {
      monotoneSeq = split(filename, tmp1, tmp2);
      while (monotoneSeq > 1) {
        merge(tmp1, tmp2, filename);
        monotoneSeq = split(filename, tmp1, tmp2);
      }
    } catch (IOException e) {
      System.out.println(e);
    }
  }
}