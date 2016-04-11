package s11;

import java.util.Random;

// ======================================================================
public class IntQueueArray {
  private int[] buffer = new int[10];
  private int   front  = 1;
  private int   back   = 0;
  private int   size   = 0;

  // ------------------------------
  public IntQueueArray() {
  }

  // ------------------------------
  public void enqueue(int elt) {
    checkSize();
    back++;
    if (back == buffer.length)
      back = 0;
    buffer[back] = elt;
    size++;
  }

  // ------------------------------
  public boolean isEmpty() {
    return size == 0;
  }

  // ------------------------------
  public int consult() {
    return buffer[front];
  }

  // ------------------------------
  // PRE : Queue =! isEmpty()
  public int dequeue() {
    assert !isEmpty();
    // returns the value in the front (the oldest)
    int frontValue = this.buffer[front % this.buffer.length];
    this.front++; // increases the index of the oldest value
    this.size--; // decrease the size of the queue
    // returns frontValue;
    return frontValue;
  }

  // ------------------------------
  private void checkSize() {
    if (size < buffer.length)
      return;
    // if the buffer is too small --> double his size
    int[] newBuffer = new int[this.buffer.length * 2];
    for (int i = 0; i < this.buffer.length; i++) {
      newBuffer[i] = this.buffer[this.front++ % this.buffer.length];
    }
    this.buffer = newBuffer; // sets the new reference
    // sets the beginning & the end of the queue
    this.front = 0;
    this.back = this.front + this.size - 1;
  }

  // ======================================================================
  public static void main(String[] args) {
    int n = 1000000;
    if (args.length == 1)
      n = Integer.parseInt(args[0]);
    Random r = new Random();
    long seed = r.nextInt(1000);
    r.setSeed(seed);
    System.out.println("Using seed " + seed);
    IntQueueArray q = new IntQueueArray();
    int m = 0;
    int k = 0;
    int p = 0;
    for (int i = 0; i < n; i++) {
      boolean doAdd = r.nextBoolean();
      if (doAdd) {
        k++;
        q.enqueue(k);
        ok(!q.isEmpty(), "should be non-empty " + m + " " + k + " " + p + "\n");
        m++;
        // System.out.print("a("+k+")");
      } else {
        if (m == 0) {
          ok(q.isEmpty(), "should be empty " + m + " " + k + " " + p + "\n");
        } else {
          ok(!q.isEmpty(), "should be non-empty " + m + " " + k + " " + p
              + "\n");
          int e = q.dequeue();
          // System.out.print("r("+e+")");
          m--;
          ok(e == p + 1, "not FIFO " + m + " " + k + " " + p + "\n");
          p++;
        }
      }
    }
    System.out.println("Test passed successfully");
  }

  // ------------------------------------------------------------
  static long endTime;

  static void timerSet(long duration) {
    endTime = 100 * duration + System.currentTimeMillis();
  }

  static boolean timerOver() {
    return System.currentTimeMillis() >= endTime;
  }

  // ------------------------------------------------------------
  static void ok(boolean b, String s) {
    if (b)
      return;
    throw new RuntimeException("property not verified: " + s);
  }
}
