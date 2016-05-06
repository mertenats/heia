/*----------------------------------------------------
 * COFFEE SHOP - UserBean
 * This class should contain all relevant information
 * about the logged-in user, e.g. user name & address
 *---------------------------------------------------
 * HEIA-FR / R. Scheurer (2015/16)
 *---------------------------------------------------*/
package shop;

public class UserBean {
  private String name;
  static int     actId = 1;
  private int    id;
  
  // more attributs...
  private String firstName;
  private String lastName;
  private String address;
  private int zipCode;
  private String city;

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public UserBean() {
    this.id = actId++;
  }

  public int getId() {
    return id;
  }
  
  public String getUserName() {
    return name;
  }
  
  public void setUserName(String name) {
    this.name = name;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public int getZipCode() {
    return zipCode;
  }

  public void setZipCode(int zipCode) {
    this.zipCode = zipCode;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }
}

