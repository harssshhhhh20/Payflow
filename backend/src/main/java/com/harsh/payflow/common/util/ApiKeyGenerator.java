package com.harsh.payflow.common.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class ApiKeyGenerator {

    private static final String PREFIX = "pf_live_";
    private static final String CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static final SecureRandom RANDOM = new SecureRandom();

    public String generate() {

        StringBuilder builder = new StringBuilder(PREFIX);

        for (int i = 0; i < 32; i++) {
            builder.append(
                    CHARACTERS.charAt(
                            RANDOM.nextInt(CHARACTERS.length())
                    )
            );
        }

        return builder.toString();
    }

}