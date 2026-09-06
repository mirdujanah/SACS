package SACS;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ReceiversReject extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try (Connection conn = SQLconnection.getconnection();
                PreparedStatement update = conn.prepareStatement(
                        "UPDATE datareceiver SET ustatus = 'Rejected' WHERE id = ?")) {
            update.setString(1, request.getParameter("uid"));
            response.sendRedirect(update.executeUpdate() > 0
                    ? "ReceiverAct.jsp?Rejected" : "ReceiverAct.jsp?Failed");
        } catch (SQLException ex) {
            throw new ServletException("Unable to reject receiver", ex);
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
        return "Rejects a data receiver";
    }
}
