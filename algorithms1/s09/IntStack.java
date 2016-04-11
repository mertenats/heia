package s09;

public class IntStack {

  private int buf[];
  private int top;

  public IntStack() {
    this(10);
  }

  // PRE: cap > 0
  public IntStack(int cap) {
    assert (cap > 0);
    buf = new int[cap];
    top = -1;
  }

  public boolean isEmpty() {
    return top == -1;
  }

  // PRE: !isEmpy()
  public int top() {
    assert (!isEmpty());
    return buf[top];
  }

  // PRE: !isEmpy()
  public int pop() {
    assert (!isEmpty());
    int a = buf[top];
    top--;
    return a;
  }

  public void push(int x) {
    checkSize();
    top++;
    buf[top] = x;
  }

  // PRE: !isEmpty
  private void checkSize() {
    assert (!isEmpty());
    if (top == buf.length - 1) {
      int[] t = new int[2 * buf.length];
      for (int i = 0; i < buf.length; i++)
        t[i] = buf[i];
      buf = t;
    }
    // POST: !isEmpty buf.length-1>top
    assert (buf.length - 1 > top);
  }
}
