package s12;

public class BitSetOfShortsItr {
  private BitSetOfShorts bs;
  private int            index;

  public BitSetOfShortsItr(BitSetOfShorts theSet) {
    this.bs = theSet;
    this.index = 0;
  }

  public boolean hasMoreElements() {
    return this.bs.bs.nextSetBit(this.index) != -1;
  }

  public short nextElement() {
    this.index = this.bs.bs.nextSetBit(this.index);
    return BitSetOfShorts.eltFromIndex(index++);
  }
}