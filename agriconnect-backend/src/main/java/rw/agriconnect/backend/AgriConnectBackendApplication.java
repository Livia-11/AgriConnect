package rw.agriconnect.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "rw.agriconnect")
@EntityScan("rw.agriconnect.model")
@EnableJpaRepositories("rw.agriconnect.repository")
public class AgriConnectBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgriConnectBackendApplication.class, args);
    }
} 