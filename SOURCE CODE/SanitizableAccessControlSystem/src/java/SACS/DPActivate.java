package SACS;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.crypto.Cipher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DPActivate extends HttpServlet {

    public static final String PUBLIC_KEY_FILE = "Public.key";
    public static final String PRIVATE_KEY_FILE = "Private.key";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String uid = request.getParameter("uid");
        try (Connection conn = SQLconnection.getconnection();
                PreparedStatement activate = conn.prepareStatement(
                        "UPDATE datapub SET ustatus = 'Active' WHERE id = ?");
                PreparedStatement lookup = conn.prepareStatement(
                        "SELECT email FROM datapub WHERE id = ?")) {
            activate.setString(1, uid);
            if (activate.executeUpdate() == 0) {
                response.sendRedirect("DataPublisherAct.jsp?Failed");
                return;
            }
            lookup.setString(1, uid);
            try (ResultSet result = lookup.executeQuery()) {
                if (!result.next()) {
                    response.sendRedirect("DataPublisherAct.jsp?Failed");
                    return;
                }
                File directory = Storage.directory("DPUB", uid);
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
                generator.initialize(1024);
                KeyPair pair = generator.generateKeyPair();
                KeyFactory factory = KeyFactory.getInstance("RSA");
                RSAPublicKeySpec publicSpec = factory.getKeySpec(pair.getPublic(), RSAPublicKeySpec.class);
                RSAPrivateKeySpec privateSpec = factory.getKeySpec(pair.getPrivate(), RSAPrivateKeySpec.class);
                saveKeys(PUBLIC_KEY_FILE, publicSpec.getModulus(), publicSpec.getPublicExponent(), directory);
                saveKeys(PRIVATE_KEY_FILE, privateSpec.getModulus(), privateSpec.getPrivateExponent(), directory);

                byte[] encrypted = encryptData(result.getString("email"), directory);
                java.nio.file.Files.write(new File(directory, "parameter.txt").toPath(),
                        new String(encrypted, StandardCharsets.UTF_8).getBytes(StandardCharsets.UTF_8));
                try (PreparedStatement update = conn.prepareStatement(
                        "UPDATE datapub SET vparam = ? WHERE id = ?")) {
                    update.setBytes(1, encrypted);
                    update.setString(2, uid);
                    update.executeUpdate();
                }
                response.sendRedirect("DataPublisherAct.jsp?Granted");
            }
        } catch (SQLException ex) {
            throw new ServletException("Unable to activate publisher", ex);
        } catch (GeneralSecurityException | ClassNotFoundException ex) {
            throw new ServletException("Unable to generate publisher keys", ex);
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
        return "Activates a data publisher";
    }

    public void saveKeys(String fileName, BigInteger modulus, BigInteger exponent, File directory)
            throws IOException {
        File keyFile = new File(directory, fileName);
        try (FileOutputStream output = new FileOutputStream(keyFile);
                ObjectOutputStream objectOutput = new ObjectOutputStream(new BufferedOutputStream(output))) {
            objectOutput.writeObject(modulus);
            objectOutput.writeObject(exponent);
        }
    }

    public byte[] encryptData(String data, File directory)
            throws IOException, GeneralSecurityException, ClassNotFoundException {
        PublicKey publicKey = readPublicKeyFromFile(PUBLIC_KEY_FILE, directory);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }

    public String decryptData(byte[] data, File directory)
            throws IOException, GeneralSecurityException, ClassNotFoundException {
        PrivateKey privateKey = readPrivateKeyFromFile(PRIVATE_KEY_FILE, directory);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(data), StandardCharsets.UTF_8);
    }

    public PublicKey readPublicKeyFromFile(String fileName, File directory)
            throws IOException, GeneralSecurityException, ClassNotFoundException {
        try (FileInputStream input = new FileInputStream(new File(directory, fileName));
                ObjectInputStream objectInput = new ObjectInputStream(input)) {
            BigInteger modulus = (BigInteger) objectInput.readObject();
            BigInteger exponent = (BigInteger) objectInput.readObject();
            return KeyFactory.getInstance("RSA")
                    .generatePublic(new RSAPublicKeySpec(modulus, exponent));
        }
    }

    public PrivateKey readPrivateKeyFromFile(String fileName, File directory)
            throws IOException, GeneralSecurityException, ClassNotFoundException {
        try (FileInputStream input = new FileInputStream(new File(directory, fileName));
                ObjectInputStream objectInput = new ObjectInputStream(input)) {
            BigInteger modulus = (BigInteger) objectInput.readObject();
            BigInteger exponent = (BigInteger) objectInput.readObject();
            return KeyFactory.getInstance("RSA")
                    .generatePrivate(new RSAPrivateKeySpec(modulus, exponent));
        }
    }
}
