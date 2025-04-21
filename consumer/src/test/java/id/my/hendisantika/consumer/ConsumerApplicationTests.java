package id.my.hendisantika.consumer;

import org.springframework.boot.SpringApplication;

class ConsumerApplicationTests {

    public static void main(String[] args) {
        SpringApplication.from(ConsumerApplication::main)
                .with(ContainerConfiguration.class)
                .run(args);
    }

}
