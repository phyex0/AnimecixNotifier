package com.phyex.animecixnotifier.util;

import lombok.NoArgsConstructor;

import java.security.SecureRandom;

@NoArgsConstructor
public class XehRandomGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                    "abcdefghijklmnopqrstuvwxyz" +
                    "0123456789";

    public static String generate() {
        return randomString(60) + "." + randomString(20);
    }

    private static String randomString(int length) {
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }

        return sb.toString();
    }
}
