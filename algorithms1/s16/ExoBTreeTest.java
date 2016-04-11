package s16;


import java.util.HashSet;
import java.util.Vector;

public class ExoBTreeTest  {
  // ------------------------------------------------------------
  static boolean validTree(BTNode t) {
    if (t==null) return true;
    HashSet s = new HashSet();
    s.add(t);
    return (t.parent==null) && validSt(t.left, t,s) && validSt(t.right,t,s);
  }
  static boolean validSt(BTNode t, BTNode p, HashSet s) {
    if (t==null) return true;
    if (s.contains(t)) return false;
    s.add(t);
    boolean l = validSt(t.left, t,s)&& validSt(t.right, t,s);
    s.remove(t);
    return (t.parent==p) && l;
  }
  // ------------------------------------------------------------
  static int h(BTNode t) {
    return (t==null)? 0 : (1+Math.max( h(t.left), h(t.right) ));
  }
  // ------------------------------------------------------------
  public static void rl(BTNode x) {
    ok(x.right!=null);
    BTNode y = x.right, p = x.parent, b=y.left;
    y.parent = p; y.left = x; x.parent = y; x.right = b;
    if (b!=null) b.parent=x;
    if (p==null) return;
    if (p.left == x)  p.left  = y; else p.right = y;
  }
  // ------------------------------------------------------------
  static BTNode [] nodesWithLeft(BTNode t) {
    Vector v=new Vector();
    nodesWithLeft(t, v);
    return (BTNode []) v.toArray(new BTNode[v.size()]);
  }
  static void  nodesWithLeft(BTNode t, Vector v) {
    if (t==null) return;
    if (t.left!=null) v.add(t);
    nodesWithLeft(t.left, v); nodesWithLeft(t.right, v);
  }
  // ------------------------------------------------------------
  static String withoutSpaces(String a) {
    String res="";
    for(int i=0;i<a.length();i++)
      if (a.charAt(i)!=' ') res+=a.charAt(i);
    return res;
  }
  // ------------------------------------------------------------
  public static void testBreadthSizeHeight(int maxSize) {
    BTNode t;
    String a, b;
    for (int i = 1; i < maxSize; i++) {
      t = ExoBTree.rndTree(i);
      ok(i==ExoBTree.size(t));
      ok(h(t)==ExoBTree.height(t));
      a = ExoBTree.breadthFirst(t);
      b = ExoBTree.breadthFirst1(t);
      ok(validTree(t));
      ok(withoutSpaces(a).equals(withoutSpaces(b)));
    }
  }
  // ------------------------------------------------------------
  public static void testRotate(int maxSize) {
    BTNode t;
    String a, b;
    for (int i = 1; i < maxSize; i++) {
      t = ExoBTree.rndTree(i);
      a = ExoBTree.breadthFirst(t);
      BTNode [] rr=nodesWithLeft(t);
      for (BTNode ti:rr) {
        ExoBTree.rotateRight(ti);
        ok(validTree(t.parent==null?t:t.parent));
        rl(ti.parent);
        b=ExoBTree.breadthFirst(t);
        ok(a.equals(b));
      }
    }
  }
  // ------------------------------------------------------------
  public static void ok(boolean b) {
    if (!b) throw new RuntimeException("bad news...");
  }
  // ------------------------------------------------------------
  public static void main(String[] args) {
    int n=100;
    if (args.length >0) n = Integer.parseInt(args[0]);
    testBreadthSizeHeight(n);
    testRotate(n);
    System.out.println("\nTest passed successfully !");
  }
  // ------------------------------------------------------------
}
