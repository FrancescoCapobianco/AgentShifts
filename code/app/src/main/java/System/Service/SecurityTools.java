package System.Service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class SecurityTools {

    private static final String HASH_ALGORITHM = "SHA-256";

    // Salt Generator: stringa esadecimale di 32 caratteri
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);

        StringBuilder sb = new StringBuilder();
        for (byte b : saltBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // Hashing Password:
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest hashFunction = MessageDigest.getInstance(HASH_ALGORITHM);
            String passwordWithSalt = password + salt;
            byte[] digest = hashFunction.digest(passwordWithSalt.getBytes(StandardCharsets.UTF_8));
            return String.format("%064x", new BigInteger(1, digest));

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // Fast-Checking Method:
    public static boolean checkPassword(String inputPassword, String storedSalt, String storedHash) {
        String calculatedHash = hashPassword(inputPassword, storedSalt);
        return calculatedHash.equals(storedHash);
    }

}