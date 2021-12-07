# spring-rabbit-test-example

Example for Spring Boot + Rabbit + JUnit5 integration testing.

I created this repository because I was having a difficult time finding complete examples for testing Rabbit messaging.
This is a minimal proof of concept that uses a MessagePostProcessor to inject a header into a rabbit message and then tests
the receiver method to make sure that the received message contains the injected header.

It also uses a custom ObjectMapper to un/marshal the JSON payload, including Java 8 time properties.

## Pre-Requisites:

- Docker or RabbitMQ installed

## Running application

- Run `docker-compose up -d` to start local instance of rabbitmq
- Run `MainApplication`, for main application to ensure that the message delivery is working as expected

## For JUnit Tests:

- **No** instance of rabbit should be running
- `RabbitTests` contains tests covering RabbitTemplate and RabbitListener integration
- run `mvn test` to run the JUnit tests