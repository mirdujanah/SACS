package SACS;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestReject extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try (Connection conn = SQLconnection.getconnection();
                PreparedStatement update = conn.prepareStatement(
                        "UPDATE searchreq SET vstatus = 'Rejected' WHERE id = ?")) {
            update.setString(1, request.getParameter("id"));
            update.executeUpdate();
            response.sendRedirect("AccessRequest.jsp?Rejected");
        } catch (SQLException ex) {
            throw new ServletException("Unable to reject request", ex);
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
        return "Rejects a file access request";
    }
}
