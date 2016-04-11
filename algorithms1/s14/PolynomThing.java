package s14;

public class PolynomThing {
  private final double[] coef;
  private final boolean  isReducible;
  private final String   name;

  public PolynomThing(double[] c, boolean red, String s) {
    int n = c.length;
    coef = new double[n];
    System.arraycopy(c, 0, coef, 0, n);
    isReducible = red;
    name = s;
  }

  @Override
  public int hashCode() {
    int result = 19; // or any non-null constant
    int c = 0; // hashcode

    // processing for this.coef (double[])
    for (int i = 0; i < coef.length; i++) {
      // uses formulas given in slides
      long coefTmp = Double.doubleToLongBits(coef[i]);
      c = (int) (coefTmp ^ (coefTmp >>> 32));
      result = result * 31 + c; // 31 gives good results
    }

    // processing for this.isReducible (boolean)
    // uses the given formula & adds hashcode to result
    c = (isReducible ? 1 : 0);
    result = result * 31 + c;

    // processing for this.name (String)
    c = name.hashCode(); // same as for an object
    result = result * 31 + c;
    return result; // returns the final hashcode
  }

  public double getCoef(int i) {
    return coef[i];
  }

  public int degree() {
    return coef.length;
  }

  public String name() {
    return name;
  }

  public boolean isReducible() {
    return isReducible;
  }

  // -------------------------------------------
  public static void main(String[] args) {
    PolynomThing a = new PolynomThing(new double[] { 2, 3, 4 }, true, "foo");
    PolynomThing b = new PolynomThing(new double[] { 2, 3, 5 }, true, "bar");
    PolynomThing c = new PolynomThing(new double[] { 2, 3 }, true, "demo");
    PolynomThing d = new PolynomThing(new double[] { 2, 3, 4 }, false, "foo");
    System.out.println(a.hashCode() + " " + b.hashCode() + " " + c.hashCode()
        + " " + d.hashCode());
  }

}
