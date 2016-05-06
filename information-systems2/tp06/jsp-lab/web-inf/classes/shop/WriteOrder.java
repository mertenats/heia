/*---------------------------------------------------
 * COFFEE SHOP - WriteOrder (B)
 * This servlet is used to write orders to file
 *---------------------------------------------------
 * HEIA-FR / R. Scheurer (2015-16)
 *---------------------------------------------------*/
package shop;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.*;

public class WriteOrder extends HttpServlet  {
  
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    // get user and cart
    // TODO user = UserBean
    // TODO cart = ShoppingCart

    String timestamp = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());
    String filename = this.getServletConfig().getServletContext().getRealPath("orders/order-"+timestamp+".txt");
    
    try {
      System.err.println("Start writing order ...");
      FileWriter writer = new FileWriter(filename, false);
      writer.write("Order from user '"+user.getUsername()+"'\n");
      writer.write("-------------------------------\n");
      writer.write("Last name: " + user.getName()+"\n");
      writer.write("First name: " + user.getFirstname() +"\n");
      writer.write("Address: " + user.getAddress()+"\n");
      writer.write("ZIP/City: " + user.getZip() + " " + user.getCity()+"\n");
      writer.write("-------------------------------\n");
      writer.write("ID\tProduct\tQty\tPrice\n");
   
      int tot = 0;
      Enumeration<CartItem> cartItems = cart.getCartItems();
      CartItem item = null;
      while(cartItems.hasMoreElements()) {
        item = cartItems.nextElement();
        tot+=(item.getProd().getPrice() * item.getQuantity());
        writer.write(item.getProd().getId() + "\t"+item.getProd().getName()+"\t"+item.getQuantity()+"\t"+item.getProd().getPrice()+"\n");
      }
      writer.write("Total : "+tot+" CHF\n");
      writer.write("-------------------------------\n");
      writer.close();
      System.err.println("Order written.");
      // TODO how to return success code to calling JSP ?
    } catch (Exception e) { }

  }
  
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    doGet(req, resp);
  }
  
}
