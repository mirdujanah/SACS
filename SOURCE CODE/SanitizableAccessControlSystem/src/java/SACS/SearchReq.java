package SACS;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SearchReq extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        String fid = request.getParameter("fid");
        String drid = (String) session.getAttribute("drid");
        String drname = (String) session.getAttribute("drname");
        String drmail = (String) session.getAttribute("drmail");
        String time = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());

        try (Connection conn = SQLconnection.getconnection();
                PreparedStatement lookup = conn.prepareStatement("SELECT dpid, dpname, fname FROM uploads WHERE id = ?");
                PreparedStatement insert = conn.prepareStatement(
                        "INSERT INTO searchreq (drid, drname, drmail, fname, fid, vstatus, time, dkey, dpid, dpname)"
                        + " VALUES (?, ?, ?, ?, ?, 'waiting', ?, 'Not Generated', ?, ?)")) {
            lookup.setString(1, fid);
            try (ResultSet rs = lookup.executeQuery()) {
                if (!rs.next()) {
                    response.sendRedirect("SearchFiles.jsp?KwordNot");
                    return;
                }
                insert.setString(1, drid);
                insert.setString(2, drname);
                insert.setString(3, drmail);
                insert.setString(4, rs.getString("fname"));
                insert.setString(5, fid);
                insert.setString(6, time);
                insert.setString(7, rs.getString("dpid"));
                insert.setString(8, rs.getString("dpname"));
            }
            response.sendRedirect(insert.executeUpdate() > 0
                    ? "SearchFiles.jsp?Requestsent" : "SearchFiles.jsp?Failed");
        } catch (SQLException ex) {
            throw new ServletException("Unable to create search request", ex);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Creates a file access request";
    }
}
