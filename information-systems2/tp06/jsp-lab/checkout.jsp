<%-- JSP Lab 2015/16 --%>
<%-- import java classes --%>
<%@ page import="shop.CatalogItem" %>
<%@ page import="shop.CartItem" %>
<%@ page import="java.util.Enumeration" %>

<%-- make use of required Java beans and specify desired scope --%>
<jsp:useBean id="catalog" class="shop.CatalogBean" scope="application" />
<jsp:useBean id="cart" class="shop.ShoppingCartBean" scope="session" />
<jsp:useBean id="user" class="shop.UserBean" scope="session" />
<jsp:setProperty name="catalog" property="*" />
<jsp:setProperty name="cart" property="*" />
<jsp:setProperty name="user" property="*" />

<jsp:include page="header.inc.html" />
<jsp:include page="menu.inc.jsp" />

<div id="main">
  <h2>Checkout</font></h2>

<%
  if (user.getUserName() == null) {
    response.sendRedirect("http://localhost:8080/jsp-lab/login-form.jsp");

  // Checks if the "checkout" button has been pressed
  else if (request.getParameter("checkout") != null) {
    // Clears the cart
    cart.empty();
%>
      <p><span style="color:red">Thank you for your order. See you soon.</span></p>
<%
  } else if (user.getAddress() == null) {
%>
    <p><span style="color:red">You have to enter an address. Please use the address form.</span></p>
<%
  // if the cart is already empty...
  } else if (cart.isEmpty()) {
%>
    <p><span style="color:red">Your shopping cart is empty!</span></p>
<%
  // If the cart is not empty, it displays the cart & the address
  } else {
%>
<h4>This is the content of your shoping cart & your address</h4>
    
  <table class="table-with-header">
    <tr>
      <th>Id</th>
      <th>Product Name</th>
      <th>Unit Price</th>
      <th>Quantity</th>
      <th>Cost</th>
    </tr>
<%
    // loop repeating the following table line per item in cart
    Enumeration<CartItem> items = cart.getCartItems();
    CartItem item = null;
    while (items.hasMoreElements()) {
      item = items.nextElement();
      CatalogItem product = item.getProd();
%>
    <tr>
      <td><%= item.getId() %></td>
      <td><%= product.getName() %></td>
      <td><%= product.getPrice() %> CHF</td>
      <td><%= item.getQuantity() %></td>
      <td><%= item.getQuantity() * product.getPrice() %></td>
    </tr>
<%
    // (end of loop)
    }
%>
  </table>

  <h4>Your address</h4>
  <table>
    <tr>
      <td>First Name :</td>
      <td><%= user.getFirstName() %></td>
    </tr>
    <tr>
      <td>Last Name :</td>
      <td><%= user.getLastName() %></td>
    </tr>
    <tr>
      <td>Address :</td>
      <td><%= user.getAddress() %></td>
    </tr>
    <tr>
      <td>ZIP / City :</td>
      <td><%= user.getZipCode() %> <%= user.getCity() %></td>
    </tr>
  </table>

    <!-- Checkout form -->
  <form action="checkout.jsp" method="post">
    <input name="checkout" type="hidden" id="checkout" value="true" />
    <td><input type="submit" name="submit" value="Checkout" /></td>
  </form>
<%
  }
%>


</div> <!-- end div#main -->

<jsp:include page="footer.inc.jsp" />
