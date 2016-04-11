package s14;

public class SetOfStringsItr {
  // TODO - A COMPLETER
  private SetOfStrings set;
  private int          index;

  // ------------------------------------------------------------
  public SetOfStringsItr(SetOfStrings theSet) {
    // TODO - A COMPLETER
    this.set = theSet;
    this.index = 0;
    // sets the index to the first element
    while (this.index < set.capacity() && !set.busy.get(index)) {
      this.index++;
    }
  }

  // ------------------------------------------------------------
  public boolean hasMoreElements() {
    // TODO - A COMPLETER
    // if the index is bigger than the capacity --> returns false
    if (this.index >= this.set.capacity()) {
      return false;
    }

    // scans each next element
    // while the index is smaller than the capacity and the checked cell is busy
    // --> increments the index
    while (this.index < this.set.capacity() && !this.set.busy.get(index)) {
      index++;
    }
    return (index < set.capacity());
  }

  // ------------------------------------------------------------
  public String nextElement() {
    // TODO - A COMPLETER
    return this.set.elt[index++];
  }
  // ------------------------------------------------------------
}
