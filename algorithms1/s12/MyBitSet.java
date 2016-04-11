package s12;

public class MyBitSet {
  private int[]              buffer;
  private static final short NB_OF_BITS = 32;

  public MyBitSet() {
    this(100);
  }

  public MyBitSet(int capacity) {
    buffer = new int[1 + (capacity / NB_OF_BITS)];
  }

  public void set(int bitIndex, boolean value) {
    assert bitIndex >= 0;
    // returns false if the value is false or if the index is out of the set
    if (value == false && (bitIndex > size())) {
      return;
    }
    int index = bitIndex / NB_OF_BITS; // index in the buffer
    int mask = 1 << (bitIndex % NB_OF_BITS);
    // checks the size; enlarges the buffer if necessary
    checkSize(index);

    if (value) {
      // to set on a bit: a = a | mask
      buffer[index] = buffer[index] | mask;
    } else {
      // to set off a bit: b = b & (~mask)
      buffer[index] = buffer[index] & (~mask);
    }
  }

  public void set(int bitIndex) {
    set(bitIndex, true);
  }

  public void clear(int bitIndex) {
    set(bitIndex, false);
  }

  // ------------------------------------------------------------
  // ------------------------------------------------------------
  // ensures that that array cell exists
  // (re-dimensions the array if necessary)
  private void checkSize(int arrayIndex) {
    if (arrayIndex < buffer.length)
      return;
    int f = 1 + arrayIndex / buffer.length;
    int[] aux = new int[f * buffer.length]; // or new int[arrayIndex+1] if
    for (int j = 0; j < buffer.length; j++)
      // we choose the minimal size
      aux[j] = buffer[j];
    buffer = aux;
    assert arrayIndex < buffer.length;
  }

  public boolean get(int bitIndex) {
    // if the bitIndex isn't in the set, it returns false
    if (bitIndex > size()) {
      return false;
    }

    // gets the index and the mask (same method as set())
    int index = bitIndex / NB_OF_BITS;
    int mask = 1 << (bitIndex % NB_OF_BITS);
    return (buffer[index] & mask) != 0;
  }

  public void and(MyBitSet o) {
    this.checkSize(o.buffer.length - 1);
    for (int i = 0; i < this.buffer.length; i++) {
      this.buffer[i] = this.buffer[i] & o.buffer[i];
    }
  }

  public void or(MyBitSet o) {
    this.checkSize(o.buffer.length - 1);
    for (int i = 0; i < this.buffer.length; i++) {
      this.buffer[i] = this.buffer[i] | o.buffer[i];
    }
  }

  public void xor(MyBitSet o) {
    this.checkSize(o.buffer.length - 1);
    for (int i = 0; i < this.buffer.length; i++) {
      this.buffer[i] = this.buffer[i] ^ o.buffer[i];
    }
  }

  public int size() {
    // crt capacity, total nb of bits
    return buffer.length * NB_OF_BITS;
  }

  public int length() { // highest bit "on" + 1
    int n = 0;
    for (int i = 0; i < buffer.length * NB_OF_BITS; i++)
      if (get(i))
        n = i + 1;
    return n;
  }

  public int nextSetBit(int fromIndex) {
    // returns the position of the next bit on
    for (int i = fromIndex; i < this.size(); i++) {
      if (this.get(i))
        return i;
    }
    return -1;  // -1 if none
  }

  public int cardinality() {
    int nbOfBitsOn = 0;
    for (int i = 0; i < this.size(); i++) {
      if (this.get(i))
        nbOfBitsOn++;
    }
    return nbOfBitsOn;
  }

  public String toString() {
    String r = "{";
    for (int i = 0; i < buffer.length * NB_OF_BITS; i++)
      if (get(i))
        if (r.length() == 1)
          r += i;
        else
          r += "," + i;
    return r + "}";
  }

  public static void main(String[] args) {
    MyBitSet a = new MyBitSet(100);
    ok(a.length() == 0);
    System.out.println(a);
    a.set(4);
    ok(a.get(4));
    ok(!a.get(3));
    a.clear(4);
    a.clear(5);
    a.set(6);
    ok(!a.get(4));
    ok(a.get(6));
    ok(!a.get(5));
    System.out.println(a);
  }

  static void ok(boolean b) {
    if (b)
      return;
    throw new RuntimeException("property not verified: ");
  }
}