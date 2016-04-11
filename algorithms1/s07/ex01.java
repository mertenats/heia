package s07;

public class ex01 {

  public static void main(String args[]) throws Exception {
    int[] t = new int[10];

    for (int i = 0; i < t.length; i++) {  // remplissage alÈatoire du tableau et
                                         // affichage de celui-ci
      t[i] = (int) (Math.random() * 100);
      System.out.print(t[i] + " ");
    }
    System.out.println();
    System.out.println("Premier test :" + minimum(t, 0, t.length - 1));
    //System.out.println("DeuxiËme test :" + min2(t, 0, t.length - 1));
  }

  public static int minimum(int[] tab, int left, int right) throws Exception {
    if (tab.length < 1 || tab == null)
      throw new Exception("OpÈration impossible");

    // ---------------- condition d'arrÍt
    if (left == right)
      return (tab[left]);
    // ---------------- tests
    if (tab[left] < tab[right]) {
      right--;
      //return (minimum(tab, left, right));
    } else {
      left++;
    }
    return (minimum(tab, left, right));
  }

  public static int min2(int[] tab, int left, int right) throws Exception {
    if (tab.length < 1 || tab == null)
      throw new Exception("OpÈration impossible");

    // ----------------- condition d'arrÍt
    if (left == right)
      return (tab[left]);

    // ----------------- tests
    int mid = (left + right) / 2;
    System.out.printf("left = %d, mid = %d, right = %d", tab[left], tab[mid],
        tab[right]);
    System.out.println();
    int vg = min2(tab, left, mid); // partie gauche
    System.out.println(" vg = " + vg);
    int vd = min2(tab, mid + 1, right); // partie droite
    System.out.println(" vd = " + vd);
    if (vg > vd)
      return vd;
    else
      return vg;

  }
}
