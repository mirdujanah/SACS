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

public class Download1 extends HttpServlet {

    public static final String ALGO = "AES";
    public static byte[] keyValue;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String fileid = request.getParameter("fid");
        String dkey = request.getParameter("dkey");

        try (Connection con = SQLconnection.getconnection();
                PreparedStatement lookup = con.prepareStatement(
                        "SELECT fname, skey, dpid, dpname, upfile FROM uploads WHERE id = ? AND enkey = ?");
                PrintWriter out = response.getWriter()) {
            lookup.setString(1, fileid);
            lookup.setString(2, dkey);
            try (ResultSet result = lookup.executeQuery()) {
                if (!result.next()) {
                    response.sendRedirect("MyFiles.jsp?InvalidKey");
                    return;
                }
                String fname = result.getString("fname");
                keyValue = result.getString("skey").getBytes(StandardCharsets.UTF_8);
                String encryptedFile = decryption(result.getString("upfile"));
                keyValue = dkey.getBytes(StandardCharsets.UTF_8);
                String text = decryption(encryptedFile);

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
        return "Downloads a publisher file";
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
