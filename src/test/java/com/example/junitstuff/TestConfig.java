package com.example.junitstuff;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.test.RabbitListenerTest;
import org.springframework.amqp.rabbit.test.TestRabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

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
    public RabbitTemplate testRabbitTemplate(final ConnectionFactory connectionFactory, MessageConverter amqpJackson2MessageConverter, MessagePostProcessor amqpMessageWrapper) {
        TestRabbitTemplate rabbitTemplate = new TestRabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(amqpJackson2MessageConverter);
        rabbitTemplate.setBeforePublishPostProcessors(amqpMessageWrapper);
        return rabbitTemplate;
    }

    @Bean
    public ConnectionFactory mockConnectionFactory() {
        ConnectionFactory factory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        Channel channel = mock(Channel.class);
        willReturn(connection).given(factory).createConnection();
        willReturn(channel).given(connection).createChannel(anyBoolean());
        given(channel.isOpen()).willReturn(true);
        return factory;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory mockConnectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(mockConnectionFactory);
        return factory;
    }

}
