<jsp:include page="header.inc.html" />
<script src="js/validation-5.js"></script>
<jsp:include page="menu.inc.html" />

<jsp:useBean id="user" class="shop.UserBean" scope="session" />
<jsp:setProperty name="user" property="*" />

  <div id="main">
    <h2>Phase 5 - Check ZIP Format</h2>

<h4>Enter your address:</h4>

<form action="#" id="addressform" method="POST" accept-charset="utf-8" >
  <table>
   	<tr>
	  <td>Firstname :</td>
	  <td><input type="text" id="firstname" name="firstname" size="30" value="<%= user.getFirstname() %>" /></td>
    <td id="firstname-errmsg" class="msg-error"></td>
	</tr>
	<tr>
      <td>Name :</td>
      <td><input type="text" id="lastname" name="lastname" size="30" value="<%= user.getLastname() %>" /></td>
    <td id="lastname-errmsg" class="msg-error"></td>
	</tr>
   	<tr>
	  <td>Address :</td>
	  <td><input type="text" id="address" name="address" size="30" value="<%= user.getAddress() %>" /></td>
    <td id="address-errmsg" class="msg-error"></td>
	</tr>
   	<tr>
	  <td>ZIP / City :</td>
	  <td>
	    <input type="text" id="zip" name="zip" size="4" value="<%= user.getZip() %>" />
	    <input type="text" id="city" name="city" size="22" value="<%= user.getCity() %>" readonly />
	  </td>
    <td id="zip-errmsg" class="msg-error"></td>
	</tr>
	<tr class="submit-line">
	  <td colspan="2" align="right" align="right">
		<input type="Submit" value="Save" name="submit">
	  </td>
<%
  if (request.getParameter("submit") != null) {
    %><td id="submit-msg" class="msg-ok">&nbsp;Address has been saved.</td><%
  } else {
    %><td id="submit-msg"></td><%
  }
%>
	</tr>
  </table>
</form>

  </div>
  
<jsp:include page="footer.inc.jsp" />
