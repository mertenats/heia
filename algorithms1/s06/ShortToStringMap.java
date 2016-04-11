package s06;

import java.util.ArrayList;

public class ShortToStringMap {
  private int                   size;      // used only for testing purpose
  private ArrayList<Dictionary> dictionary; // uses my class "Dictionary"

  public ShortToStringMap() {
    this.size = 0;
    this.dictionary = new ArrayList<Dictionary>();
  }

  public int indexOfKey(ArrayList<Dictionary> dictionary, short key) {
    for (int i = 0; i < this.dictionary.size(); i++) {
      if (dictionary.get(i).getKey() == key) {
        return i; // if the key exists --> return the position
      }
    }
    return -1; // if the key does't exists --> return -1
  }

  public void put(short key, String img) {
    if (indexOfKey(this.dictionary, key) != -1) {
      // if the key already exists --> updates the value
      this.dictionary.get(indexOfKey(this.dictionary, key)).setValue(img);
    } else {
      // if the key does't exists --> creates a new entry
      this.dictionary.add(new Dictionary(key, img));
      this.size++; // used only for testing purpose
    }
  }

  public String get(short key) {
    // if the entry exists --> returns it else returns null
    if (indexOfKey(dictionary, key) != -1) {
      return this.dictionary.get(indexOfKey(dictionary, key)).getValue();
    } else {
      return null;
    }
  }

  public void remove(short key) {
    // remove the entry only if it exists in the dictionary
    if (indexOfKey(dictionary, key) != -1) {
      this.dictionary.remove(indexOfKey(dictionary, key));
      // this.size--; // used only for testing purpose
      this.size = this.dictionary.size();
    }
  }

  public boolean containsKey(short key) {
    // true if indexOfKey returns a value bigger than -1
    return (indexOfKey(this.dictionary, key) != -1);
  }

  public boolean isEmpty() {
    // returns true if size == 0 (this.size == 0)
    return this.dictionary.size() == 0;
  }

  public int size() {
    // return size;
    return this.dictionary.size();
  }

  public void union(ShortToStringMap m) {
    for (int i = 0; i < m.dictionary.size(); i++) {
      // creates a temporary entry --> inserts it in the dictionary
      Dictionary entry = m.dictionary.get(i);
      this.put(entry.getKey(), entry.getValue());
    }
  }

  public void intersection(ShortToStringMap s) {
    for (int i = 0; i < this.dictionary.size(); i++) {
      Dictionary entry = this.dictionary.get(i);
      if (indexOfKey(s.dictionary, entry.getKey()) != -1) {
        entry.setValue(s.dictionary.get(
            indexOfKey(s.dictionary, entry.getKey())).getValue());
      } else {
        this.remove(entry.getKey());
        i--;
      }
    }
  }

  public String toString() {
    StringBuffer output = new StringBuffer();
    output.append("{");
    for (int i = this.dictionary.size() - 1; i >= 0; i--) {
      output.append(this.dictionary.get(i).toString());
      if (i != 0) {
        output.append(",");
      }
    }
    output.append("}");
    return output.toString();
  }
}