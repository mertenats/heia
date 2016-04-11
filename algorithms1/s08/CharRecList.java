package s08;

public class CharRecList {
  // ===========================
  private static class CRLNode {
    final char    elt;
    final CRLNode tail;

    public CRLNode(char e, CRLNode t) {
      elt = e;
      tail = t;
    }
  }

  // ===========================
  private final CRLNode           first;
  // ----------------------------
  public static final CharRecList EMPTY_LIST = new CharRecList(null);

  private CharRecList(CRLNode n) {
    first = n;
  }

  public CharRecList withHead(char e) {
    CRLNode n = new CRLNode(e, first);
    return new CharRecList(n);
  }

  public char head() {
    return first.elt;
  }

  public CharRecList tail() {
    return new CharRecList(first.tail);
  }

  public boolean isEmpty() {
    return first == null;
  }

  public String toString() {
    if (isEmpty())
      return "";
    return head() + tail().toString();
  }
}
