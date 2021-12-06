package com.example.junitstuff;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.test.RabbitListenerTest;
import org.springframework.amqp.rabbit.test.TestRabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;

@Configuration
@RabbitListenerTest(capture = true)
@Slf4j
public class TestConfig {

    @Primary
    @Bean
    public RabbitTemplate testRabbitTemplate(final ConnectionFactory connectionFactory, MessageConverter amqpJackson2MessageConverter, MessagePostProcessor amqpMessageWrapper) throws IOException {
        TestRabbitTemplate rabbitTemplate = new TestRabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(amqpJackson2MessageConverter);
        rabbitTemplate.setBeforePublishPostProcessors(amqpMessageWrapper);
        return rabbitTemplate;
    }

    //@Primary
    //@Bean
    public ConnectionFactory mockConnectionFactory() throws IOException {
        ConnectionFactory factory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        Channel channel = mock(Channel.class);
        willReturn(connection).given(factory).createConnection();
        willReturn(channel).given(connection).createChannel(anyBoolean());
        given(channel.isOpen()).willReturn(true);
        return factory;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory mockConnectionFactory) throws IOException {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(mockConnectionFactory);
        return factory;
    }

    private ObjectMapper rabbitObjectMapper(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setDateFormat(new StdDateFormat());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        return mapper;
    }


    //@Bean
    public MessageConverter amqpJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter(rabbitObjectMapper());
    }

    //@Bean
    public MessagePostProcessor standaloneAmqpMessageWrapper(){
        log.info("Initializing StandaloneAmqpMessageWrapper");
        return new CustomMessagePostProcessor();
    }

    //@Bean
    public ThingListener thingListener(){
        return new ThingListener();
    }

}
