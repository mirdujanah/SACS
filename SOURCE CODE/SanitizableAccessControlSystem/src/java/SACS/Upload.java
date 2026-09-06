package SACS;

import com.oreilly.servlet.MultipartRequest;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@MultipartConfig(maxFileSize = 1048576)
public class Upload extends HttpServlet {

    public static final String ALGO = "AES";
    public static byte[] keyValue;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        MultipartRequest multipart = new MultipartRequest(request, Storage.directory("incoming").getPath());
        File uploaded = multipart.getFile("upfile");
        HttpSession session = request.getSession(true);
        String dpid = (String) session.getAttribute("dpid");
        String dpname = (String) session.getAttribute("dpname");
        String keyword = multipart.getParameter("kword");
        if (uploaded == null || dpid == null || dpname == null) {
            response.sendRedirect("FileUpload.jsp?Failed");
            return;
        }

        String filename = uploaded.getName();
        int extensionIndex = filename.lastIndexOf('.');
        String extension = extensionIndex > 0 ? filename.substring(extensionIndex + 1) : "";
        String time = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                .format(new java.util.Date());
        String secret = randomSecret(16, "abcdefghijklmnopqrstuvxyz123456789");
        keyValue = secret.getBytes(StandardCharsets.UTF_8);

        StringBuilder contents = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(uploaded))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contents.append(line).append('\n');
            }
        }
        String encrypted;
        try {
            encrypted = encryption(contents.toString());
        } catch (GeneralSecurityException ex) {
            throw new ServletException("Unable to encrypt upload", ex);
        }
        File destination = Storage.file(Storage.directory(dpid), filename);
        java.nio.file.Files.write(destination.toPath(), encrypted.getBytes(StandardCharsets.UTF_8));

        try (Connection conn = SQLconnection.getconnection();
                PreparedStatement insert = conn.prepareStatement(
                        "INSERT INTO uploads (dpid, dpname, kword, upfile, time, enkey, fname, ftype, fstatus)"
                        + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            insert.setString(1, dpid);
            insert.setString(2, dpname);
            insert.setString(3, keyword);
            insert.setString(4, encrypted);
            insert.setString(5, time);
            insert.setString(6, secret);
            insert.setString(7, filename);
            insert.setString(8, extension);
            insert.setString(9, "Waiting");
            response.sendRedirect(insert.executeUpdate() > 0
                    ? "FileUpload.jsp?Success" : "FileUpload.jsp?Failed");
        } catch (SQLException ex) {
            throw new ServletException("Unable to save upload metadata", ex);
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
        return "Uploads and encrypts a file";
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
