package com.example.junitstuff;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Only declare things here that shouldn't be contained in the TestConfiguration
 */
@Slf4j
@Configuration
public class RabbitConfiguration {
    public static final String EXCHANGE_NAME = "events";
    public static final String QUEUE_NAME = "my-queue";
    public static final String ROUTING_KEY = "thinghappened";
    public static final String LISTENER_ID = "thing-listener";
    public static final String TENANT_HEADER = "X-Tenant-Id";


    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory, MessageConverter amqpMessageConverter, MessagePostProcessor amqpMessageWrapper) {
        log.info("Setting up Standalone RabbitTemplate bean");
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(amqpMessageConverter);
        rabbitTemplate.setBeforePublishPostProcessors(amqpMessageWrapper);
        return rabbitTemplate;
    }

    @Bean
    Queue queue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME,true,false);
    }

    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }


}
