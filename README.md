# Spring Boot Kafka TestContainers Reusable Mode

[![Java CI with Maven](https://github.com/hendisantika/spring-boot-kafka-testcontainers-reusable-mode/actions/workflows/maven.yml/badge.svg)](https://github.com/hendisantika/spring-boot-kafka-testcontainers-reusable-mode/actions/workflows/maven.yml)
![Java](https://img.shields.io/badge/Java-25-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-6DB33F?logo=springboot)
![Testcontainers](https://img.shields.io/badge/Testcontainers-2.x-2496ED?logo=docker)
![Dependabot](https://img.shields.io/badge/Dependabot-enabled-025E8C?logo=dependabot)

This project demonstrates how to use TestContainers with Kafka in reusable mode for Spring Boot applications. It
consists of a producer and consumer application that communicate through Kafka.

## Tech Stack

| Component      | Version                                   |
|----------------|-------------------------------------------|
| Java           | 25 (Temurin in CI)                        |
| Spring Boot    | 4.1.x (`spring-boot-starter-kafka`)       |
| Apache Kafka   | 4.x client (managed by Spring Boot)       |
| Testcontainers | 2.x (`testcontainers-kafka`)              |
| Maven          | 3.10.x via the Maven Wrapper (`./mvnw`)   |
| Kafka image    | `confluentinc/cp-kafka:7.4.0`             |

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
   static ConfluentKafkaContainer kafkaContainer = new ConfluentKafkaContainer(
           DockerImageName.parse("confluentinc/cp-kafka:7.4.0"))
           .withReuse(true);

   @DynamicPropertySource
   static void kafkaProperties(DynamicPropertyRegistry registry) {
      registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
   }
}
```

The key element is `.withReuse(true)` which enables the reusable mode.

> **Note:** Testcontainers 2.x removed the legacy `org.testcontainers.containers.KafkaContainer`. Use
> `org.testcontainers.kafka.ConfluentKafkaContainer` for `confluentinc/cp-kafka` images or
> `org.testcontainers.kafka.KafkaContainer` for `apache/kafka` images.

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

- Java 25
- Docker
- Maven is optional; each module ships with the Maven Wrapper (`./mvnw`)

### Enabling Reusable Mode

`.withReuse(true)` only takes effect when reuse is enabled on your machine. Add the following line to
`~/.testcontainers.properties`:

```properties
testcontainers.reuse.enable=true
```

Without this flag (for example on CI), containers are started and stopped normally.

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

   Alternatively, start the consumer together with a reusable Kafka container (Approach 3):
   ```
   cd consumer
   ./mvnw spring-boot:test-run
   ```

### Running Tests

Run the tests in each module (Docker must be running):

```
cd producer && ./mvnw verify
cd ../consumer && ./mvnw verify
```

## CI/CD & Dependency Management

- **GitHub Actions** (`.github/workflows/maven.yml`): builds and tests `producer` and `consumer` as a matrix on
  Temurin JDK 25 using the Maven Wrapper. Surefire reports are uploaded when a build fails, and the Maven dependency
  graph is submitted to GitHub on every push to `main`.
- **Dependabot** (`.github/dependabot.yml`): checks Maven (both modules) and GitHub Actions weekly on Monday at
  05:00 Asia/Jakarta. Updates are grouped (`spring`, `testcontainers`, `maven-plugins`, `others`, `github-actions`),
  use conventional commit prefixes (`build(deps)`, `ci(deps)`), and wait for a short cooldown after new releases.

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
