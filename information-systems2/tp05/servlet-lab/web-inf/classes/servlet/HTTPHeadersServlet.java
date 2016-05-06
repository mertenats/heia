package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class HTTPHeadersServlet extends HttpServlet {

  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    resp.setContentType("text/html");
    final PrintWriter out = resp.getWriter();
    out.println("<html><body>");

    getHeaderFields(req, out);
    getParameters(req, out);

    out.println("</body></html>");
    out.close();
  }

  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    doGet(req, resp);
  }

  private void getHeaderFields(HttpServletRequest req, PrintWriter out) {
    out.println("<h2>Header fields (method: " + req.getMethod() + "): </h2>");
    out.println("<h3> name : value </h3>");

    Enumeration<String> headers = req.getHeaderNames();
    while (headers.hasMoreElements()) {
      String header = headers.nextElement();
      String value = req.getHeader(header);

      out.println("<p>" + header + " : " + value + "</p>");
    }
  }

  private void getParameters(HttpServletRequest req, PrintWriter out) {
    out.println("<h2>Parameters : </h2>");
    out.println("<h3> name : value </h3>");

    Enumeration<String> params = req.getParameterNames();
    while (params.hasMoreElements()) {
      String param = params.nextElement();
      String value = req.getParameter(param);

      out.println("<p>" + param + " : " + value + "</p>");
    }
  }
}
