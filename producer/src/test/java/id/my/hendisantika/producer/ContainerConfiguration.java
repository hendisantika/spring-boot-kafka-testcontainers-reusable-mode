package id.my.hendisantika.producer;

import org.junit.runner.Description;
import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.Network;
import org.testcontainers.images.builder.dockerfile.statement.Statement;
import org.testcontainers.kafka.ConfluentKafkaContainer;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-kafka-testcontainers-reusable-mode2
 * User: hendisantika
 * Link: s.id/hendisantika
 * Email: hendisantika@yahoo.co.id
 * Telegram : @hendisantika34
 * Date: 21/04/25
 * Time: 19.07
 * To change this template use File | Settings | File Templates.
 */
@TestConfiguration(proxyBeanMethods = false)
public class ContainerConfiguration {
    private static final String KAFKA_NETWORK = "kafka-network";

    Network network = getNetwork();

    static Network getNetwork() {
        Network defaultDaprNetwork = new Network() {
            @Override
            public String getId() {
                return KAFKA_NETWORK;
            }

            @Override
            public void close() {

            }

            @Override
            public Statement apply(Statement base, Description description) {
                return null;
            }
        };

        List<Network> networks = DockerClientFactory.instance().client().listNetworksCmd().withNameFilter(KAFKA_NETWORK).exec();
        if (networks.isEmpty()) {
            Network.builder()
                    .createNetworkCmdModifier(cmd -> cmd.withName(KAFKA_NETWORK))
                    .build().getId();
            return defaultDaprNetwork;
        } else {
            return defaultDaprNetwork;
        }
    }

    @Bean
    @ServiceConnection
    @RestartScope
    ConfluentKafkaContainer kafkaContainer() {
        return new ConfluentKafkaContainer("confluentinc/cp-kafka:7.4.0")
                .withListener("kafka:19092")
                .withNetwork(network)
                .withReuse(true);
    }

    @Bean
    @DependsOn("kafkaContainer")
    GenericContainer<?> controlCenter() {
        GenericContainer<?> schemaRegistry = new GenericContainer<>("confluentinc/cp-schema-registry:7.4.0")
                .withExposedPorts(8085)
                .withNetworkAliases("schemaregistry")
                .withNetwork(network)
                .withEnv("SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS", "PLAINTEXT://kafka:19092")
                .withEnv("SCHEMA_REGISTRY_LISTENERS", "http://0.0.0.0:8085")
                .withEnv("SCHEMA_REGISTRY_HOST_NAME", "schemaregistry")
                .withEnv("SCHEMA_REGISTRY_KAFKASTORE_SECURITY_PROTOCOL", "PLAINTEXT")
                .waitingFor(Wait.forHttp("/subjects"))
                .withStartupTimeout(Duration.of(120, ChronoUnit.SECONDS));

        GenericContainer<?> ksqldb = new GenericContainer<>("confluentinc/cp-ksqldb-server:7.4.0")
                .withExposedPorts(8088)
                .withNetwork(network)
                .withNetworkAliases("ksqldb")
                .withEnv("KSQL_LISTENERS", "http://0.0.0.0:8088")
                .withEnv("KSQL_KSQL_SERVICE_ID", "ksqldb-server")
                .withEnv("KSQL_BOOTSTRAP_SERVERS", "kafka:19092")
                .withEnv("KSQL_KSQL_SCHEMA_REGISTRY_URL", "http://schemaregistry:8085");

        GenericContainer<?> connect = new GenericContainer("confluentinc/cp-server-connect:7.4.0") {
            @Override
            protected void containerIsStarting(InspectContainerResponse containerInfo) {
                try {
                    execInContainer("confluent-hub", "install", "--no-prompt", "confluentinc/kafka-connect-s3:latest");
                    execInContainer("confluent-hub", "install", "--no-prompt", "confluentinc/kafka-connect-jdbc:latest");
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException("Error downloading connectors", e);
                }

            }
        }

    }
