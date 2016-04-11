package s16;

public class BTNode {
  Object elt;
  BTNode left, right, parent;
  //-------------------------
  public BTNode(Object e, BTNode l, BTNode r, BTNode p) {
    elt = e;left = l; right = r; parent = p;
  }
  //------------------------------------------------------------
  public String toString() {
    String ls="left:"+((  left==null)?"-":left.elt);
    String rs=" right:"+(( right==null)?"-":right.elt);
    String ps=" parent:"+((parent==null)?"-":parent.elt);
    return ""+elt+"("+ls+","+rs+","+ps+")";
  }
  //------------------------------------------------------------
  public String toReadableString() {
    return toReadableStr(this, "");
  }
  //------------------------------------------------------------
  private static String toReadableStr(BTNode n, String path) {
    if (n==null) return path + "-\n";
    String s ="";
    String b = "";
    String e = n.elt.toString();
    String pad = "";
    int i;
    for (i=0; i<e.length(); i++) pad+=" ";
    for (i=0; i<path.length(); i++) {
      //b+=" ";
      char a = path.charAt(i);
      b+=(a=='/' || a=='\\')?""+a:" ";
    }
    s += toReadableStr(n.right,  b +pad+"/");
    s += path + n.elt+"\n";
    s += toReadableStr(n.left, b +pad+"\\");
    return s;
  }
}
