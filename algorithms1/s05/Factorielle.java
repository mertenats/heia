package s05;

public class Factorielle {
  public static int f(int n) {
    //int r = 1;
    if (n == 1)
      return 1;
      //return r;
    //r = n * f(n - 1);
    return n * f(n -1);
    //return r;
  }

  public static void main(String[] args) {
    System.out.println(f(19));
  }
}
