package servlet;

import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class HelloWorld extends HttpServlet {

  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    resp.setContentType("text/html");
    final PrintWriter out = resp.getWriter();
    out.println("<html><body>");
    out.println("<h1>Hello World</h1>");
    out.println("<p><a href=\"../\">Back to Home Page</a></p>");
    out.println("</body></html>");
  }
}
