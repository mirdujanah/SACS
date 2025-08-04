/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package SACS;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Murthi
 */
public class ReceiversActivate extends HttpServlet {
public static final String PUBLIC_KEY_FILE = "Public.key";
    public static final String PRIVATE_KEY_FILE = "Private.key";

    //Exceution times Encrypt
    public long encryt_extime = 0;
    public long dcrypt_extime = 0;

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
            String uid = request.getParameter("uid");
            String path = "D://Receiver";
            String pathf = path + uid;

            Statement st = null;
            Connection conn = SQLconnection.getconnection();
            Statement sto = conn.createStatement();
            st = conn.createStatement();

            try {
                int i = sto.executeUpdate("update datareceiver set ustatus='Active' where id='" + uid + "'");
                System.out.println("test print==" + uid);
                if (i != 0) {
                    ResultSet rs = st.executeQuery(" SELECT * from datareceiver where id = '" + uid + "' ");
                    if (rs.next()) {
                        String email = rs.getString("email");
                        System.out.println("-------GENRATE PUBLIC and PRIVATE KEY-------------");
                        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
                        keyPairGenerator.initialize(1024); //1024 used for normal securities

                        long sTime = System.nanoTime();
                        KeyPair keyPair = keyPairGenerator.generateKeyPair();
                        long kygen_extime = (System.nanoTime() - sTime) / 1000000;

                        PublicKey publicKey = keyPair.getPublic();
                        PrivateKey privateKey = keyPair.getPrivate();
                        System.out.println("Public Key --------- " + publicKey);
                        System.out.println("Private Key -------- " + privateKey);
                        System.out.println("KGen  Performance : " + kygen_extime + " msec");

                        //Pullingout parameters which makes up Key
                        System.out.println("\n------- PULLING OUT PARAMETERS WHICH MAKES KEYPAIR----------\n");
                        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                        RSAPublicKeySpec rsaPubKeySpec = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
                        RSAPrivateKeySpec rsaPrivKeySpec = keyFactory.getKeySpec(privateKey, RSAPrivateKeySpec.class);
                        System.out.println("PubKey Modulus : " + rsaPubKeySpec.getModulus());
                        System.out.println("PubKey Exponent : " + rsaPubKeySpec.getPublicExponent());
                        System.out.println("PrivKey Modulus : " + rsaPrivKeySpec.getModulus());
                        System.out.println("PrivKey Exponent : " + rsaPrivKeySpec.getPrivateExponent());

                        //Share public key with other so they can encrypt data and decrypt thoses using private key(Don't share with Other)
                        System.out.println("\n--------SAVING PUBLIC KEY AND PRIVATE KEY TO FILES-------\n");
                        DPActivate rsaObj = new DPActivate();
                        rsaObj.saveKeys(PUBLIC_KEY_FILE, rsaPubKeySpec.getModulus(), rsaPubKeySpec.getPublicExponent(), pathf);
                        rsaObj.saveKeys(PRIVATE_KEY_FILE, rsaPrivKeySpec.getModulus(), rsaPrivKeySpec.getPrivateExponent(), pathf);

                        //Encrypt Data using Public Key
                        // byte[] encryptedData = rsaObj.encryptData(email, pathf);
                        byte[] IBE = rsaObj.encryptData(email, pathf);

                        String strin = new String(IBE);

                        //------------------------------------------------------------------------------------
                        //String IBE = e.encrypt(sb.toString());
                      
                            File myObj = new File(pathf+ "//" + "parameter.txt");
                            if (myObj.createNewFile()) {
                                System.out.println("File created: " + myObj.getName());
                            } else {
                                System.out.println("File already exists.");
                            }

                    //storing encrypted file
                    FileWriter fw = new FileWriter(pathf+ "//" + "parameter.txt");
                    fw.write(strin);
                    fw.close();
                    System.out.println("string Buffer" + strin);
                    //Secret Key
                    Random RANDOM = new SecureRandom();
                    int PASSWORD_LENGTH = 10;
                    String letters = "89POIUYTR234567EWQMNBVCXZHJKL1234567GFDSA189";

                    for (int k = 0; k < PASSWORD_LENGTH; k++) {
                        int index = (int) (RANDOM.nextDouble() * letters.length());
                        letters.substring(index, index + 1);
                    }

                    // date and Time
                    DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss ");
                    Date date = new Date();
                    String time = dateFormat.format(date);
                    System.out.println("current Date " + time);

                    String len = strin.length() + "";
                    System.out.println("Length---------- " + len);
                    
                    String sqlUpdate = "Update datareceiver set vparam = ? WHERE id = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sqlUpdate);
                     pstmt.setBytes(1, IBE);
                    pstmt.setString(2, uid);
                    int rowAffected = pstmt.executeUpdate();
                    System.out.println("rowAffected---> " + rowAffected);
                    response.sendRedirect("ReceiverAct.jsp?Granted");
                } else {

                    System.out.println("failed");
                    response.sendRedirect("ReceiverAct.jsp?Failed");
                }
            }
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | SQLException ex) {
        }
    }
    catch (SQLException ex

    
        ) {
            Logger.getLogger(DPActivate.class.getName()).log(Level.SEVERE, null, ex);
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

    /**
     * Save Files
     *
     * @param fileName
     * @param mod
     * @param exp
     * @return
     * @throws IOException
     */
    public String saveKeys(String fileName, BigInteger mod, BigInteger exp, String pathf) throws IOException {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;

        try {

            File newFolder = new File(pathf);

            boolean created = newFolder.mkdirs();

            if (created) {
                System.out.println("Folder was created !");
            } else {
                System.out.println("Unable to create folder");
            }

            System.out.println("Generating " + fileName + "...");
            fos = new FileOutputStream(pathf + "//" + fileName);
            oos = new ObjectOutputStream(new BufferedOutputStream(fos));

            oos.writeObject(mod);
            oos.writeObject(exp);

            System.out.println(fileName + " generated successfully");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (oos != null) {
                oos.close();

                if (fos != null) {
                    fos.close();
                }
            }
        }
        return null;
    }

    /**
     * Encrypt Data
     *
     * @param data
     * @throws IOException
     */
    public byte[] encryptData(String data, String pathf) throws IOException {
        System.out.println("\n----------------ENCRYPTION STARTED------------");

        System.out.println("Data Before Encryption :" + data);
        long sTime = System.nanoTime();
        byte[] dataToEncrypt = data.getBytes();
        byte[] encryptedData = null;
        try {
            PublicKey pubKey = readPublicKeyFromFile(PUBLIC_KEY_FILE, pathf);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            encryptedData = cipher.doFinal(dataToEncrypt);
            System.out.println("Encryted Data: " + encryptedData);

        } catch (Exception e) {
            e.printStackTrace();
        }
        encryt_extime = (System.nanoTime() - sTime) / 1000000;
        System.out.println("----------------ENCRYPTION COMPLETED------------");
        System.out.println("Enc  Performance : " + encryt_extime + " msec");
        return encryptedData;
    }

    /**
     * Encrypt Data
     *
     * @param data
     * @return
     * @throws IOException
     */
    public String decryptData(byte[] data, String pathf) throws IOException {
        System.out.println("\n----------------DECRYPTION STARTED------------");
        long sTime = System.nanoTime();
        byte[] descryptedData = null;

        try {
            PrivateKey privateKey = readPrivateKeyFromFile(PRIVATE_KEY_FILE, pathf);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            descryptedData = cipher.doFinal(data);
            System.out.println("Decrypted Data: " + new String(descryptedData));

        } catch (Exception e) {
            e.printStackTrace();
        }
        dcrypt_extime = (System.nanoTime() - sTime) / 1000000;
        System.out.println("----------------DECRYPTION COMPLETED------------");
        System.out.println("Dec  Performance : " + dcrypt_extime + " msec");
        return null;
    }

    /**
     * read Public Key From File
     *
     * @param fileName
     * @return PublicKey
     * @throws IOException
     */
    public PublicKey readPublicKeyFromFile(String fileName, String pathf) throws IOException {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(new File(pathf + "//" + fileName));
            ois = new ObjectInputStream(fis);

            BigInteger modulus = (BigInteger) ois.readObject();
            BigInteger exponent = (BigInteger) ois.readObject();

            //Get Public Key
            RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(modulus, exponent);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PublicKey publicKey = fact.generatePublic(rsaPublicKeySpec);

            return publicKey;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ois != null) {
                ois.close();
                if (fis != null) {
                    fis.close();
                }
            }
        }
        return null;
    }

    /**
     * read Public Key From File
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public PrivateKey readPrivateKeyFromFile(String fileName, String pathf) throws IOException {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(new File(pathf + "//" + fileName));
            ois = new ObjectInputStream(fis);

            BigInteger modulus = (BigInteger) ois.readObject();
            BigInteger exponent = (BigInteger) ois.readObject();

            //Get Private Key
            RSAPrivateKeySpec rsaPrivateKeySpec = new RSAPrivateKeySpec(modulus, exponent);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = fact.generatePrivate(rsaPrivateKeySpec);

            System.out.println("Check private key----- " + privateKey);

            return privateKey;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ois != null) {
                ois.close();
                if (fis != null) {
                    fis.close();
                }
            }
        }
        return null;
    }

}