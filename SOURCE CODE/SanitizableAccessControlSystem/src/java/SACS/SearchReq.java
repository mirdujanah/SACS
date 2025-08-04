/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package SACS;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
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
public class SearchReq extends HttpServlet {

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
            /* TODO output your page here. You may use following sample code. */
            HttpSession session = request.getSession(true);
            String fid = request.getParameter("fid");
            String drid = (String) session.getAttribute("drid");
            String drname = (String) session.getAttribute("drname");
            String drmail = (String) session.getAttribute("drmail");

            System.out.println("fid" + fid + "drid=" + drid + "drname=" + drname);

            Connection con = null;
            Statement st = null;
            Statement st1 = null;
            ResultSet rs = null;

            Connection conn = SQLconnection.getconnection();
            Statement sto = conn.createStatement();
            st = conn.createStatement();
            st1 = conn.createStatement();
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            String time = dateFormat.format(date);
            System.out.println("current Date " + time);
            
             Random RANDOM = new SecureRandom();
            int PASSWORD_LENGTH = 8;
            String letters = "ABCD12EFGHIJ34KLMN89OPQRST67UVXYZ5";
            String trapdoor = "";
            for (int i = 0; i < PASSWORD_LENGTH; i++) {
                int index = (int) (RANDOM.nextDouble() * letters.length());
                trapdoor += letters.substring(index, index + 1);
            }
            //=====================================================================================
            try {
                rs = st.executeQuery("select * from uploads where id='" + fid + "'");

                if (rs.next()) {
                    String doid = rs.getString("dpid");
                    String doname = rs.getString("dpname");
                    String fname = rs.getString("fname");
                   
                    String hashvalue = "Not Generated";
                    System.out.println("accesspol" + fname);

                    int i = st1.executeUpdate("INSERT into searchreq(drid, drname, drmail, fname, fid, vstatus, time, dkey, dpid, dpname) values('" + drid + "','" + drname + "','" + drmail + "','" + fname + "','" + fid + "','waiting','" + time + "','Not Generated','" + doid + "','" + doname + "')");
                    if (i != 0) {
                        response.sendRedirect("SearchFiles.jsp?Requestsent");
                    } else {
                        response.sendRedirect("SearchFiles.jsp?Failed");
                    }

                } else {
                    response.sendRedirect("SearchFiles.jsp?KwordNot");

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } catch (Exception ex) {
            Logger.getLogger(SearchReq.class.getName()).log(Level.SEVERE, null, ex);
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
