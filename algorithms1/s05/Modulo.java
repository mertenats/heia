package s05;

public class Modulo {
  public static int m(int n, int m) {
    if (n < m)
      return n;
    if (m == n)
      return 0;
    //n = m(n - m, m);
    //return n;
    return m(n - m, m);
  }

  public static void main(String[] args) {
    System.out.println(m(19, 5));
  }
}
