/*---------------------------------------------------
 *  COFFEE SHOP - CatalogItem (B)
 *  This class represents items in a shop catalog.
 *  The items are products with name, price and id.
 *---------------------------------------------------
 * HEIA-FR / R. Scheurer (2015-16)
 *---------------------------------------------------*/
package shop;

public class CatalogItem {

  private String  name  = new String();
  private Integer price = new Integer(0);
  private int     id    = 0;

  public CatalogItem(int id, String name, Integer price) {
    this.id = id;
    this.name = name;
    this.price = price;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getPrice() {
    return price;
  }

  public void setPrice(Integer price) {
    this.price = price;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }
  
  public String toString() {
    return "{CatalogItem: id=" + id
        + ", name=" + name
        + ", price=" + price + "}";
  }

}
