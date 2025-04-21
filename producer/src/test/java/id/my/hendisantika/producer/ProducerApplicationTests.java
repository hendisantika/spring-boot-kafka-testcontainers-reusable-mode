package id.my.hendisantika.producer;

import org.springframework.boot.SpringApplication;

class ProducerApplicationTests {

    public static void main(String[] args) {
        SpringApplication.from(ProducerApplication::main)
                .with(ContainerConfiguration.class)
                .run(args);
    }


}
