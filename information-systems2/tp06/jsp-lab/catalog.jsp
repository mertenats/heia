<%-- JSP Lab 2015/16 --%>
<%-- import java classes --%>
<%@ page import="shop.CatalogItem" %>
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
  <h2>The Catalog</h2>
  <h4>The following products are available:</h2>

    <table class="table-with-header">
      <tr>
        <th>Id</th>
        <th>Product Name</b></th>
        <th>Unit Price</th>
        <th>Quantity</th>
        <th>Cart </th>
      </tr>
  <%
      Enumeration<CatalogItem> cat = catalog.getCatalogItems();
      CatalogItem item = null;
      
      while (cat.hasMoreElements()) {
        item = cat.nextElement();
        // loop repeating the following form (one form per line/product)
  %>
        <!-- we use a form to send the product ID to add to the cart -->
        <form action="catalog.jsp" method="post">
        <tr>
          <td><%= item.getId() %>
            <input name="pId" type="hidden" id="pId" value="<%= item.getId() %>" /></td>
            <td><%= item.getName() %></td>
            <td><%= item.getPrice() %> CHF</td>
            <td><input name="quantity" type="text" id="quantity" size="4" value="1"/></td>
            <td><input type="submit" name="submit" value="Add" /></td>
          </tr>
        </form>
<% 
      } // (end of loop)
%>
    </table>

<%
    try {
      // Retrieves the product ID (method POST)
      if (request.getParameter("pId") != null) {
        int id = Integer.parseInt(request.getParameter("pId"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));

        // Gets the product from the id and inserts it into the cart
        CatalogItem itemToAdd = catalog.getCatalogItem(id);
        cart.addToCart(itemToAdd, quantity);
%>
        <p><span style="color:red">Successfully added <%= quantity %>x <%= itemToAdd.getName() %></span></p>
<%
      }
    } catch (Exception e) {
%>
      <p><span style="color:red">Your item couldn't be added to the cart. Please try again later.</span></p>
<%
    }
%>

</div> <!-- end div#main -->

<jsp:include page="footer.inc.jsp" />
