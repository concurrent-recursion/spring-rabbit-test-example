# spring-rabbit-test-example
Example for Spring Boot + Rabbit + JUnit5 integration testing.

##Pre-Requisites:
- Docker or RabbitMQ installed

##Running application
- Run `docker-compose up -d` to start local instance of rabbitmq
- Run `JunitStuffApplication`, for main application

##For JUnit Tests:
- **No** instance of rabbit should be running
- `JUnitStuffApplicationTests` contains contextLoad() test
- `RabbitTests` contains tests covering RabbitTemplate and RabbitListener integration