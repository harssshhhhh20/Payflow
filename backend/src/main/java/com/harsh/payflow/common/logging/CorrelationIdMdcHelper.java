package com.harsh.payflow.common.logging;

import com.harsh.payflow.common.messaging.RabbitHeaders;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

@Component
public class CorrelationIdMdcHelper {

    public void populate(Message message) {

        Object correlationId = message.getMessageProperties()
                .getHeaders()
                .get(RabbitHeaders.CORRELATION_ID);

        if (correlationId instanceof String id) {
            MDC.put(CorrelationIdConstants.MDC_KEY, id);
        }
    }

    public void clear() {
        MDC.remove(CorrelationIdConstants.MDC_KEY);
    }
}