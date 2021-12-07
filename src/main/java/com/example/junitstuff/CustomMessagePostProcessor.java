package com.example.junitstuff;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Correlation;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;

import static com.example.junitstuff.RabbitConfiguration.TENANT_HEADER;

@Slf4j
public class CustomMessagePostProcessor implements MessagePostProcessor {

    private final TenantContext tenantContext = new TenantContext();

    @Override
    public Message postProcessMessage(Message message) {
        log.trace("CustomMessagePostProcessor.postProcessMessage [{}]", message.getMessageProperties());
        return setupProperties(message);
    }

    @Override
    public Message postProcessMessage(Message message, Correlation correlation) {
        log.trace("CustomMessagePostProcessor.postProcessMessage [{} , {}]", message.getMessageProperties(), correlation);
        return setupProperties(message);
    }

    private Message setupProperties(Message message) {
        MessageProperties messageProperties = message.getMessageProperties();
        //Put custom header in with the current tenant context
        messageProperties.getHeaders().put(TENANT_HEADER, tenantContext.getTenantId());
        return message;
    }
}
