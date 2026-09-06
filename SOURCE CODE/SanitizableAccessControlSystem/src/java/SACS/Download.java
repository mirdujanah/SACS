package SACS;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class Download extends HttpServlet {

    public static final String ALGO = "AES";
    public static byte[] keyValue;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String fileid = request.getParameter("fid");
        String dkey = request.getParameter("dkey");
        String time = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                .format(new java.util.Date());
        HttpSession session = request.getSession(true);
        String drname = (String) session.getAttribute("drname");
        String drid = (String) session.getAttribute("drid");
        String drmail = (String) session.getAttribute("drmail");

        try (Connection con = SQLconnection.getconnection();
                PreparedStatement lookup = con.prepareStatement(
                        "SELECT fname, enkey, dpid, dpname, upfile FROM uploads WHERE id = ? AND skey = ?");
                PreparedStatement insert = con.prepareStatement(
                        "INSERT INTO downloads (drid, drname, dpid, dpname, fid, fname, time)"
                        + " VALUES (?, ?, ?, ?, ?, ?, ?)");
                PrintWriter out = response.getWriter()) {
            lookup.setString(1, fileid);
            lookup.setString(2, dkey);
            try (ResultSet result = lookup.executeQuery()) {
                if (!result.next()) {
                    response.sendRedirect("RequestedFiles.jsp?InvalidKey");
                    return;
                }
                String fname = result.getString("fname");
                String enkey = result.getString("enkey");
                keyValue = dkey.getBytes(StandardCharsets.UTF_8);
                String encryptedFile = decryption(result.getString("upfile"));
                keyValue = enkey.getBytes(StandardCharsets.UTF_8);
                String text = decryption(encryptedFile);

                insert.setString(1, drid);
                insert.setString(2, drname);
                insert.setString(3, result.getString("dpid"));
                insert.setString(4, result.getString("dpname"));
                insert.setString(5, fileid);
                insert.setString(6, fname);
                insert.setString(7, time);
                insert.executeUpdate();

                response.setContentType("text/plain;charset=UTF-8");
                response.setHeader("Content-Disposition",
                        "attachment;filename=\"" + headerFilename(fname) + "\"");
                out.write(text);
                out.flush();
            }
        } catch (SQLException ex) {
            throw new ServletException("Unable to download file", ex);
        } catch (GeneralSecurityException | IllegalArgumentException ex) {
            throw new ServletException("Unable to decrypt file", ex);
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
        return "Downloads an approved file";
    }

    public static String encryption(String data) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, generateKey());
        return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }

    public static String decryption(String encryptedData) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.DECRYPT_MODE, generateKey());
        return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedData)), StandardCharsets.UTF_8);
    }

    public static Key generateKey() {
        return new SecretKeySpec(keyValue, ALGO);
    }

    private static String headerFilename(String name) {
        return name == null ? "download" : name.replace("\"", "").replace("\r", "").replace("\n", "");
    }

    static void readWrite(RandomAccessFile raf, BufferedOutputStream bw, long numBytes) throws IOException {
        byte[] buf = new byte[(int) numBytes];
        int read = raf.read(buf);
        if (read != -1) {
            bw.write(buf, 0, read);
        }
    }
}
