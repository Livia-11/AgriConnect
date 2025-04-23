package rw.agriconnect.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ComponentScan(basePackages = {
    "rw.agriconnect",
    "rw.agriconnect.backend",
    "rw.agriconnect.config",
    "rw.agriconnect.security"
})
@EntityScan(basePackages = {
    "rw.agriconnect.model",
    "rw.agriconnect.backend.model"
})
@EnableJpaRepositories(basePackages = {
    "rw.agriconnect.repository",
    "rw.agriconnect.backend.repository"
})
@EnableTransactionManagement
public class AgriConnectBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgriConnectBackendApplication.class, args);
    }
} 