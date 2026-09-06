/*
 */
package SACS;

/**
 *
 * @author Murthi
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLconnection {
   public static Connection getconnection() {
       String url = getSetting("SACS_DB_URL", "jdbc:mariadb://localhost:3306/sacs");
       String user = getSetting("SACS_DB_USER", "root");
       String password = getSetting("SACS_DB_PASSWORD", null);

       if (password == null) {
           throw new IllegalStateException("SACS_DB_PASSWORD is not configured");
       }

       try {
           Class.forName("org.mariadb.jdbc.Driver");
           return DriverManager.getConnection(url, user, password);
       } catch (ClassNotFoundException | SQLException e) {
           throw new IllegalStateException("Unable to connect to the SACS database", e);
       }
   }

   private static String getSetting(String name, String defaultValue) {
       String value = System.getProperty(name);
       if (value == null || value.trim().isEmpty()) {
           value = System.getenv(name);
       }
       return value == null || value.trim().isEmpty() ? defaultValue : value;
   }
}
