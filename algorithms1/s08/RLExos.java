package s08;

public class RLExos {
  // ----------------------------------------------------------------
  // --- Exercises, Series 8 :
  // ----------------------------------------------------------------
  public static CharRecList append(CharRecList l, char e) {

    if (l.isEmpty())
      return l.withHead(e);
    CharRecList t = append(l.tail(), e);
    return t.withHead(l.head());
  }

  // ----------------------------------------------------------------
  public static CharRecList concat(CharRecList l, CharRecList r) {

    if (l.isEmpty())
      return r;
    return concat(l.tail(), r).withHead(l.head());
  }

  // ----------------------------------------------------------------
  public static CharRecList replaceEach(CharRecList l, char old, char by) {
    if (l.isEmpty())
      return l;

    if (l.head() == old)
      return replaceEach(l.tail(), old, by).withHead(by);
    return replaceEach(l.tail(), old, by).withHead(l.head());
  }

  // ----------------------------------------------------------------
  public static char consultAt(CharRecList l, int index) {
    if (l.isEmpty() || index < 0)
      return '-';

    if (index == 0)
      return l.head();
    return consultAt(l.tail(), --index);
  }

  // ----------------------------------------------------------------
  public static boolean isEqual(CharRecList l, CharRecList o) {
    if (l.isEmpty() || o.isEmpty())
      return l.isEmpty() && o.isEmpty();
    if (l.head() == o.head())
      return isEqual(l.tail(), o.tail());
    return false;
  }

  // ----------------------------------------------------------------
  public static boolean isSuffix(CharRecList l, CharRecList suff) {

    if (suff.isEmpty())
      return true;
    else if (l.isEmpty())
      return false;
    else if (isEqual(l, suff))
      return true;
    return isSuffix(l.tail(), suff);
  }

  // ----------------------------------------------------------------
  // --- example from the lecture :
  // ----------------------------------------------------------------
  public static int sizeOf(CharRecList l) {
    if (l.isEmpty())
      return 0;
    return 1 + sizeOf(l.tail());
  }

  // ----------------------------------------------------------------
  public static CharRecList inverse(CharRecList l) {
    if (l.isEmpty())
      return l;
    return append(inverse(l.tail()), l.head());
  }

  // ----------------------------------------------------------------
  public static boolean isMember(CharRecList l, char e) {
    if (l.isEmpty())
      return false;
    if (e == l.head())
      return true;
    return isMember(l.tail(), e);
  }

  // ----------------------------------------------------------------
  public static CharRecList smaller(CharRecList l, char e) {
    if (l.isEmpty())
      return l;
    if (l.head() < e)
      return smaller(l.tail(), e).withHead(l.head());
    return smaller(l.tail(), e);
  }

  // ----------------------------------------------------------------
  public static CharRecList greaterOrEqual(CharRecList l, char e) {
    if (l.isEmpty())
      return l;
    if (l.head() < e)
      return greaterOrEqual(l.tail(), e);
    return greaterOrEqual(l.tail(), e).withHead(l.head());
  }

  // ----------------------------------------------------------------
  public static CharRecList quickSort(CharRecList l) {
    CharRecList left, right;
    if (l.isEmpty())
      return l;
    left = smaller(l.tail(), l.head());
    right = greaterOrEqual(l.tail(), l.head());
    left = quickSort(left);
    right = quickSort(right);
    return concat(left, right.withHead(l.head()));
  }

  // ----------------------------------------------------------------
  // ----------------------------------------------------------------
  // ----------------------------------------------------------------
  public static void main(String[] args) {
    CharRecList l = CharRecList.EMPTY_LIST;
    l = l.withHead('a');
    l = l.withHead('b');
    System.out.println(append(l, 'c'));

    System.out.println(l);
//    CharRecList m = CharRecList.EMPTY_LIST;
//    CharRecList t = CharRecList.EMPTY_LIST;
//    l = l.withHead('c').withHead('d').withHead('a').withHead('b');
//    m = m.withHead('t').withHead('u').withHead('v');
//    t = t.withHead('c').withHead('d');
//
//    System.out.println("list l : " + l);
//    System.out.println("list m : " + m);
//    System.out.println("list t : " + t);
//
//    System.out.println("quickSort(l) : " + quickSort(l));
//    System.out.println("append(l,'z') : " + append(l, 'z'));
//    System.out.println("concat(l,m) : " + concat(l, m));
//    System.out.println("replaceEach(l,'a','z') : " + replaceEach(l, 'a', 'z'));
//    System.out.println("consultAt(l,2) : " + consultAt(l, 2));
//    System.out.println("isEqual(l,m) : " + isEqual(l, m));
//    System.out.println("isSuffix(l,t) : " + isSuffix(l, t));
    // ...
  }

}
