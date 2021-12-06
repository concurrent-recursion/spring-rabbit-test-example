package com.example.junitstuff;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;

@Slf4j
@Configuration
public class RabbitConfiguration {
    public static final String EXCHANGE_NAME = "events";
    public static final String QUEUE_NAME = "my-queue";
    public static final String ROUTING_KEY = "thinghappened";
    public static final String LISTENER_ID = "thing-listener";
    public static final String TENANT_HEADER = "X-Tenant-Id";

    @Bean
    public MessagePostProcessor standaloneAmqpMessageWrapper(){
        log.info("Initializing StandaloneAmqpMessageWrapper");
        return new CustomMessagePostProcessor();
    }

    //Custom ObjectMapper for rabbit messages to handle date/time and ignore unmapped properties
    private ObjectMapper rabbitObjectMapper(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setDateFormat(new StdDateFormat());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        return mapper;
    }

    @Bean
    public MessageConverter amqpMessageConverter() {
        return new Jackson2JsonMessageConverter(rabbitObjectMapper());
    }

    @Bean
    public DefaultMessageHandlerMethodFactory myHandlerMethodFactory() {
        DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(rabbitObjectMapper());
        factory.setMessageConverter(converter);
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory, MessageConverter amqpMessageConverter, MessagePostProcessor amqpMessageWrapper) {
        log.info("Setting up Standalone RabbitTemplate bean");
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(amqpMessageConverter);
        rabbitTemplate.setBeforePublishPostProcessors(amqpMessageWrapper);
        return rabbitTemplate;
    }

    @Bean
    public ThingListener thingListener(){
        return new ThingListener();
    }
}
