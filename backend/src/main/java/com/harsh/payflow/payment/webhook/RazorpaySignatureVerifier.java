package com.harsh.payflow.payment.webhook;

import com.harsh.payflow.payment.gateway.razorpay.RazorpayProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class RazorpaySignatureVerifier {

    private final RazorpayProperties razorpayProperties;

    public boolean verify(String payload, String razorpaySignature) {

        try {

            Mac mac = Mac.getInstance("HmacSHA256");

            SecretKeySpec secretKeySpec =
                    new SecretKeySpec(
                            razorpayProperties.getWebhookSecret()
                                    .getBytes(StandardCharsets.UTF_8),
                            "HmacSHA256"
                    );

            mac.init(secretKeySpec);

            byte[] hash =
                    mac.doFinal(
                            payload.getBytes(StandardCharsets.UTF_8)
                    );

            String generatedSignature = bytesToHex(hash);

            return generatedSignature.equals(razorpaySignature);

        } catch (Exception e) {
            return false;
        }
    }

    private String bytesToHex(byte[] bytes) {

        StringBuilder builder = new StringBuilder();

        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }

        return builder.toString();
    }
}