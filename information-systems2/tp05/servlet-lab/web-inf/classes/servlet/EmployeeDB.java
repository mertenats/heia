package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@SuppressWarnings("serial")
public class EmployeeDB extends HttpServlet {

  final static String         DBURL = "jdbc:mysql://localhost/phplab";
  private final static String USER  = "sinf";
  private final static String PASS  = "classT3";

  // ---------------------------------------------------
  // SERVLET LIFECYLCE METHODS
  // ---------------------------------------------------

  // servlet initialisation
  public void init() throws ServletException {
    // load JDBC driver for MySQL
    try {
      Class.forName("com.mysql.jdbc.Driver");
      System.out.println("INFO: EmployeeDB has loaded the JDBC Driver.");
    } catch (ClassNotFoundException e) {
      System.out
          .println("ERROR: EmployeeDB could not load MySQL driver class!");
    }
  }

  // handle GET requests
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    Connection conn = null;
    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    // include header and menu, start main div
    req.getRequestDispatcher("header.html").include(req, resp);
    req.getRequestDispatcher("menu.html").include(req, resp);
    out.println("<div id=\"main\">");

    // get action
    String requestedAction = "index";
    if (req.getParameter("action") != null) {
      requestedAction = req.getParameter("action");
    }

    // dispatch action
    switch (requestedAction) {
      case "add":
        actionAdd(req, resp);
        break;
      case "list":
        conn = getDBConnection(out);
        actionList(conn, out);
        break;
      case "form":
        actionForm(req, resp);
        break;
      case "delete":    // add Delete functionality
        actionDelete(req, resp);
        break;
      case "edit":    // add edit functionality
        actionEdit(req, resp);
        break;
      case "update":    // add update functionality
        actionUpdate(req, resp);
        break;
      case "index":
      default:
        actionIndex(req, resp);
        break;
    }

    // end main div, add footer and close output
    out.println("</div> ");
    req.getRequestDispatcher("footer.html").include(req, resp);
    out.close();

    // close DB connection (if any)
    if (conn != null)
      try {
        conn.close();
      } catch (Exception e) {
      }

  } // end doGet()

  // let POST requests be handled by doGet()
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    doGet(req, resp);
  }

  // unregister the JDBC driver when destroying the servlet
  public void destroy() {
    Enumeration<Driver> drivers = DriverManager.getDrivers();
    while (drivers.hasMoreElements()) {
      Driver driver = drivers.nextElement();
      try {
        DriverManager.deregisterDriver(driver);
        System.out
            .println("INFO: EmployeeDB has unregistered its MySQL driver.");
      } catch (SQLException e) {
        System.out.println(
            "ERROR: EmployeeDB could not unregister its MySQL driver\nException: "
                + e);
      }
    }
    try { // little workaround for Tomcat/JDBC bug
      com.mysql.jdbc.AbandonedConnectionCleanupThread.shutdown();
    } catch (Throwable t) {
    }
  } // end destroy()

  // ---------------------------------------------------
  // ACTION IMPLEMENTATIONS
  // ---------------------------------------------------

  // display home page
  private void actionIndex(HttpServletRequest req, HttpServletResponse resp) {
    try {
      req.getRequestDispatcher("home.html").include(req, resp);
    } catch (Exception e) {
    }
    ;
  }

  private void actionAdd(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {

    final PrintWriter out = resp.getWriter();
    Connection conn = getDBConnection(out);
    try {
      String ssn = req.getParameter("ssn");
      if (ssn == null || ssn.equals("") || ssn.length() > 3
          || Integer.parseInt(ssn) == 0) {
        throw new Exception("SSN not valid");
      }
      String lastname = req.getParameter("lastname");
      if (lastname == null || lastname.equals("")) {
        throw new Exception("Lastname missing");
      }
      String firstname = req.getParameter("firstname");
      String email = req.getParameter("email");

      String query = "INSERT INTO employee(ssn, lastname, firstname, email) VALUES(?, ?, ?, ?)";
      PreparedStatement stmt = conn.prepareStatement(query);

      stmt.setString(1, ssn);
      stmt.setString(2, lastname);
      stmt.setString(3, firstname);
      stmt.setString(4, email);

      stmt.execute();
      stmt.close();

      // session counter
      HttpSession session = req.getSession(true);
      Object cnt = session.getAttribute("count_entry");

      int counter = cnt == null ? 0 : (int) cnt;
      session.setAttribute("count_entry", ++counter);

      out.write("<h2>Action report</h2>");
      out.write("<p>Entry successfully added !</p>");
      if (counter == 1) {
        out.write("<p>This was your first entry</p>");
      } else {
        out.write("<p>This was not your first entry! You have already added "
            + counter + " entries.</p>");
      }
      out.write("<p>Go back to the <a href=\"?action=list\">list</a>.</p>");

    } catch (Exception e) {
      out.write("<h2>Action report</h2>");
      out.write("<p>An error has occured !</p>");
      out.write("<p>" + e.getMessage() + "</p>");
      out.write("<p>Go back to the <a href=\"?action=form\">form</a>.</p>");
    } finally {
      out.write("</body></html>");
      out.close();

      try {
        conn.close();
      } catch (SQLException e) {
        printSQLException(e, out);
      }
    }
  }
  
  private void actionUpdate(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {

    final PrintWriter out = resp.getWriter();
    Connection conn = getDBConnection(out);
    try {
      String ssn = req.getParameter("ssn");
      if (ssn == null || ssn.equals("") || ssn.length() > 3
          || Integer.parseInt(ssn) == 0) {
        throw new Exception("SSN not valid");
      }
      String lastname = req.getParameter("lastname");
      if (lastname == null || lastname.equals("")) {
        throw new Exception("Lastname missing");
      }
      String firstname = req.getParameter("firstname");
      String email = req.getParameter("email");

      String query = "UPDATE employee SET lastname = ?, firstname = ?,  email = ? WHERE ssn = ?";
      PreparedStatement stmt = conn.prepareStatement(query);
      stmt.setString(1, lastname);
      stmt.setString(2, firstname);
      stmt.setString(3, email);
      stmt.setString(4, ssn);
      stmt.execute();
      stmt.close();

      out.write("<h2>Action report</h2>");
      out.write("<p>Entry successfully edited !</p>");
      out.write("<p>Go back to the <a href=\"?action=list\">list</a>.</p>");

    } catch (Exception e) {
      out.write("<h2>Action report</h2>");
      out.write("<p>An error has occured !</p>");
      out.write("<p>" + e.getMessage() + "</p>");
    } finally {
      out.write("</body></html>");
      out.close();

      try {
        conn.close();
      } catch (SQLException e) {
        printSQLException(e, out);
      }
    }
  }

  private void actionDelete(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {

    final PrintWriter out = resp.getWriter();
    Connection conn = getDBConnection(out);
    try {
      String ssn = req.getParameter("ssn");
      if (ssn == null || ssn.equals("") || ssn.length() > 3
          || Integer.parseInt(ssn) == 0) {
        throw new Exception("SSN not valid");
      }

      String query = "DELETE FROM employee WHERE ssn = ?";
      PreparedStatement stmt = conn.prepareStatement(query);
      stmt.setString(1, ssn);
      stmt.execute();
      stmt.close();

      out.write("<h2>Action report</h2>");
      out.write("<p>Entry successfully deleted !</p>");
      out.write("<p>Go back to the <a href=\"?action=list\">list</a>.</p>");
    } catch (Exception e) {
      out.write("<h2>Action report</h2>");
      out.write("<p>An error has occured !</p>");
      out.write("<p>" + e.getMessage() + "</p>");
    } finally {
      out.write("</body></html>");
      out.close();

      try {
        conn.close();
      } catch (SQLException e) {
        printSQLException(e, out);
      }
    }
  }

  // display add form
  private void actionForm(HttpServletRequest req, HttpServletResponse resp) {
    try {
      req.getRequestDispatcher("form.html").include(req, resp);
    } catch (Exception e) {
    }
    ;
  }
  
  // display edit form
  private void actionEdit(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    final PrintWriter out = resp.getWriter();
    Connection conn = getDBConnection(out);
    try {
      String ssn = req.getParameter("ssn");
      if (ssn == null || ssn.equals("") || ssn.length() > 3
          || Integer.parseInt(ssn) == 0) {
        throw new Exception("SSN not valid");
      }

      String query = "SELECT * FROM employee WHERE ssn = ?";
      PreparedStatement stmt = conn.prepareStatement(query);
      stmt.setString(1, ssn);
      ResultSet rs = stmt.executeQuery();
      
      rs.next();
      
      out.write("<h2>Edit employee</h2>");
      out.write("<form action=\"?action=update\" method=\"POST\">");
      out.write("<table>");
      out.write("<tr>");
      out.write("<td><b>SSN</b> :</td>");
      out.write("<td><input type=\"text\" name=\"ssn\" size=\"3\" maxlength=\"3\" value=\"" + rs.getString(1) + "\"readonly/></td>");
      out.write("</tr>");
      out.write("<tr>");
      out.write("<td><b>Last name</b><sup>*</sup> :</td>");
      out.write("<td><input type=\"text\" name=\"lastname\" size=\"20\" value=\"" + rs.getString(2) + "\"/></td>");
      out.write("</tr>");
      out.write("<tr>");
      out.write("<td>First name :</td>");
      out.write("<td><input type=\"text\" name=\"firstname\" size=\"20\" value=\"" + rs.getString(3) + "\" /></td>");
      out.write("</tr>");
      out.write("<tr>");
      out.write("<td>E-mail :</td>");
      out.write("<td><input type=\"text\" name=\"email\" size=\"20\" value=\"" + rs.getString(4) + "\" /></td>");
      out.write("</tr>");
      out.write("<tr>");
      out.write("<td></td>");
      out.write("<td align=\"right\" bgcolor=\"#ccddff\">");
      out.write("<input type=\"submit\" name=\"submit\" value=\"Edit\" >");
      out.write("</td>");
      out.write("</tr>");
      out.write("</table>");
      out.write("<br>");
      out.write("<i>(*) mandatory</i>");
      out.write("</form>");
      
      stmt.close();
      
    } catch (Exception e) {
      out.write("<h2>Action report</h2>");
      out.write("<p>An error has occured !</p>");
      out.write("<p>" + e.getMessage() + "</p>");
    } finally {
      out.write("</body></html>");
      out.close();

      try {
        conn.close();
      } catch (SQLException e) {
        printSQLException(e, out);
      }
    }
  }

  // retrieve and display employee list
  private void actionList(Connection conn, PrintWriter out) {
    if (conn == null)
      return;
    out.println("<h2>List of Employees</h2>");

    try {
      // prepare and execute SQL query
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM employee ORDER BY ssn");

      // write HTML table from result
      writeHTMLTableFromResultSet(out, rs);

      // clean up
      rs.close();
      stmt.close();

    } catch (SQLException ex) {
      printSQLException(ex, out);
    }
  }

  // ---------------------------------------------------
  // UTILITY METHODS
  // ---------------------------------------------------

  // get a DB connection from DriverManager
  private Connection getDBConnection(PrintWriter out) {
    Connection conn = null;
    try {
      conn = DriverManager.getConnection(DBURL, USER, PASS);
    } catch (SQLException ex) {
      printSQLException(ex, out);
      conn = null;
    }
    return conn;
  }

  // produce an HTML table containing the results
  private void writeHTMLTableFromResultSet(PrintWriter out, ResultSet rs)
      throws SQLException {

    out.println("<table class=\"employee-list\">");

    // write header row
    ResultSetMetaData metadata = rs.getMetaData();
    int numCols = metadata.getColumnCount();
    out.println("<tr>");
    for (int i = 1; i <= numCols; i++) {
      out.print("<th>" + metadata.getColumnName(i) + "</th>");
    }              // end for (columns)
    out.println("<th>actions</th>"); // additional column for actions
    out.println("</tr>");

    // write entry rows
    while (rs.next()) {
      out.println("<tr>");
      for (int i = 1; i <= numCols; i++) {
        out.print("<td>" + rs.getString(i) + "</td>");
      }              // end for (columns)
      // additional column for action icons/links
      out.println("<td align=\"center\">");
      // edit form
      out.println("  <form action=app?action=edit method=post>");
      out.println("    <input type=image src=img/edit.png />");
      out.println(
          "    <input type=hidden name=ssn value= " + rs.getString(1) + " />");
      out.println("  </form>");
      // delete form
      out.println("  <form action=app?action=delete method=post>");
      out.println("    <input type=image src=img/delete.png />");
      out.println(
          "    <input type=hidden name=ssn value= " + rs.getString(1) + " />");
      out.println("  </form>");
      out.println("</td>");
      out.println("</tr>");
    }
    out.println("</table>");
  }

  //
  private void printSQLException(SQLException ex, PrintWriter out) {
    out.println("<h4 class=\"result-error\">SQL Exception:</h4>");
    while (ex != null) {
      out.println(ex.getMessage().replaceAll("(\n|\r)+", "<br>"));
      ex = ex.getNextException();
    }
  }
}
