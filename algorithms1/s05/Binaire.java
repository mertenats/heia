package s05;

public class Binaire {
  static int x = 0;

  // public static int b(int n) {
  // // int x = 0;
  // if (n == 0)
  // return 0;
  // if (n % 2 == 1) {
  // x++;
  // b(n - 1);
  // } else {
  // b(n / 2);
  // }
  // return x;
  // }
  public static int b(int n) {
    if (n == 0)
      return 0;
    if (n % 2 == 1) 
      return 1 + b(n / 2);
    return b(n / 2);
  }

  public static void main(String[] args) {
    System.out.println(b(1));
  }
}
