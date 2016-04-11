package s17;

import java.util.Random;

// ------------------------------------------------------------
public class IntIntPtyQueue {
  private IntQueueArray[] qt;
  private int             maxPty;
  private int             highestPty;

  // or: private IntQueueChained [] qt;
  // ...
  // ------------------------------------------------------------
  // priorities will be in 0..theMaxPty
  public IntIntPtyQueue(int theMaxPty) {
    maxPty = theMaxPty;
    highestPty = maxPty;
    qt = new IntQueueArray[maxPty];
    for (int i = 0; i < maxPty; i++) {
      qt[i] = new IntQueueArray();
    }
  }

  // ------------------------------------------------------------
  public boolean isEmpty() {
    for (int i = 0; i < maxPty; i++) {
      if (!qt[i].isEmpty())
        return false;
    }
    return true;
  }

  // ------------------------------------------------------------
  // PRE: 0<=pty<maxPty
  public void enqueue(int elt, int pty) {
    if (!(pty >= 0 && pty < maxPty))
      throw new AssertionError(); // ähnlich wie assert
    qt[pty].enqueue(elt);
    if (pty < highestPty)
      highestPty = pty;
  }

  // ------------------------------------------------------------
  // highest pty present in the queue.
  // PRE: ! isEmpty()
  public int consultPty() {
    if (isEmpty())
      throw new AssertionError();
    return highestPty;
  }

  // ------------------------------------------------------------
  // elt with highest (smallest) pty.
  // PRE: ! isEmpty()
  public int consult() {
    if (isEmpty())
      throw new AssertionError();
    return qt[consultPty()].consult();
  }

  // ------------------------------------------------------------
  // elt with highest (smallest) pty.
  // PRE: ! isEmpty()
  public int dequeue() {
    if (isEmpty())
      throw new AssertionError();
    int pty = consultPty();
    int res = qt[pty].dequeue();
    if (qt[pty].isEmpty()) {
      highestPty = pty + 1;
      while (highestPty < maxPty && qt[highestPty].isEmpty())
        highestPty++;
    }
    return res;
  }

  // ------------------------------------------------------------
  // ------------------------------------------------------------
  // ------------------------------------------------------------
  public static void main(String[] args) {
    Random r = new Random();
    long seed = r.nextInt(1000);
    r.setSeed(seed);
    System.out.println("Using seed " + seed);
    int n = 10000;
    if (args.length == 1)
      n = Integer.parseInt(args[0]);
    int p, e;
    IntIntPtyQueue pq = new IntIntPtyQueue(n);
    for (int i = 0; i < 10 * n; i++) {
      p = r.nextInt(n);
      pq.enqueue(p, p);
    }
    e = Integer.MIN_VALUE;
    for (int i = 0; i < 10 * n; i++) {
      p = pq.dequeue();
      ok(p >= e);
      e = p;
    }
    ok(pq.isEmpty());
    for (int i = 0; i < 10 * n; i++) {
      p = r.nextInt(n);
      pq.enqueue(p, p);
      p = r.nextInt(n);
      pq.enqueue(p, p);
      pq.dequeue();
    }
    e = Integer.MIN_VALUE;
    while (!pq.isEmpty()) {
      p = pq.dequeue();
      ok(p >= e);
      e = p;
    }
    System.out.println("Test passed successfully");
  }

  // ------------------------------------------------------------
  static void ok(boolean b) {
    if (b)
      return;
    throw new RuntimeException("property not verified: ");
  }
}
