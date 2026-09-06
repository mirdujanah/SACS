/*
 */
package SACS;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Murthi
 */
public class DPLog extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession();
            String email = request.getParameter("email");
            String pass = request.getParameter("password");

            if (email == null || pass == null || email.trim().isEmpty() || pass.isEmpty()) {
                response.sendRedirect("DataPublisher.jsp?Authentication_Failed");
                return;
            }

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            String time = dateFormat.format(date);

            try (Connection con = SQLconnection.getconnection();
                    PreparedStatement login = con.prepareStatement(
                            "SELECT id, name, email, pass FROM datapub WHERE email = ? AND ustatus = 'Active'");
                    PreparedStatement update = con.prepareStatement(
                            "UPDATE datapub SET lastlog = ?, pass = ? WHERE email = ?")) {
                login.setString(1, email);
                String storedPassword;
                try (ResultSet rs = login.executeQuery()) {
                    if (!rs.next()) {
                        response.sendRedirect("DataPublisher.jsp?Authentication_Failed");
                        return;
                    }
                    storedPassword = rs.getString("pass");
                    boolean valid;
                    try {
                        valid = PasswordUtil.matches(pass, storedPassword);
                    } catch (GeneralSecurityException ex) {
                        throw new ServletException("Unable to verify password", ex);
                    }
                    if (!valid) {
                        response.sendRedirect("DataPublisher.jsp?Authentication_Failed");
                        return;
                    }
                    session.setAttribute("dpid", rs.getString("id"));
                    session.setAttribute("dpname", rs.getString("name"));
                    session.setAttribute("dpmail", rs.getString("email"));
                }
                String passwordHash;
                try {
                    passwordHash = PasswordUtil.isHash(storedPassword)
                            ? storedPassword : PasswordUtil.hash(pass);
                } catch (GeneralSecurityException ex) {
                    throw new ServletException("Unable to protect password", ex);
                }
                update.setString(1, time);
                update.setString(2, passwordHash);
                update.setString(3, email);
                update.executeUpdate();
                response.sendRedirect("DPHome.jsp?Success");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DPLog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}