package com.harsh.payflow.payment.webhook;

import com.harsh.payflow.common.config.WebhookProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
public class WebhookController {

    private final RazorpaySignatureVerifier signatureVerifier;
    private final WebhookService webhookService;
    private final WebhookProperties webhookProperties;

    @PostMapping("/razorpay")
    public ResponseEntity<Void> handleRazorpayWebhook(
            @RequestHeader("X-Razorpay-Signature") String signature,
            @RequestBody String payload
    ) {

        if (webhookProperties.isSignatureVerificationEnabled()
                && !signatureVerifier.verify(payload, signature)) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        webhookService.processWebhook(payload);

        return ResponseEntity.ok().build();
    }
}