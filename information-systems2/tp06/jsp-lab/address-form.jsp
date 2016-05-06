<%-- JSP Lab 2015/16 --%>
<%-- make use of required Java beans and specify desired scope --%>
<jsp:useBean id="user" class="shop.UserBean" scope="session" />

<jsp:include page="header.inc.html" />
<jsp:include page="menu.inc.jsp" />

<%
if (user.getUserName() == null) {
  response.sendRedirect("http://localhost:8080/jsp-lab/login-form.jsp");
}
%>

<div id="main">
  <h2>Address form</h2>
  <h4>Enter your address:</h4>

  <form action="address-form.jsp" method="POST">
    <table>
      <tr>
      <td>First Name :</td>
      <td><input type="text" name="firstname" size="30" /></td>
    </tr>
    <tr>
      <td>Last Name :</td>
      <td><input type="text" name="lastname" size="30" /></td>
    </tr>
      <tr>
      <td>Address :</td>
      <td><input type="text" name="address" size="30" /></td>
    </tr>
      <tr>
      <td>ZIP / City :</td>
      <td>
        <input type="text" name="zip" size="5" />
        <input type="text" name="city" size="20" />
      </td>
    </tr>
    <tr>
      <td colspan="2" align="right" align="right" bgcolor="#F0E3C4">
      <input type="Submit" name="action" value="Save">
      </td>
    </tr>
    </table>
  </form>
<%
  try {
    // Checks if the parameters given by the POST method are valid
    if ((request.getParameter("firstname") != null) && (request.getParameter("lastname") != null ) && (request.getParameter("address") != null) && (request.getParameter("zip") != null) && (request.getParameter("city") != null)) {
      // Retrieves the zip code
      int zipCode = Integer.parseInt(request.getParameter("zip"));

      // Sets the user bean with the address information
      user.setFirstName(request.getParameter("firstname"));
      user.setLastName(request.getParameter("lastname"));
      user.setAddress(request.getParameter("address"));
      user.setZipCode(zipCode);
      user.setCity(request.getParameter("city"));
%>
      <p><span style="color:red">Successfully added.</span></p>
<%
    }
  } catch (Exception e) {
%>
    <p><span style="color:red">Your address couldn't be added. Please try again.</span></p>
<%
  }
%>

</div> <!-- end div#main -->

<jsp:include page="footer.inc.jsp" />
