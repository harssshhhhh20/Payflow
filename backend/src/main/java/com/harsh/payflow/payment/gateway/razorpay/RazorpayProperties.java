package com.harsh.payflow.payment.gateway.razorpay;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "razorpay")
public class RazorpayProperties {

    private String keyId;

    private String webhookSecret;

    private String keySecret;

}