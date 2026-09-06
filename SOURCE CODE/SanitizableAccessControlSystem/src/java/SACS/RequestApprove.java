package SACS;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestApprove extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String id = request.getParameter("id");
        String fid = request.getParameter("fid");
        try (Connection conn = SQLconnection.getconnection();
                PreparedStatement lookup = conn.prepareStatement("SELECT skey FROM uploads WHERE id = ?");
                PreparedStatement update = conn.prepareStatement(
                        "UPDATE searchreq SET vstatus = 'Approved', dkey = ? WHERE id = ?")) {
            lookup.setString(1, fid);
            try (ResultSet rs = lookup.executeQuery()) {
                if (!rs.next()) {
                    response.sendRedirect("AccessRequest.jsp?Failed");
                    return;
                }
                update.setString(1, rs.getString("skey"));
            }
            update.setString(2, id);
            response.sendRedirect(update.executeUpdate() > 0
                    ? "AccessRequest.jsp?Approved" : "AccessRequest.jsp?Failed");
        } catch (SQLException ex) {
            throw new ServletException("Unable to approve request", ex);
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
        return "Approves a file access request";
    }
}
