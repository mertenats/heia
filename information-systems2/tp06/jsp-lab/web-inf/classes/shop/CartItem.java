/*---------------------------------------------------
 *  COFFEE SHOP - CartItem
 *  This class represents items in a shopping cart.
 *  The attribute "quantity' indicates how many of
 *  this items have been put in the cart.
 *---------------------------------------------------
 * HEIA-FR / R. Scheurer (2015-16)
 *---------------------------------------------------*/
package shop;

public class CartItem {

  private CatalogItem product  = null;
  private int         quantity = 0;
  static int          actId    = 1;
  private int         id       = actId++;

  public int getId() {
    return id;
  }

  public CartItem(CatalogItem p, int q) {
    this.product = p;
    this.quantity = q;
  }

  public CatalogItem getProd() {
    return product;
  }

  public void setProd(CatalogItem product) {
    this.product = product;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public void addQuantity(int quantity) {
    this.quantity += quantity;
  }

  public String toString() {
    return "{CartItem: id=" + id + ", productId=" + product.getId() + ", qty="
        + quantity + "}";
  }

}
