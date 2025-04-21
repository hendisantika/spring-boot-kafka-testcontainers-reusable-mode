# Spring Boot Kafka TestContainers Reusable Mode

This project demonstrates how to use TestContainers with Kafka in reusable mode for Spring Boot applications. It
consists of a producer and consumer application that communicate through Kafka.

## Project Structure

The project is organized into two modules:

1. **Producer Module**: Sends messages to a Kafka topic every second
2. **Consumer Module**: Listens to the Kafka topic and logs the received messages

## What is TestContainers Reusable Mode?

TestContainers is a Java library that provides lightweight, throwaway instances of common databases, Selenium web
browsers, or anything else that can run in a Docker container. By default, TestContainers creates a new container for
each test class and destroys it after the tests are completed.

**Reusable Mode** allows containers to stay alive between test runs, which significantly speeds up the development and
testing process. Instead of creating and destroying containers for each test run, containers are reused, reducing
startup time and resource usage.

## Benefits of Reusable Mode

1. **Faster Test Execution**: Containers are started once and reused across multiple test runs
2. **Resource Efficiency**: Reduces CPU and memory usage by not repeatedly creating and destroying containers
3. **Improved Developer Experience**: Quicker feedback loop during development
4. **Consistent Test Environment**: All tests use the same container instance, ensuring consistency

## Implementation Approaches

This project demonstrates multiple approaches to implementing TestContainers with Kafka in reusable mode:

### Approach 1: Simple Annotation-based Setup

Used in `ProducerApplicationTests.java`:

```java

@SpringBootTest
@Testcontainers
class ProducerApplicationTests {

   @Container
   static KafkaContainer kafkaContainer = new KafkaContainer(
           DockerImageName.parse("confluentinc/cp-kafka:7.4.0"))
           .withReuse(true);

   @DynamicPropertySource
   static void kafkaProperties(DynamicPropertyRegistry registry) {
      registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
   }
}
```

The key element is `.withReuse(true)` which enables the reusable mode.

### Approach 2: TestConfiguration with ServiceConnection

Used in `ContainerConfiguration.java`:

```java

@TestConfiguration(proxyBeanMethods = false)
public class ContainerConfiguration {
   @Bean
   @ServiceConnection
   @RestartScope
   ConfluentKafkaContainer kafkaContainer() {
      return new ConfluentKafkaContainer("confluentinc/cp-kafka:7.4.0")
              .withListener("kafka:19092")
              .withNetwork(network)
              .withReuse(true);
   }
}
```

This approach uses Spring Boot's `@TestConfiguration` and `@ServiceConnection` for automatic configuration.

### Approach 3: Main Method Configuration

Used in `ConsumerApplicationTests.java`:

```java
public static void main(String[] args) {
   SpringApplication.from(ConsumerApplication::main)
           .with(ContainerConfiguration.class)
           .run(args);
}
```

This approach starts the application with a custom configuration class that provides the reusable containers.

## Additional Features

The project also demonstrates:

1. **Network Configuration**: Setting up a shared network for containers
2. **Confluent Platform Components**: Integration with Schema Registry, KSQLDB, Kafka Connect, REST Proxy, and Control
   Center
3. **Dynamic Property Injection**: Using `@DynamicPropertySource` to inject container properties into Spring context

## How to Run the Project

### Prerequisites

- Java 17 or higher
- Docker

### Running the Applications

1. Start the consumer application:
   ```
   cd consumer
   ./mvnw spring-boot:run
   ```

2. Start the producer application:
   ```
   cd producer
   ./mvnw spring-boot:run
   ```

### Running Tests

1. Run tests with reusable containers:
   ```
   ./mvnw test
   ```

## Configuration

The project uses the following configuration:

- **Producer**: Configured to send messages to "test-topic" every second
- **Consumer**: Configured to listen to "test-topic" with consumer group "test-group"
- **Kafka**: Uses Confluent's Kafka image (confluentinc/cp-kafka:7.4.0)

## Conclusion

TestContainers' reusable mode provides significant benefits for development and testing with Kafka. By keeping
containers alive between test runs, it reduces startup time and resource usage, leading to a more efficient development
process.

This project demonstrates different approaches to implementing reusable mode, allowing you to choose the one that best
fits your needs.
