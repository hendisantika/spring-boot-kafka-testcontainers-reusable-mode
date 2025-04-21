package id.my.hendisantika.producer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Import(ContainerConfiguration.class)
@DirtiesContext
class ProducerApplicationTests {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void contextLoads() {
        // Verify that the application context loads successfully
        assertNotNull(kafkaTemplate, "KafkaTemplate should be autowired");
    }

}
