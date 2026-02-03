package custom.striker.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for Security related actions
 */
public final class SecurityService {
    private static final String SALT = "&;&";
    private static final String ENCRYPTION = "SHA-256";

    public static String hashString(String string) {
        int midIndex = string.length() / 2;
        StringBuilder hashedString = new StringBuilder();
        hashedString.append(SALT);
        hashedString.append(string, 0, midIndex);
        hashedString.append(SALT);
        hashedString.append(string, midIndex, string.length());
        hashedString.append(SALT);
        try {
            MessageDigest digest = MessageDigest.getInstance(ENCRYPTION);
            byte[] hashBytes = digest.digest(hashedString.toString().getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException ignored) {
            throw new RuntimeException("Encryption algorithm not recognized!");
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
