package com.harsh.payflow.common.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String PAYMENT_EXCHANGE = "payment.exchange";

    public static final String PAYMENT_CREATED_QUEUE = "payment.created.queue";

    public static final String PAYMENT_CAPTURED_QUEUE = "payment.captured.queue";

    public static final String PAYMENT_FAILED_QUEUE = "payment.failed.queue";

    public static final String PAYMENT_RETRY_QUEUE = "payment.retry.queue";

    public static final String PAYMENT_CREATED_ROUTING_KEY = "payment.created";

    public static final String PAYMENT_CAPTURED_ROUTING_KEY = "payment.captured";

    public static final String PAYMENT_FAILED_ROUTING_KEY = "payment.failed";

    public static final String PAYMENT_RETRY_ROUTING_KEY = "payment.retry.initiated";

    public static final String DEAD_LETTER_EXCHANGE = "payment.dlx";

    public static final String PAYMENT_CAPTURED_DLQ = "payment.captured.dlq";

    public static final String PAYMENT_CAPTURED_DLQ_ROUTING_KEY = "payment.captured.dlq";

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter
    ) {

        RabbitTemplate rabbitTemplate =
                new RabbitTemplate(connectionFactory);

        rabbitTemplate.setMessageConverter(converter);

        return rabbitTemplate;
    }
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter
    ) {

        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);

        factory.setDefaultRequeueRejected(false);

        return factory;
    }

    @Bean
    public DirectExchange paymentExchange() {
        return ExchangeBuilder
                .directExchange(PAYMENT_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public Queue paymentCreatedQueue() {
        return QueueBuilder
                .durable(PAYMENT_CREATED_QUEUE)
                .build();
    }

    @Bean
    public Queue paymentCapturedQueue() {
        return QueueBuilder
                .durable(PAYMENT_CAPTURED_QUEUE)
                .deadLetterExchange(DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey(PAYMENT_CAPTURED_DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue paymentFailedQueue() {
        return QueueBuilder
                .durable(PAYMENT_FAILED_QUEUE)
                .build();
    }

    @Bean
    public Queue paymentRetryQueue() {
        return QueueBuilder
                .durable(PAYMENT_RETRY_QUEUE)
                .build();
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return ExchangeBuilder
                .directExchange(DEAD_LETTER_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public Queue paymentCapturedDeadLetterQueue() {
        return QueueBuilder
                .durable(PAYMENT_CAPTURED_DLQ)
                .build();
    }

    @Bean
    public Binding paymentCreatedBinding(
            Queue paymentCreatedQueue,
            DirectExchange paymentExchange
    ) {

        return BindingBuilder
                .bind(paymentCreatedQueue)
                .to(paymentExchange)
                .with(PAYMENT_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding paymentCapturedBinding(
            Queue paymentCapturedQueue,
            DirectExchange paymentExchange
    ) {

        return BindingBuilder
                .bind(paymentCapturedQueue)
                .to(paymentExchange)
                .with(PAYMENT_CAPTURED_ROUTING_KEY);
    }

    @Bean
    public Binding paymentFailedBinding(
            Queue paymentFailedQueue,
            DirectExchange paymentExchange
    ) {

        return BindingBuilder
                .bind(paymentFailedQueue)
                .to(paymentExchange)
                .with(PAYMENT_FAILED_ROUTING_KEY);
    }

    @Bean
    public Binding paymentRetryBinding(
            Queue paymentRetryQueue,
            DirectExchange paymentExchange
    ) {

        return BindingBuilder
                .bind(paymentRetryQueue)
                .to(paymentExchange)
                .with(PAYMENT_RETRY_ROUTING_KEY);
    }

    @Bean
    public Binding paymentCapturedDeadLetterBinding(
            Queue paymentCapturedDeadLetterQueue,
            DirectExchange deadLetterExchange
    ) {
        return BindingBuilder
                .bind(paymentCapturedDeadLetterQueue)
                .to(deadLetterExchange)
                .with(PAYMENT_CAPTURED_DLQ_ROUTING_KEY);
    }
}