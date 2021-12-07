package com.example.junitstuff;

import com.example.junitstuff.model.Thing;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.ZonedDateTime;
import java.util.UUID;

import static com.example.junitstuff.RabbitConfiguration.EXCHANGE_NAME;
import static com.example.junitstuff.RabbitConfiguration.ROUTING_KEY;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MainApplication implements CommandLineRunner, ExitCodeGenerator {
    private int exitCode = 0;

    private final RabbitTemplate template;
    private final TenantContext tenantContext = new TenantContext();

    public static void main(String[] args) {
        SpringApplication.exit(SpringApplication.run(MainApplication.class, args));
    }

    @Override
    public void run(String... args)  {
        try {
            log.info("Sending Messages");
            //Send messages with different tenants just as a proof-of-concept
            Thing thingy = new Thing(UUID.randomUUID(), "Acme Thing", ZonedDateTime.now());
            try {
                tenantContext.setTenantId("ACME");
                template.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, thingy);
            }finally{
                tenantContext.clear();
            }
            thingy = new Thing(UUID.randomUUID(), "Innotech Thing", ZonedDateTime.now());
            try {
                tenantContext.setTenantId("Innotech");
                template.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, thingy);
            }finally {
                tenantContext.clear();
            }

            thingy = new Thing(UUID.randomUUID(), "Nakatomi Thing", ZonedDateTime.now());
            try {
                tenantContext.setTenantId("Nakatomi");
                template.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, thingy);
            }finally {
                tenantContext.clear();
            }
            Thread.sleep(5000);
        }catch(InterruptedException ie) {
            log.error("Thread interrupted",ie);
            exitCode = 2;
            Thread.currentThread().interrupt();
        }catch(Exception e){
            exitCode = 1;
            log.error("Exception occurred sending messages",e);
        }
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}
