package s09;

public class Test {

  public static void main(String[] args) {
    // TODO Auto-generated method stub
    System.out.println(findsBug06());
  }

  static boolean findsBug01() {
    IntStack s = new IntStack(10);
    s.push(0);
    if (s.pop() != 0)
      return true;
    if (!s.isEmpty())
      return true;
    return false;
  }

  static boolean findsBug02() {
    IntStack s = new IntStack(10);
    s.push(0);
    s.push(1);
    if (s.isEmpty())
      return true;
    return false;
  }

  static boolean findsBug03() {
    IntStack s = new IntStack(10);
    s.push(0);
    s.push(5);
    s.pop();
    s.push(1);
    if (s.pop() != 1)
      return true;
    if (s.isEmpty())
      return true;
    return false;
  }

  static boolean findsBug04() {
    IntStack s = new IntStack(10);
    try {
      s.push(10);
    } catch (Exception e) {
      return true;
    }
    return false;
  }

  static boolean findsBug05() {
    IntStack s = new IntStack(10);
    IntStack t = new IntStack(1);
    s.push(11);
    t.push(12);
    if (s.top() == 12)
      return true;
    return false;
  }
  
  static boolean findsBug06() {
    IntStack x = new IntStack(0);
    return false;
  }
}
