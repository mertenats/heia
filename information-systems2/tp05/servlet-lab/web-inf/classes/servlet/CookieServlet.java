package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class CookieServlet extends HttpServlet {

  // handle GET requests
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    out.println("<html><head><title>Cookie</title></head><body>");
    out.println("<h2>Do you want to add a new Cookie ?</h2><br>");
    out.println("<form action=?action=add name=\"myform\" method=\"POST\">");
    out.println("Name : <input type=\"text\" name=\"name\" size=\"20\" />");
    out.println("Value : <input type=\"text\" name=\"value\" size=\"20\" />");
    out.println("Age : <input type=\"text\" name=\"age\" size=\"20\" />");
    out.println("<input type=\"Submit\" value=\"add\" name=\"Submit\">");
    out.println("</form>");

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
      case "delete":
        actionDelete(req, resp);
        break;
      case "index":
      default:
        actionList(req, resp);
        break;
    }

    out.write("</body></html>");
    out.close();
  } // end doGet()

  // let POST requests be handled by doGet()
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    doGet(req, resp);
  }

  // ---------------------------------------------------
  // ACTION IMPLEMENTATIONS
  // ---------------------------------------------------

  // retrieve and display cookies' list
  private void actionList(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {

    final PrintWriter out = resp.getWriter();
    out.println("<h2>List of Cookie(s)</h2>");
    out.println("<table border=\"1px solid black\">");
    out.println("<tr>");
    out.println("<th>Name</th>");
    out.println("<th>Value</th>");
    out.println("<th>Max Age</th>");
    out.println("<th>Remove</th>");
    out.println("</tr>");

    // retrieves the cookie(s)
    Cookie cookies[];
    cookies = req.getCookies();

    // generates the table
    for (int i = 0; i < cookies.length; i++) {
      out.println("<tr>");
      out.println("<td>" + cookies[i].getName() + "</td>");
      out.println("<td>" + cookies[i].getValue() + "</td>");
      out.println("<td>" + cookies[i].getMaxAge() + "</td>");
      out.println("<td>");
      out.println("<form action=?action=delete method=POST>");
      out.println("<input type=\"hidden\" name=\"name\" value=\""
          + cookies[i].getName() + "\" />");
      out.println("<input type=image src=empl-db/img/delete.png />");
      out.println("</form>");
      out.println("</td>");
      out.println("</tr>");
    }
    out.println("</table>");
  }

  private void actionDelete(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    // http://stackoverflow.com/questions/890935/how-do-you-remove-a-cookie-in-a-java-servlet
    String name = req.getParameter("name");
    if (name.equals("") == false) {
      Cookie cookies[] = req.getCookies();
      if (cookies != null)
        for (int i = 0; i < cookies.length; i++) {
          if (cookies[i].getName().equals(name)) {
            cookies[i].setValue("");
            cookies[i].setPath("/");
            cookies[i].setMaxAge(0);
            resp.addCookie(cookies[i]);
          }
        }
    }
  }

  private void actionAdd(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {

    // if a new cookie is sent by POST
    String name = req.getParameter("name");
    String value = req.getParameter("value");
    String age = req.getParameter("age");
    if (name.equals("") == false && value.equals("") == false
        && age.equals("") == false) {
      Cookie cookie = new Cookie(name, value);
      cookie.setMaxAge(Integer.parseInt(age));
      cookie.setPath(req.getRequestURI());
      resp.addCookie(cookie);
    }
  }
}