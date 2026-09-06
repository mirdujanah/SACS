package SACS;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.SecureRandom;
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

public class Sanitize extends HttpServlet {

    public static final String ALGO = "AES";
    public static byte[] keyValue;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String fid = request.getParameter("fid");
        String secret = randomSecret(16, "378AIJKLM5CD4NOP126EFGHB9");
        keyValue = secret.getBytes(StandardCharsets.UTF_8);

        try (Connection conn = SQLconnection.getconnection();
                PreparedStatement lookup = conn.prepareStatement(
                        "SELECT upfile, fname, dpid FROM uploads WHERE id = ?");
                PreparedStatement update = conn.prepareStatement(
                        "UPDATE uploads SET upfile = ?, skey = ?, fstatus = ? WHERE id = ?")) {
            lookup.setString(1, fid);
            try (ResultSet result = lookup.executeQuery()) {
                if (!result.next()) {
                    response.sendRedirect("SanitizeFiles.jsp?Failed");
                    return;
                }
                String filename = result.getString("fname");
                String encrypted = encryption(result.getString("upfile"));
                File directory = Storage.directory(result.getString("dpid"));
                File file = Storage.file(directory, filename);
                java.nio.file.Files.write(file.toPath(), encrypted.getBytes(StandardCharsets.UTF_8));
                new FTPcon().upload(file);

                update.setString(1, encrypted);
                update.setString(2, secret);
                update.setString(3, "Sanitized");
                update.setString(4, fid);
                response.sendRedirect(update.executeUpdate() > 0
                        ? "SanitizeFiles.jsp?Success" : "SanitizeFiles.jsp?Failed");
            }
        } catch (SQLException ex) {
            throw new ServletException("Unable to sanitize file", ex);
        } catch (GeneralSecurityException ex) {
            throw new ServletException("Unable to encrypt sanitized file", ex);
        }
    }

    private static String randomSecret(int length, String alphabet) {
        SecureRandom random = new SecureRandom();
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            result.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }
        return result.toString();
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
        return "Sanitizes an uploaded file";
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

    static void readWrite(RandomAccessFile raf, BufferedOutputStream bw, long numBytes) throws IOException {
        byte[] buf = new byte[(int) numBytes];
        int read = raf.read(buf);
        if (read != -1) {
            bw.write(buf, 0, read);
        }
    }
}
