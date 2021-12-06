package com.example.junitstuff;

import com.example.junitstuff.model.Thing;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.test.RabbitListenerTestHarness;
import org.springframework.amqp.rabbit.test.TestRabbitTemplate;
import org.springframework.amqp.rabbit.test.context.SpringRabbitTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.ZonedDateTime;
import java.util.UUID;

import static com.example.junitstuff.RabbitConfiguration.*;
import static org.mockito.Mockito.verify;

/**
 * This is supposed to verify that the listener got the message and also verify that the special header is present and
 * contains a certain value.
 */
@Slf4j
//@SpringBootTest(classes = {TestConfig.class, JunitStuffApplication.class})
@SpringRabbitTest
@SpringJUnitConfig
@ContextConfiguration(classes = {TestConfig.class, RabbitConfiguration.class})
@EnableRabbit
class RabbitTests {
    private TenantContext tenantContext = new TenantContext();

    @Autowired
    private RabbitListenerTestHarness harness;

    @Autowired
    private TestRabbitTemplate template;


    @Test
    void test(){
        Thing t = new Thing(UUID.randomUUID(),"Luigi", ZonedDateTime.now());
        log.info("Sending message, Exchange:{}, Routing Key:{}, Object:{}",EXCHANGE_NAME,ROUTING_KEY,t);
        try {
            tenantContext.setTenantId("ACME");
            template.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, t);
        }finally {
            tenantContext.clear();
        }
        ThingListener thingListener = harness.getSpy(LISTENER_ID);
        verify(thingListener).receiveWidget("ACME",t);
    }
}