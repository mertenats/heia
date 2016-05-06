/*---------------------------------------------------
 *  COFFEE SHOP - ShoppingCartBean (B)
 *  This bean is used to hold all items (CartItem)
 *  a customer has put in his shopping cart.
 *---------------------------------------------------
 * HEIA-FR / R. Scheurer (2015-16)
 *---------------------------------------------------*/
package shop;

import java.util.Enumeration;
import java.util.Vector;

public class ShoppingCartBean {

  // growable array of cart items
  private Vector<CartItem> cart = new Vector<CartItem>();

  public ShoppingCartBean() {
  }
  
  public void empty() {
    cart = new Vector<CartItem>();
  }

  // add a given quantity of this item to the cart
  public void addToCart(CatalogItem p, int quantity) {
    // is there already an entry for this catalog item ?
    for (int i=0; i<cart.size(); i++) {
      if (cart.get(i).getProd().getId() == p.getId()) {
        cart.get(i).addQuantity(quantity);
        return;
      }
    }
    // otherwise add new entry
    cart.add(new CartItem(p, quantity));
  }

  // remove the item identified by 'id' from this cart
  public void removeFromCart(int id) {
    try {
      //cart.remove(id);
      for (int i=0; i<cart.size(); i++) {
        if (cart.get(i).getId() == id)
          cart.remove(i);
      }
    } catch (ArrayIndexOutOfBoundsException e) {
      // silently ignore this exception
    }
  }
  
  // tells if the cart is empty
  public boolean isEmpty() {
    return cart.size() == 0;
  }

  // return an enumeration of the items in this cart
  public Enumeration<CartItem> getCartItems() {
    return cart.elements();
  }
  
  // for debugging purposes: String representation of shopping cart
  public String toString() {
    return cart.toString();
  }
  
// Usage of enumerations:
// ----------------------
//
// ShoppingCartBean cart = ...;
// Enumeration<CartItem> cartItems = cart.getCartItems();
// CartItem item = null;
// while(cartItems.hasMoreElements()) {
//   item = cartItems.nextElement();
//   ...
// }
  
}
