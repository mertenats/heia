package s14;

import java.util.BitSet;

public class SetOfStrings {
  BitSet           busy;
  String[]         elt;
  int[]            total;
  int              crtSize;
  static final int DEFAULT_CAPACITY = 5;

  // ------------------------------------------------------------
  public SetOfStrings() {
    this(DEFAULT_CAPACITY);
  }

  public SetOfStrings(int initialCapacity) {
    initialCapacity = Math.max(2, initialCapacity);
    elt = new String[initialCapacity];
    total = new int[initialCapacity];
    busy = new BitSet(initialCapacity);
    crtSize = 0;
  }

  int capacity() {
    return elt.length;
  }

  // Here is the hash function :
  int hashString(String s) {
    int h = s.hashCode() % capacity();
    if (h < 0)
      h = -h;
    return h;
  }

  // PRE: table is not full
  // returns the index where element e is stored,
  // or, if absent, the index where e should be stored
  int targetIndex(String e) {
    // TODO - A COMPLETER
    int hashCode = this.hashString(e); // gets the hashcode of "e" (String)
    // checks if the element is present
    // scans all the elements between [0, elt.length -1]
    for (int i = 0; i < this.capacity(); i++) {
      if (this.elt[i] != null && this.elt[i].equals(e)) {
        return i;
      }
    }

    // if the element isn't present --> finds a free position
    // scans all the values : [hashCode, capacity -1] & [0, hashCode -1]
    for (int i = hashCode; i < capacity(); i++) {
      if (!this.busy.get(i)) {
        return i;
      }
    }
    for (int i = 0; i < hashCode; i++) {
      if (!this.busy.get(i)) {
        return i;
      }
    }
    return -1; // returns -1 if it's full
  }

  public void add(String e) {
    if (crtSize * 2 >= capacity())
      doubleTable();
    // TODO - A COMPLETER
    // returns nothing if e is null or if e is already present
    if (e == null || this.contains(e)) {
      return;
    }
    int index = targetIndex(e); // gets the insertion position
    this.elt[index] = e; // adds e to the position
    this.total[hashString(e)]++; // increments the total value
    this.busy.set(index); // sets busy to true
    this.crtSize++; // increments the size
  }

  private void doubleTable() {
    // TODO - A COMPLETER
    // backups the data contained in elt (array)
    String[] eltCopy = this.elt.clone();
    // creates new arrays (double size of elt.length)
    String[] eltTmp = new String[this.capacity() * 2];
    int[] totalTmp = new int[this.capacity() * 2];
    this.busy.clear(); // initializes the BitSet
    this.crtSize = 0; // initializes the size
    this.elt = eltTmp;
    this.total = totalTmp;

    for (int i = 0; i < eltCopy.length; i++) {
      if (eltCopy[i] != null) {
        this.add(eltCopy[i]);
      }
    }
  }

  public void remove(String e) {
    int i = targetIndex(e);
    if (!busy.get(i))
      return; // elt is absent
    int h = hashString(e);
    total[h]--;
    elt[i] = null;
    busy.clear(i);
    crtSize--;
  }

  public boolean contains(String e) {
    return busy.get(targetIndex(e));
  }

  public void union(SetOfStrings s) {
    // TODO - A COMPLETER
    // "Try" to add each element of s
    for (int i = 0; i < s.capacity(); i++) {
      this.add(s.elt[i]);
    }
  }

  public void intersection(SetOfStrings s) {
    // TODO - A COMPLETER
    // if the element isn't in the two arrays --> removes it
    for (int i = 0; i < capacity(); i++) {
      if (this.elt[i] != null && !s.contains(elt[i])) {
        this.remove(this.elt[i]);
      }
    }
  }

  public int size() {
    return crtSize;
  }

  public boolean isEmpty() {
    return size() == 0;
  }

  public String[] arrayFromSet() {
    String t[] = new String[size()];
    int i = 0;
    SetOfStringsItr itr = new SetOfStringsItr(this);
    while (itr.hasMoreElements()) {
      t[i++] = itr.nextElement();
    }
    return t;
  }

  public String toString() {
    SetOfStringsItr itr = new SetOfStringsItr(this);
    if (isEmpty())
      return "{}";
    String r = "{" + itr.nextElement();
    while (itr.hasMoreElements()) {
      r += ", " + itr.nextElement();
    }
    return r + "}";
  }

  // ------------------------------------------------------------
  // tiny example
  // ------------------------------------------------------------
  public static void main(String[] args) {
    String a = "abc";
    String b = "defhijk";
    String c = "hahaha";
    SetOfStrings s = new SetOfStrings();
    s.add(a);
    s.add(b);
    s.remove(a);
    if (s.contains(a) || s.contains(c) || !s.contains(b))
      System.out.println("bad news...");
    else
      System.out.println("ok");
  }
}
