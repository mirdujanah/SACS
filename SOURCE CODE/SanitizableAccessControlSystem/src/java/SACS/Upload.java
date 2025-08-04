/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package SACS;

import com.oreilly.servlet.MultipartRequest;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.security.Key;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Murthi
 */
@MultipartConfig(maxFileSize = 1048576)
public class Upload extends HttpServlet {

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
    File file;
    final String filepath = "D:/";
    File file1;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here. You may use following sample code. */
            MultipartRequest m = new MultipartRequest(request, filepath);
            File file = m.getFile("upfile");

            HttpSession user = request.getSession(true);
            String dpid = user.getAttribute("dpid").toString();
            String dpname = user.getAttribute("dpname").toString();
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            String time = dateFormat.format(date);
            System.out.println("current Date " + time);
            String kword = m.getParameter("kword");
            String filename = file.getName();
            String path = file.getPath();
            String extension = "";

            int f = filename.lastIndexOf('.');
            if (f > 0) {
                extension = filename.substring(f + 1);
            }
            String ftype = extension;
            System.out.println("path " + path);

            Random RANDOM = new SecureRandom();
            int PASSWORD_LENGTH = 16;
            String letters = "abcdefghijklmnopqrstuvxyz123456789";
            String val = "";
            for (int i = 0; i < PASSWORD_LENGTH; i++) {
                int index = (int) (RANDOM.nextDouble() * letters.length());
                val += letters.substring(index, index + 1);
            }
            
            keyValue = val.getBytes();

            Connection conn = SQLconnection.getconnection();
            Connection con = SQLconnection.getconnection();
            Statement st = con.createStatement();

            String pathf = filepath + dpid;
            File newFolder = new File(pathf);
            boolean created = newFolder.mkdirs();

            BufferedReader br = new BufferedReader(new FileReader(file));
            StringBuilder sb1 = new StringBuilder();
            String temp = null;

            while ((temp = br.readLine()) != null) {
                sb1.append(temp + "\n");
            }
            String encryptedtext1 = encryption(sb1.toString());
            file1 = new File(pathf + "//" + filename);

            System.out.println("\n\nCheck Point----->   " + encryptedtext1);
            FileWriter fw = new FileWriter(file1);
            fw.write(encryptedtext1);
            fw.close();
            
            System.out.println("file.getName() : " + filename);
           

            try {
                String sql = "insert into uploads(dpid, dpname, kword, upfile, time, enkey, fname, ftype, fstatus) values (?, ?, ?, ?, ?, ?,?,?,?)";
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.setString(1, dpid);
                statement.setString(2, dpname);
                statement.setString(3, kword);
                statement.setString(4, encryptedtext1);
                statement.setString(5, time);
                statement.setString(6, val);
                statement.setString(7, filename);
                statement.setString(8, ftype);
                statement.setString(9, "Waiting");
                int row = statement.executeUpdate();
                System.out.println("Upload.files.Upload.processRequest()" + row);
                if (row > 0) {

                    response.sendRedirect("FileUpload.jsp?Success");

                } else {
                    response.sendRedirect("FileUpload.jsp?Failed");

                }
            } catch (SQLException ex) {
                Logger.getLogger(Upload.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (Exception ex) {
            Logger.getLogger(Upload.class.getName()).log(Level.SEVERE, null, ex);
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
