<jsp:useBean id="user" class="shop.UserBean" scope="session" />
<jsp:setProperty name="user" property="*" />

<div id="nav">
  <ul>
    <li><a href="index.jsp">Home</a></li>
    <%
    if (user.getUserName() != null) {
    %>
    <li><a href="catalog.jsp">Catalog</a></li>
    <li><a href="cart.jsp">Shopping Cart</a></li>
    <li><a href="address-form.jsp">Address</a></li>
    <li><a href="checkout.jsp">Check-out</a></li>
    <li><a href="logout.jsp">Logout</a></li>
    <% } else
      out.println("<li><a href=\"login-form.jsp\">Login</a></li>");
    %>
  </ul>
</div>
