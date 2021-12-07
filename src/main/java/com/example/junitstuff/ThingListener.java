package com.example.junitstuff;

import com.example.junitstuff.model.Thing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;

import static com.example.junitstuff.RabbitConfiguration.*;

@Slf4j
public class ThingListener {
    public ThingListener(){
        log.info("Constructor for Thing Listener");
    }

    //@RabbitListener(id=LISTENER_ID,queues = {QUEUE_NAME})
    @RabbitListener(id=LISTENER_ID,
        bindings = @QueueBinding(
            value=@Queue(QUEUE_NAME),
            exchange = @Exchange(EXCHANGE_NAME),
            key = ROUTING_KEY
        )
    )
    public void receiveWidget(@Header(TENANT_HEADER) String tenantId, Thing thing){
        log.info("Thing Received! Tenant:'{}' Thing= {}",tenantId,thing);
    }
}
