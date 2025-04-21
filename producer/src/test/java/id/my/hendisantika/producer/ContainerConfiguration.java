package id.my.hendisantika.producer;

import org.springframework.boot.test.context.TestConfiguration;
import org.testcontainers.containers.Network;

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
}
