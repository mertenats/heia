  <!-- FOOTER -->
  <jsp:useBean id="user" class="shop.UserBean" scope="session" />
  
  <div id="footer">
   SINF2 JSP Lab 2015-16 / implemented by <font color="red">Roldan & Mertenat</font>
   <span style="float:right;">User: <font color="red">
    <%
    if (user.getUserName() != null)
      out.println(user.getUserName());
    else 
    out.println("Guest");
    %>



  </font></span>
</div>

</body>
</html>