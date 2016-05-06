<%-- JSP Lab 2015/16 --%>
<%-- import java classes --%>
<%@ page import="shop.CatalogItem" %>
<%@ page import="shop.CartItem" %>
<%@ page import="java.util.Enumeration" %>

<%-- make use of required Java beans and specify desired scope --%>
<jsp:useBean id="catalog" class="shop.CatalogBean" scope="application" />
<jsp:useBean id="cart" class="shop.ShoppingCartBean" scope="session" />
<jsp:useBean id="user" class="shop.UserBean" scope="session" />

<jsp:include page="header.inc.html" />
<jsp:include page="menu.inc.jsp" />

<%
if (user.getUserName() == null) {
  response.sendRedirect("http://localhost:8080/jsp-lab/login-form.jsp");
}
%>

<div id="main">
  <h2>Your shopping cart</font></h2>
  <h4>This is the actual content of your shoping cart:</h4>
	  
<table class="table-with-header">
  <tr>
    <th>Id</th>
    <th>Product Name</th>
    <th>Unit Price</th>
    <th>Quantity</th>
    <th>Cost</th>
    <th>Action</th>
  </tr>
<%
	// HINT: usage of enumerations --> see Java source of ShoppingCartBean !

  // loop repeating the following table line per item in cart
  Enumeration<CartItem> items = cart.getCartItems();
  CartItem item = null;
  while (items.hasMoreElements()) {
    item = items.nextElement();
    CatalogItem product = item.getProd();
%>
  <!-- We use a form to send the id by POST method -->
  <form action="cart.jsp" method="post">
  <tr>
    <td><%= item.getId() %> <input name="pId" type="hidden" id="pId" value="<%= item.getId() %>" /></td>
    <td><%= product.getName() %></td>
    <td><%= product.getPrice() %> CHF</td>
    <td><%= item.getQuantity() %></td>
    <td><%= item.getQuantity() * product.getPrice() %></td>
    <td><input type="submit" name="submit" value="Remove" /></td>
  </tr>
  </form>
<%
  // (end of loop)
  }
%>
</table>

<%
try {
  // retrieves the id sent by the form
  if (request.getParameter("pId") != null) {
    int id = Integer.parseInt(request.getParameter("pId"));
    CatalogItem itemToRemove = catalog.getCatalogItem(id);

    cart.removeFromCart(id); // removes the item from the cart
%>
    // Javascript code to reload the page
    <script>
      location.reload(true);
    </script>
<%
  }
} catch (Exception e) {
%>
    <p><span style="color:red">Your item couldn't be remove entirely from the cart. Please try again.</span></p>
<%
}
%>

</div> <!-- end div#main -->

<jsp:include page="footer.inc.jsp" />
