/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package SACS;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.security.Key;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Murthi
 */
public class Download extends HttpServlet {

    public static final String ALGO = "AES";
    public static byte[] keyValue = null;

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
            String fileid = request.getParameter("fid");
            String dkey = request.getParameter("dkey");
            String id = request.getParameter("id");
            String pathf = "D:\\";
            Connection con = SQLconnection.getconnection();
            Statement st = con.createStatement();
            Statement st1 = con.createStatement();
            Statement sto = con.createStatement();
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            String time = dateFormat.format(date);
            System.out.println("current Date " + time);
            HttpSession session = request.getSession(true);
            String drname = (String) session.getAttribute("drname");
            String drid = (String) session.getAttribute("drid");
            String drmail = (String) session.getAttribute("drmail");
            ResultSet rt = st.executeQuery("select * from uploads where id='" + fileid + "' and skey ='" + dkey + "'");

            if (rt.next()) {
                String fname = rt.getString("fname");
                String enkey = rt.getString("enkey");
                String oid = rt.getString("dpid");
                String oname = rt.getString("dpname");
                String ftype = rt.getString("ftype");
                String blob = rt.getString("upfile");
                
                keyValue =dkey.getBytes();
                
                String file1=decryption(blob);
                
                 keyValue =enkey.getBytes();
                 
                 String text=decryption(file1);

                int k = st1.executeUpdate("insert into downloads (drid, drname, dpid, dpname, fid, fname, time)values('" + drid + "','" + drname + "','" + oid + "','" + oname + "','" + fileid + "','" + fname + "','" + time + "')");

                response.setHeader("Content-Disposition", "attachment;filename=\"" + fname + "" + "\"");
                out.write(text);
                out.close();
                rt.close();
                con.close();

                response.sendRedirect("RequestedFiles.jsp?path");
            } else {
                response.sendRedirect("RequestedFiles.jsp?InvalidKey");

            }

        } catch (SQLException ex) {
            Logger.getLogger(Download.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Throwable ex) {
            Logger.getLogger(Download.class.getName()).log(Level.SEVERE, null, ex);
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

    public static String encryption(String data) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encVal);
    }

    public static String decryption(String encryptedData) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = Base64.getDecoder().decode(encryptedData);
        byte[] decValue = c.doFinal(decordedValue);
        return new String(decValue);
    }

    public static Key generateKey() throws Exception {
        return new SecretKeySpec(keyValue, ALGO);
    }

    static void readWrite(RandomAccessFile raf, BufferedOutputStream bw, long numBytes) throws IOException {
        byte[] buf = new byte[(int) numBytes];
        int val = raf.read(buf);
        if (val != -1) {
            bw.write(buf);
        }
    }
}
