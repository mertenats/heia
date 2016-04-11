package s05;

public class Carre {
  public static int auCarre(int n) {
    if (n == 1)
      return 1;
    return auCarre((n - 1) * (n - 1) + 2*n -1);
  }

  public static void main(String[] args) {
    System.out.println(auCarre(3));
  }

}
