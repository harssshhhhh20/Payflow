package com.harsh.payflow.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "payflow.webhook")
public class WebhookProperties {

    private boolean signatureVerificationEnabled = true;

}