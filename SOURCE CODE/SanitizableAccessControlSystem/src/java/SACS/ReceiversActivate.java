package SACS;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ReceiversActivate extends DPActivate {

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String uid = request.getParameter("uid");
        try (Connection conn = SQLconnection.getconnection();
                PreparedStatement activate = conn.prepareStatement(
                        "UPDATE datareceiver SET ustatus = 'Active' WHERE id = ?");
                PreparedStatement lookup = conn.prepareStatement(
                        "SELECT email FROM datareceiver WHERE id = ?")) {
            activate.setString(1, uid);
            if (activate.executeUpdate() == 0) {
                response.sendRedirect("ReceiverAct.jsp?Failed");
                return;
            }
            lookup.setString(1, uid);
            try (ResultSet result = lookup.executeQuery()) {
                if (!result.next()) {
                    response.sendRedirect("ReceiverAct.jsp?Failed");
                    return;
                }
                File directory = Storage.directory("Receiver", uid);
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
                        new String(encrypted, java.nio.charset.StandardCharsets.UTF_8)
                                .getBytes(java.nio.charset.StandardCharsets.UTF_8));
                try (PreparedStatement update = conn.prepareStatement(
                        "UPDATE datareceiver SET vparam = ? WHERE id = ?")) {
                    update.setBytes(1, encrypted);
                    update.setString(2, uid);
                    update.executeUpdate();
                }
                response.sendRedirect("ReceiverAct.jsp?Granted");
            }
        } catch (SQLException ex) {
            throw new ServletException("Unable to activate receiver", ex);
        } catch (GeneralSecurityException | ClassNotFoundException ex) {
            throw new ServletException("Unable to generate receiver keys", ex);
        }
    }
}
