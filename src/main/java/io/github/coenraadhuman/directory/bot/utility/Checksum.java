package io.github.coenraadhuman.directory.bot.utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

public class Checksum {

    private Checksum() {
        throw new RuntimeException("This is a utility class and should not be constructed");
    }

    public static Optional<String> getMD5(String input) {
        try {
            var messageDigest = MessageDigest.getInstance("MD5");
            byte[] digest = messageDigest.digest(input.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                hexString.append(String.format("%02x", b));
            }
            return Optional.of(hexString.toString());

        } catch (NoSuchAlgorithmException e) {
            return Optional.empty();
        }
    }

}
