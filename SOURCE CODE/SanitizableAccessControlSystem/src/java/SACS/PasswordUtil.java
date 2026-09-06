package SACS;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Password hashing compatible with the existing VARCHAR(45) pass columns.
 */
public final class PasswordUtil {

    private static final int SALT_BYTES = 8;
    private static final int HASH_BYTES = 24;
    private static final int ITERATIONS = 120000;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {
    }

    public static String hash(String password) throws GeneralSecurityException {
        byte[] salt = new byte[SALT_BYTES];
        RANDOM.nextBytes(salt);
        byte[] hash = derive(password, salt);
        byte[] encoded = new byte[salt.length + hash.length];
        System.arraycopy(salt, 0, encoded, 0, salt.length);
        System.arraycopy(hash, 0, encoded, salt.length, hash.length);
        return Base64.getEncoder().encodeToString(encoded);
    }

    public static boolean matches(String password, String stored) throws GeneralSecurityException {
        if (password == null || stored == null) {
            return false;
        }
        byte[] encoded;
        try {
            encoded = Base64.getDecoder().decode(stored);
        } catch (IllegalArgumentException ex) {
            return false;
        }
        if (encoded.length != SALT_BYTES + HASH_BYTES) {
            return password.equals(stored);
        }
        byte[] salt = Arrays.copyOfRange(encoded, 0, SALT_BYTES);
        byte[] expected = Arrays.copyOfRange(encoded, SALT_BYTES, encoded.length);
        return MessageDigest.isEqual(expected, derive(password, salt));
    }

    public static boolean isHash(String stored) {
        if (stored == null) {
            return false;
        }
        try {
            return Base64.getDecoder().decode(stored).length == SALT_BYTES + HASH_BYTES;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private static byte[] derive(String password, byte[] salt) throws GeneralSecurityException {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, HASH_BYTES * 8);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } finally {
            spec.clearPassword();
        }
    }
}
