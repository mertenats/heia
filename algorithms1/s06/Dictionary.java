package s06;

public class Dictionary {
  private short  key;  // index value
  private String value; // the value corresponding to the index

  public Dictionary(short key, String value) {
    this.key = key;
    this.value = value;
  }

  public short getKey() {
    return key;
  }

  public void setKey(short key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}