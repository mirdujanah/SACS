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

/**
 *
 * @author Murthi
 */
public class DataPubReg extends HttpServlet {

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
            String name = request.getParameter("name");
            String mail = request.getParameter("email");
            String pass = request.getParameter("pass");
            String phone = request.getParameter("phone");
            String address = request.getParameter("address");
            if (name == null || mail == null || pass == null || phone == null || address == null
                    || name.trim().isEmpty() || mail.trim().isEmpty() || pass.isEmpty()) {
                response.sendRedirect("DataPublisher.jsp?failed");
                return;
            }

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            String time = dateFormat.format(date);

            final String passwordHash;
            try {
                passwordHash = PasswordUtil.hash(pass);
            } catch (GeneralSecurityException ex) {
                throw new ServletException("Unable to protect password", ex);
            }

            try (Connection conn = SQLconnection.getconnection();
                    PreparedStatement existing = conn.prepareStatement(
                            "SELECT 1 FROM datapub WHERE email = ?");
                    PreparedStatement insert = conn.prepareStatement(
                            "INSERT INTO datapub (name, email, phone, address, pass, ustatus, regtime, vparam)"
                            + " VALUES (?, ?, ?, ?, ?, 'No', ?, 'No')")) {
                existing.setString(1, mail);
                try (ResultSet rs = existing.executeQuery()) {
                    if (rs.next()) {
                        response.sendRedirect("DataPublisher.jsp?mailid");
                        return;
                    }
                }
                insert.setString(1, name);
                insert.setString(2, mail);
                insert.setString(3, phone);
                insert.setString(4, address);
                insert.setString(5, passwordHash);
                insert.setString(6, time);
                if (insert.executeUpdate() > 0) {
                    response.sendRedirect("DataPublisher.jsp?success");
                } else {
                    response.sendRedirect("DataPublisher.jsp?failed");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataPubReg.class.getName()).log(Level.SEVERE, null, ex);
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
