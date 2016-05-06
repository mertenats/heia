<jsp:useBean id="user" class="shop.UserBean" scope="session" />
<jsp:setProperty name="user" property="*" />

<% user.setUserName(request.getParameter("username")); %>
<jsp:forward page="catalog.jsp" />
