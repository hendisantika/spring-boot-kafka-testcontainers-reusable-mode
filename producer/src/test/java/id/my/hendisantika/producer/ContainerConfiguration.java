package id.my.hendisantika.producer;

import org.junit.runner.Description;
import org.springframework.boot.test.context.TestConfiguration;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.Network;
import org.testcontainers.images.builder.dockerfile.statement.Statement;

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
}
