<%-- JSP Lab 2015/16 B --%>
<jsp:include page="header.inc.html" />
<jsp:include page="menu.inc.jsp" />

<div id="main">

    <h2>Please Login</h2>

    <form name="loginform" action="connection.jsp" method="post" >
      <table border="0" cellpadding="2">
        <tr>
          <td>User name: </td>
          <td>
            <input name="username" type="text" id="username" size="20" />
          </td>
        </tr>
        <tr>
          <td colspan="2" align="right" bgcolor="#F0E3C4">
            <input type="submit" name="submit" value="Login" />
          </td>
        </tr>
      </table>
    </form>

</div>

<jsp:include page="footer.inc.jsp" />
